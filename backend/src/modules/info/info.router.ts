import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import { requireAuth, requireAdmin } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";
import { z } from "zod";

export const infoRouter = Router();

const InfoCreateSchema = z.object({
  categoryId: z.string().nullable().optional(),
  title: z.string().min(1),
  content: z.string().min(1),
  sourceUrl: z.string().nullable().optional(),
  locationName: z.string().nullable().optional(),
  address: z.string().nullable().optional(),
  lat: z.number().nullable().optional(),
  lng: z.number().nullable().optional(),
  tagIds: z.array(z.string()).optional().default([]),
});

const InfoUpdateSchema = InfoCreateSchema.partial();

// GET /info-categories (public)
infoRouter.get(
  "/info-categories",
  asyncHandler(async (_req, res) => {
    const items = await prisma.infoCategory.findMany({
      select: { id: true, name: true, sortOrder: true },
      orderBy: [{ sortOrder: "asc" }, { name: "asc" }],
    });
    res.json({ items });
  })
);

// GET /infos (public) categoryId/tag/near + cursor/limit
infoRouter.get(
  "/infos",
  asyncHandler(async (req, res) => {
    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);

    const categoryId = req.query.categoryId ? String(req.query.categoryId) : undefined;
    const tag = req.query.tag ? String(req.query.tag) : undefined;

    const nearLat = req.query.nearLat ? Number(req.query.nearLat) : undefined;
    const nearLng = req.query.nearLng ? Number(req.query.nearLng) : undefined;
    const radiusKm = req.query.radiusKm ? Number(req.query.radiusKm) : undefined;

    // near 검색은 실제로는 geo 인덱스가 필요하니,
    // 여기서는 "좌표가 있는 것만" 필터링 정도로 최소 구현.
    const where: any = {
      ...(categoryId ? { categoryId } : {}),
      ...(tag
        ? { tags: { some: { name: tag } } } // tag가 id가 아니라 name으로 들어오는 스펙
        : {}),
      ...(nearLat !== undefined && nearLng !== undefined
        ? { lat: { not: null }, lng: { not: null } }
        : {}),
    };

    const rows = await prisma.info.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where,
      orderBy: { id: "desc" },
      include: {
        category: true,
        tags: { include: { tag: true } },
      },
    });

    const hasNext = rows.length > limit;
    const items = hasNext ? rows.slice(0, limit) : rows;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    // radiusKm을 실제 반영하려면 Haversine 계산 필요.
    // 지금은 최소 구현이므로 radiusKm은 무시(필요 시 추가 구현).
    void radiusKm;

    res.json({ items, nextCursor });
  })
);

// POST /infos (ADMIN)
infoRouter.post(
  "/infos",
  requireAuth,
  requireAdmin,
  asyncHandler(async (req, res) => {
    const body = InfoCreateSchema.parse(req.body);

    const data: any = {
      categoryId: body.categoryId ?? null,
      title: body.title,
      content: body.content,
      sourceUrl: body.sourceUrl ?? null,
      locationName: body.locationName ?? null,
      address: body.address ?? null,
      lat: body.lat ?? null,
      lng: body.lng ?? null,
      ...(body.tagIds?.length ? { tags: { create: body.tagIds.map((tagId) => ({ tagId })) } } : {}),
    };

    const created = await prisma.info.create({
      data,
      include: { category: true, tags: { include: { tag: true } } },
    });

    res.status(201).json(created);
  })
);

// GET /infos/:id (public)
infoRouter.get(
  "/infos/:id",
  asyncHandler(async (req, res) => {
    const id = req.params.id;    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");    const item = await prisma.info.findUnique({
      where: { id },
      include: { category: true, tags: { include: { tag: true } } },
    });
    if (!item) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    res.json(item);
  })
);

// PATCH /infos/:id (ADMIN)
infoRouter.patch(
  "/infos/:id",
  requireAuth,
  requireAdmin,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    const body = InfoUpdateSchema.parse(req.body);

    // 태그 갱신: set 전략
    const data: any = {
      ...(body.categoryId !== undefined ? { categoryId: body.categoryId } : {}),
      ...(body.title !== undefined ? { title: body.title } : {}),
      ...(body.content !== undefined ? { content: body.content } : {}),
      ...(body.sourceUrl !== undefined ? { sourceUrl: body.sourceUrl } : {}),
      ...(body.locationName !== undefined ? { locationName: body.locationName } : {}),
      ...(body.address !== undefined ? { address: body.address } : {}),
      ...(body.lat !== undefined ? { lat: body.lat } : {}),
      ...(body.lng !== undefined ? { lng: body.lng } : {}),
      ...(body.tagIds !== undefined ? { tags: { set: body.tagIds.map((tid) => ({ tagId: tid })) } } : {}),
    };

    const updated = await prisma.info.update({
      where: { id },
      data,
      include: { category: true, tags: { include: { tag: true } } },
    });

    res.json(updated);
  })
);

// DELETE /infos/:id (ADMIN) (soft delete 권장 → 여기선 hard delete)
infoRouter.delete(
  "/infos/:id",
  requireAuth,
  requireAdmin,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    await prisma.info.delete({ where: { id } });
    res.status(204).send();
  })
);
