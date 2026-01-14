// backend/src/modules/posts/posts.router.ts
import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import { requireAuth } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";
import { z } from "zod";

export const postsRouter = Router();

const PostCreateSchema = z.object({
  title: z.string().min(1),
  content: z.string().min(1),
  visibility: z.enum(["PUBLIC", "SCHOOL_ONLY"]).optional().default("PUBLIC"),
  tagIds: z.array(z.string()).optional().default([]),
  mediaIds: z.array(z.string()).optional().default([]),
});
const PostUpdateSchema = PostCreateSchema.partial();

// ✅ PostMedia[] -> Media[]로 평탄화
function flattenMedias(post: any) {
  return {
    ...post,
    medias: (post.medias ?? []).map((pm: any) => pm.media).filter(Boolean),
  };
}



// posts.router.ts 상단 import 근처에 prisma가 이미 있으니 그대로 사용

async function resolveTagIds(tagInputs: string[]) {
  if (!tagInputs?.length) return [];

  const resolved: string[] = [];

  for (const t of tagInputs) {
    if (!t?.trim()) continue;

    // 1) "id"로 들어온 경우인지 먼저 확인
    const byId = await prisma.tag.findUnique({ where: { id: t } });
    if (byId) {
      resolved.push(byId.id);
      continue;
    }

    // 2) 아니면 "name"으로 보고 upsert
    const byName = await prisma.tag.upsert({
      where: { name: t },
      update: {},
      create: { name: t },
      select: { id: true },
    });
    resolved.push(byName.id);
  }

  // 중복 제거
  return Array.from(new Set(resolved));
}

// GET /get
postsRouter.get(
  "/",
  asyncHandler(async (req, res) => {
    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);
    const tag = req.query.tag ? String(req.query.tag) : undefined;

    const where: any = {
      ...(tag ? { tags: { some: { tag: { name: tag } } } } : {}),
    };

    const rows = await prisma.post.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where,
      orderBy: { createdAt: "desc" },
      include: {
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },

        // ✅ 목록은 대표 1장만
        medias: {
          take: 1,
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
      },
    });

    const filtered = rows.filter((p: any) => p.visibility !== "SCHOOL_ONLY");
    const hasNext = filtered.length > limit;
    const items = hasNext ? filtered.slice(0, limit) : filtered;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    // ✅ medias 평탄화
    const mapped = items.map((p) => ({
      ...p,
      medias: (p.medias ?? []).map((pm: any) => pm.media).filter(Boolean),
      contentPreview: p.content.length > 80 ? p.content.slice(0, 80) : p.content,
    }));

    res.json({ items: mapped, nextCursor });
  })
);


postsRouter.post(
  "/",
  requireAuth,
  asyncHandler(async (req, res) => {
    const body = PostCreateSchema.parse(req.body);

    const created = await prisma.post.create({
      data: {
        authorId: req.user!.id,
        title: body.title,
        content: body.content,
        visibility: body.visibility,
        ...(body.tagIds?.length
          ? { tags: { create: body.tagIds.map((tagId) => ({ tagId })) } }
          : {}),
        ...(body.mediaIds?.length
           ? { medias: { create: body.mediaIds.map((mediaId, i) => ({ mediaId, sortOrder: i })) } }
          : {}),
      },
      include: {
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        medias: {
          take: 1,
          orderBy: { sortOrder: "asc" },
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
      },
    });

    res.status(201).json(flattenMedias(created));
  })
);



// GET /posts/:id
postsRouter.get(
  "/:id",
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const post = await prisma.post.findUnique({
      where: { id },
      include: {
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        medias: {
          orderBy: { sortOrder: "asc" },
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
      },
    });

    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (post.visibility === "SCHOOL_ONLY") throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");

    res.json(flattenMedias(post));
  })
);


// PATCH /posts/:id (작성자 or ADMIN)
postsRouter.patch(
  "/:id",
  requireAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");
    const body = PostUpdateSchema.parse(req.body);

    const post = await prisma.post.findUnique({ where: { id }, select: { id: true, authorId: true } });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (req.user!.role !== "ADMIN" && post.authorId !== req.user!.id) {
      throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");
    }

    // ✅ tagIds 변환
    const realTagIds = body.tagIds !== undefined ? await resolveTagIds(body.tagIds) : undefined;

    const updated = await prisma.post.update({
      where: { id },
      data: {
        ...(body.title !== undefined ? { title: body.title } : {}),
        ...(body.content !== undefined ? { content: body.content } : {}),
        ...(body.visibility !== undefined ? { visibility: body.visibility } : {}),

        ...(realTagIds !== undefined
          ? { tags: { deleteMany: {}, create: realTagIds.map((tagId) => ({ tagId })) } }
          : {}),

        ...(body.mediaIds !== undefined
          ? { medias: { deleteMany: {}, create: body.mediaIds.map((mediaId) => ({ mediaId })) } }
          : {}),
      },
      include: {
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        medias: { include: { media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } } } },
      },
    });

    res.json(flattenMedias(updated));
  })
);


// DELETE /posts/:id
postsRouter.delete(
  "/:id",
  requireAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const post = await prisma.post.findUnique({ where: { id }, select: { id: true, authorId: true } });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (req.user!.role !== "ADMIN" && post.authorId !== req.user!.id) {
      throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");
    }

    await prisma.post.delete({ where: { id } });
    res.status(204).send();
  })
);


