import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { requireAuth } from "../../middlewares/auth.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import { HttpError } from "../../utils/httpError.js";

export const notificationsRouter = Router();

notificationsRouter.get(
  "/",
  requireAuth,
  asyncHandler(async (req, res) => {
    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);
    const unreadOnly = String(req.query.unreadOnly ?? "false") === "true";

    const rows = await prisma.notification.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where: {
        userId: req.user!.id,
        ...(unreadOnly ? { readAt: null } : {}),
      },
      orderBy: { id: "desc" },
      select: {
        id: true,
        type: true,
        title: true,
        body: true,
        dataJson: true,
        readAt: true,
        createdAt: true,
      },
    });

    const hasNext = rows.length > limit;
    const items = hasNext ? rows.slice(0, limit) : rows;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    res.json({ items, nextCursor });
  })
);

notificationsRouter.post(
  "/:id/read",
  requireAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    await prisma.notification.update({
      where: { id },
      data: { readAt: new Date() },
    });
    res.status(204).send();
  })
);
