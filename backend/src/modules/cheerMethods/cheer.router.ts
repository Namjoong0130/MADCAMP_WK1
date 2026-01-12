import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import { requireAuth, requireAdmin } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";
import { z } from "zod";

export const cheerRouter = Router();

const CheerCreateSchema = z.object({
  schoolId: z.string().nullable().optional(),
  title: z.string().min(1),
  content: z.string().min(1),
  mediaUrl: z.string().nullable().optional(),
});
const CheerUpdateSchema = CheerCreateSchema.partial();

// GET /cheer-methods (public)
cheerRouter.get(
  "/",
  asyncHandler(async (req, res) => {
    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);
    const schoolId = req.query.schoolId ? String(req.query.schoolId) : undefined;

    const rows = await prisma.cheerMethod.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where: { ...(schoolId ? { schoolId } : {}) },
      orderBy: { id: "desc" },
      include: { school: { select: { id: true, name: true, shortName: true, logoUrl: true } } },
    });

    const hasNext = rows.length > limit;
    const items = hasNext ? rows.slice(0, limit) : rows;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    res.json({ items, nextCursor });
  })
);

// POST /cheer-methods (ADMIN)
cheerRouter.post(
  "/",
  requireAuth,
  requireAdmin,
  asyncHandler(async (req, res) => {
    const body = CheerCreateSchema.parse(req.body);
    const created = await prisma.cheerMethod.create({
      data: {
        schoolId: body.schoolId ?? null,
        title: body.title,
        content: body.content,
        mediaUrl: body.mediaUrl ?? null,
      },
      include: { school: { select: { id: true, name: true, shortName: true, logoUrl: true } } },
    });
    res.status(201).json(created);
  })
);

// GET /cheer-methods/:id (public)
cheerRouter.get(
  "/:id",
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    const item = await prisma.cheerMethod.findUnique({
      where: { id },
      include: { school: { select: { id: true, name: true, shortName: true, logoUrl: true } } },
    });
    if (!item) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    res.json(item);
  })
);

// PATCH /cheer-methods/:id (ADMIN)
cheerRouter.patch(
  "/:id",
  requireAuth,
  requireAdmin,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    const body = CheerUpdateSchema.parse(req.body);

    const updated = await prisma.cheerMethod.update({
      where: { id },
      data: {
        ...(body.schoolId !== undefined ? { schoolId: body.schoolId } : {}),
        ...(body.title !== undefined ? { title: body.title } : {}),
        ...(body.content !== undefined ? { content: body.content } : {}),
        ...(body.mediaUrl !== undefined ? { mediaUrl: body.mediaUrl } : {}),
      },
      include: { school: { select: { id: true, name: true, shortName: true, logoUrl: true } } },
    });

    res.json(updated);
  })
);

// DELETE /cheer-methods/:id (ADMIN)
cheerRouter.delete(
  "/:id",
  requireAuth,
  requireAdmin,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    await prisma.cheerMethod.delete({ where: { id } });
    res.status(204).send();
  })
);
