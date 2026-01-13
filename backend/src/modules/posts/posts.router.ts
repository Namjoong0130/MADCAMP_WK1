//backend/src/modules/posts/posts.router.ts
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

function toClientPost(post: any) {
  // ✅ PostMedia[] -> Media[] 로 평탄화
  const medias = Array.isArray(post.medias) ? post.medias.map((pm: any) => pm.media).filter(Boolean) : [];
  return { ...post, medias };
}

// GET /posts (public, but SCHOOL_ONLY는 필터링/권한처리)
postsRouter.get(
  "/",
  asyncHandler(async (req, res) => {
    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);
    const visibility = req.query.visibility ? String(req.query.visibility) : undefined;
    const schoolId = req.query.schoolId ? String(req.query.schoolId) : undefined;
    const tag = req.query.tag ? String(req.query.tag) : undefined;

    const where: any = {
      ...(visibility ? { visibility } : {}),
      ...(tag ? { tags: { some: { name: tag } } } : {}),
      ...(schoolId ? { author: { schoolId } } : {}),
    };

    const rows = await prisma.post.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where,
      orderBy: { id: "desc" },
      select: {
        id: true,
        title: true,
        content: true,
        visibility: true,
        likeCount: true,
        commentCount: true,
        createdAt: true,
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        tags: { include: { tag: { select: { id: true, name: true } } } },
        // ✅ 목록에서도 대표 이미지(첫 장) 정도는 내려줌
        medias: {
          orderBy: { sortOrder: "asc" },
          take: 1,
          include: {
            media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } },
          },
        },
      },
    });

    // SCHOOL_ONLY는 로그인 없이 보이면 안 되므로 목록에서 제거
    const filtered = rows.filter((p) => p.visibility !== "SCHOOL_ONLY");

    const hasNext = filtered.length > limit;
    const items = hasNext ? filtered.slice(0, limit) : filtered;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    const mapped = items.map((p) => {
      const flattened = toClientPost(p);
      return {
        ...flattened,
        contentPreview: p.content.length > 80 ? p.content.slice(0, 80) : p.content,
      };
    });

    res.json({ items: mapped, nextCursor });
  })
);

// POST /posts (login)
postsRouter.post(
  "/",
  requireAuth,
  asyncHandler(async (req, res) => {
    const body = PostCreateSchema.parse(req.body);

    const data: any = {
      authorId: req.user!.id,
      title: body.title,
      content: body.content,
      visibility: body.visibility,
      ...(body.tagIds.length ? { tags: { create: body.tagIds.map((tagId) => ({ tagId })) } } : {}),
      ...(body.mediaIds.length ? { medias: { create: body.mediaIds.map((mediaId) => ({ mediaId })) } } : {}),
    };

    const created = await prisma.post.create({
      data,
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

    res.status(201).json(toClientPost(created));
  })
);

// GET /posts/:id (public지만 SCHOOL_ONLY 권한처리)
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

    if (post.visibility === "SCHOOL_ONLY") {
      throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");
    }

    res.json(toClientPost(post));
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
    if (req.user!.role !== "ADMIN" && post.authorId !== req.user!.id) throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");

    const updated = await prisma.post.update({
      where: { id },
      data: {
        ...(body.title !== undefined ? { title: body.title } : {}),
        ...(body.content !== undefined ? { content: body.content } : {}),
        ...(body.visibility !== undefined ? { visibility: body.visibility } : {}),
        ...(body.tagIds !== undefined
          ? { tags: { set: body.tagIds.map((tid) => ({ postId_tagId: { postId: id, tagId: tid } })) } }
          : {}),
        ...(body.mediaIds !== undefined
          ? { medias: { set: body.mediaIds.map((mid) => ({ postId_mediaId: { postId: id, mediaId: mid } })) } }
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
      },
    });

    res.json(toClientPost(updated));
  })
);

// DELETE /posts/:id (작성자 or ADMIN)
postsRouter.delete(
  "/:id",
  requireAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const post = await prisma.post.findUnique({ where: { id }, select: { id: true, authorId: true } });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (req.user!.role !== "ADMIN" && post.authorId !== req.user!.id) throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");

    await prisma.post.delete({ where: { id } });
    res.status(204).send();
  })
);

// POST /posts/:id/like (toggle)
postsRouter.post(
  "/:id/like",
  requireAuth,
  asyncHandler(async (req, res) => {
    const postId = req.params.id;
    if (typeof postId !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");
    const userId = req.user!.id;

    const post = await prisma.post.findUnique({ where: { id: postId }, select: { id: true, likeCount: true } });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

    const existed = await prisma.postLike
      .findUnique({ where: { userId_postId: { userId, postId } } })
      .catch(() => null);

    let liked: boolean;
    let likeCount: number;

    if (existed) {
      await prisma.postLike.delete({ where: { userId_postId: { userId, postId } } });
      const updated = await prisma.post.update({
        where: { id: postId },
        data: { likeCount: { decrement: 1 } },
        select: { likeCount: true },
      });
      liked = false;
      likeCount = updated.likeCount;
    } else {
      await prisma.postLike.create({ data: { userId, postId } });
      const updated = await prisma.post.update({
        where: { id: postId },
        data: { likeCount: { increment: 1 } },
        select: { likeCount: true },
      });
      liked = true;
      likeCount = updated.likeCount;
    }

    res.json({ liked, likeCount });
  })
);
