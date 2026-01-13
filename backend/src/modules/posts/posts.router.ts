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

// PostMedia[] -> Media[]로 평탄화 + likedByMe 계산까지 한 번에 처리
function mapPostForResponse(post: any, userId?: string) {
  const medias = Array.isArray(post.medias)
    ? post.medias.map((pm: any) => pm?.media).filter(Boolean)
    : [];

  // ✅ 조회 쿼리에서 postLikes를 userId 기준으로 1개만 가져오면,
  // post.postLikes.length>0 으로 likedByMe 계산 가능
  const likedByMe =
    userId && Array.isArray(post.postLikes) ? post.postLikes.length > 0 : null;

  return {
    ...post,
    medias,
    likedByMe,
  };
}

// GET /posts
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

    const userId = req.user?.id;

    const rows = await prisma.post.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where,
      orderBy: { createdAt: "desc" },
      select: {
        id: true,
        authorId: true,
        title: true,
        content: true,
        visibility: true,
        likeCount: true,
        commentCount: true,
        createdAt: true,
        updatedAt: true,
        deletedAt: true,
        author: {
          select: { id: true, nickname: true, schoolId: true, profileImageUrl: true },
        },
        tags: {
          include: { tag: { select: { id: true, name: true } } },
        },
        // 목록에는 대표 1장만 내려줌
        medias: {
          take: 1,
          include: {
            media: {
              select: { id: true, url: true, mimeType: true, size: true, width: true, height: true },
            },
          },
        },
        // ✅ likedByMe 계산용 (로그인 했을 때만 의미 있음)
        likes: userId
          ? {
              where: { userId },
              take: 1,
              select: { userId: true },
            }
          : false,
      },
    });

    // SCHOOL_ONLY는 로그인 없이 보이면 안 되므로 제거 (현재 정책 유지)
    const filtered = rows.filter((p) => p.visibility !== "SCHOOL_ONLY");

    const hasNext = filtered.length > limit;
    const items = hasNext ? filtered.slice(0, limit) : filtered;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    const mapped = items.map((p) => {
      const base = mapPostForResponse(p, userId);
      return {
        ...base,
        medias: (p.medias ?? []).map((pm: any) => pm.media).filter(Boolean),
        contentPreview: p.content.length > 80 ? p.content.slice(0, 80) : p.content,
      };
    });

    res.json({ items: mapped, nextCursor });
  })
);

// POST /posts
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
          ? { medias: { create: body.mediaIds.map((mediaId) => ({ mediaId })) } }
          : {}),
      },
      include: {
        author: {
          select: { id: true, nickname: true, schoolId: true, profileImageUrl: true },
        },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        medias: {
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
      },
    });

    // 작성 직후는 내 글이고, 좋아요는 기본 false
    res.status(201).json({ ...mapPostForResponse(created, req.user!.id), likedByMe: false });
  })
);

// GET /posts/:id
postsRouter.get(
  "/:id",
  optionalAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const userId = req.user?.id;

    const post = await prisma.post.findUnique({
      where: { id },
      include: {
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        medias: {
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
        // ✅ likes 계산용
        ...(userId
          ? {
              postLikes: {
                where: { userId },
                take: 1,
                select: { userId: true },
              },
            }
          : {}),
      },
    });

    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

    if (post.visibility === "SCHOOL_ONLY") {
      throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");
    }

    res.json(mapPostForResponse(post, userId));
  })
);

// PATCH /posts/:id
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

    const updated = await prisma.post.update({
      where: { id },
      data: {
        ...(body.title !== undefined ? { title: body.title } : {}),
        ...(body.content !== undefined ? { content: body.content } : {}),
        ...(body.visibility !== undefined ? { visibility: body.visibility } : {}),
        ...(body.tagIds !== undefined
          ? { tags: { deleteMany: {}, create: body.tagIds.map((tagId) => ({ tagId })) } }
          : {}),
        ...(body.mediaIds !== undefined
          ? { medias: { deleteMany: {}, create: body.mediaIds.map((mediaId) => ({ mediaId })) } }
          : {}),
      },
      include: {
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        medias: {
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
      },
    });

    res.json({ ...mapPostForResponse(updated, req.user!.id), likedByMe: null });
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

// ✅ POST /posts/:id/like (좋아요 토글: 동시성 안전 + likeCount 실데이터 동기화)
postsRouter.post(
  "/:id/like",
  requireAuth,
  asyncHandler(async (req, res) => {
    const postId = req.params.id;
    if (typeof postId !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const userId = req.user!.id;

    const result = await prisma.$transaction(async (tx) => {
      const post = await tx.post.findUnique({ where: { id: postId }, select: { id: true } });
      if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

      const existed = await tx.postLike.findUnique({
        where: { userId_postId: { userId, postId } },
        select: { userId: true },
      });

      let liked: boolean;
      if (existed) {
        await tx.postLike.delete({ where: { userId_postId: { userId, postId } } });
        liked = false;
      } else {
        await tx.postLike.create({ data: { userId, postId } });
        liked = true;
      }

      const likeCount = await tx.postLike.count({ where: { postId } });
      await tx.post.update({ where: { id: postId }, data: { likeCount } });

      return { liked, likeCount };
    });

    res.json(result);
  })
);
