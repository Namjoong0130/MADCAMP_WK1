import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { HttpError } from "../../utils/httpError.js";

export const schoolsRouter = Router();

// GET /schools (public)
schoolsRouter.get(
  "/",
  asyncHandler(async (_req, res) => {
    const items = await prisma.school.findMany({
      select: { id: true, name: true, shortName: true, logoUrl: true },
      orderBy: { name: "asc" },
    });
    res.json({ items });
  })
);

// GET /schools/:id (public) + include=cheerMethods,schedules
schoolsRouter.get(
  "/:id",
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    const include = String(req.query.include ?? "");
    const includeSet = new Set(include.split(",").map((s) => s.trim()).filter(Boolean));

    const school = await prisma.school.findUnique({
      where: { id },
      select: {
        id: true,
        name: true,
        shortName: true,
        logoUrl: true,
        cheerMethods: includeSet.has("cheerMethods")
          ? { select: { id: true, schoolId: true, title: true, content: true, mediaUrl: true } }
          : false,
        schedulesAsHome: includeSet.has("schedules")
          ? { select: { id: true, title: true, description: true, startAt: true, endAt: true, status: true } }
          : false,
        schedulesAsAway: includeSet.has("schedules")
          ? { select: { id: true, title: true, description: true, startAt: true, endAt: true, status: true } }
          : false,
      },
    });

    if (!school) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    res.json(school);
  })
);
