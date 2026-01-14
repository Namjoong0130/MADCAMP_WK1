// backend/src/modules/posts/posts.router.ts
import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import { requireAuth, optionalAuth } from "../../middlewares/auth.js";
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

  return Array.from(new Set(resolved));
}

/**
 * ✅ 공통: 응답용 매핑
 * - medias 평탄화
 * - likeCount, likedByMe 계산
 * - prisma include 부가필드(_count, likes) 제거
 */
function mapPostForResponse(p: any, viewerUserId?: string) {
  const mediasFlat = (p.medias ?? []).map((pm: any) => pm.media).filter(Boolean);

  const likeCount = p._count?.likes ?? 0;
  const likedByMe = viewerUserId ? (p.likes?.length ?? 0) > 0 : false;

  const { _count, likes, ...rest } = p;

  return {
    ...rest,
    medias: mediasFlat,
    contentPreview: p.content?.length > 80 ? p.content.slice(0, 80) : p.content,
    likeCount,
    likedByMe,
  };
}

// -------------------------------------------------------------------
// GET /posts (public) - 토큰 있으면 likedByMe 포함
// -------------------------------------------------------------------
postsRouter.get(
  "/",
  optionalAuth,
  asyncHandler(async (req, res) => {
    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);
    const tag = req.query.tag ? String(req.query.tag) : undefined;

    const where: any = {
      ...(tag ? { tags: { some: { tag: { name: tag } } } } : {}),
    };

    // ✅ include 구성 (로그인 여부에 따라 likes include만 추가)
    const include: any = {
      author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
      tags: { include: { tag: { select: { id: true, name: true } } } },
      medias: {
        take: 1,
        include: {
          media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
        },
      },
      _count: { select: { likes: true } }, // ✅ Post의 관계필드명은 likes
    };

    if (req.user) {
      include.likes = {
        where: { userId: req.user.id },
        select: { userId: true, postId: true }, // ✅ PostLike에는 id 없음
      };
    }

    const rows = await prisma.post.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where,
      orderBy: { createdAt: "desc" },
      include,
    });

    // 기존 로직 유지 (SCHOOL_ONLY 필터)
    const filtered = rows.filter((p: any) => p.visibility !== "SCHOOL_ONLY");

    const hasNext = filtered.length > limit;
    const items = hasNext ? filtered.slice(0, limit) : filtered;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    const cleaned = items.map((p: any) => mapPostForResponse(p, req.user?.id));

    res.json({ items: cleaned, nextCursor });
  })
);

// -------------------------------------------------------------------
// POST /posts (auth)
// -------------------------------------------------------------------
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
        ...(body.tagIds?.length ? { tags: { create: body.tagIds.map((tagId) => ({ tagId })) } } : {}),
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
        _count: { select: { likes: true } },
        likes: {
          where: { userId: req.user!.id },
          select: { userId: true, postId: true },
        },
      },
    });

    const flattened = flattenMedias(created);

    // created.likes는 보통 0개라 likedByMe=false가 정상
    res.status(201).json({
      ...flattened,
      likeCount: created._count?.likes ?? 0,
      likedByMe: (created.likes?.length ?? 0) > 0,
    });
  })
);

// -------------------------------------------------------------------
// GET /posts/:id (public) - 토큰 있으면 likedByMe 포함
// -------------------------------------------------------------------
postsRouter.get(
  "/:id",
  optionalAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const include: any = {
      author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
      tags: { include: { tag: { select: { id: true, name: true } } } },
      medias: {
        orderBy: { sortOrder: "asc" },
        include: {
          media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
        },
      },
      _count: { select: { likes: true } },
    };

    if (req.user) {
      include.likes = {
        where: { userId: req.user.id },
        select: { userId: true, postId: true },
      };
    }

    const post = await prisma.post.findUnique({
      where: { id },
      include,
    });

    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (post.visibility === "SCHOOL_ONLY") throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");

    const cleaned = mapPostForResponse(post, req.user?.id);

    res.json(cleaned);
  })
);

// -------------------------------------------------------------------
// PATCH /posts/:id (작성자 or ADMIN)
// -------------------------------------------------------------------
postsRouter.patch(
  "/:id",
  requireAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");
    const body = PostUpdateSchema.parse(req.body);

    const before = await prisma.post.findUnique({ where: { id }, select: { id: true, authorId: true } });
    if (!before) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (req.user!.role !== "ADMIN" && before.authorId !== req.user!.id) {
      throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");
    }

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
          ? { medias: { deleteMany: {}, create: body.mediaIds.map((mediaId, i) => ({ mediaId, sortOrder: i })) } }
          : {}),
      },
      include: {
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        medias: {
          orderBy: { sortOrder: "asc" },
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
        _count: { select: { likes: true } },
        likes: { where: { userId: req.user!.id }, select: { userId: true, postId: true } },
      },
    });

    const flattened = flattenMedias(updated);

    res.json({
      ...flattened,
      likeCount: updated._count?.likes ?? 0,
      likedByMe: (updated.likes?.length ?? 0) > 0,
    });
  })
);

// -------------------------------------------------------------------
// ✅ POST /posts/:id/like (토글)
// -------------------------------------------------------------------
postsRouter.post(
  "/:id/like",
  requireAuth,
  asyncHandler(async (req, res) => {
    const postId = req.params.id;
    if (typeof postId !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const post = await prisma.post.findUnique({
      where: { id: postId },
      select: { id: true, visibility: true },
    });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (post.visibility === "SCHOOL_ONLY") throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");

    const userId = req.user!.id;

    const result = await prisma.$transaction(async (tx) => {
      const existing = await tx.postLike.findUnique({
        where: { userId_postId: { userId, postId } }, // ✅ 복합키 이름 확정
        select: { userId: true, postId: true },
      });

      let liked: boolean;

      if (existing) {
        await tx.postLike.delete({
          where: { userId_postId: { userId, postId } },
        });
        liked = false;
      } else {
        await tx.postLike.create({
          data: { userId, postId },
        });
        liked = true;
      }

      // ✅ count는 postLike 모델에서 세는 게 가장 단순/정확
      const likeCount = await tx.postLike.count({ where: { postId } });

      return { liked, likeCount };
    });

    res.status(200).json(result); // { liked, likeCount }
  })
);

// -------------------------------------------------------------------
// DELETE /posts/:id
// -------------------------------------------------------------------
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
