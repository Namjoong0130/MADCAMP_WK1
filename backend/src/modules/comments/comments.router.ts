import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import { requireAuth } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";
import { z } from "zod";

export const commentsRouter = Router();

const CommentCreateSchema = z.object({
  content: z.string().min(1),
  parentId: z.string().nullable().optional(),
});

// GET /posts/:id/comments (public; SCHOOL_ONLY 권한은 Posts에서 같이 보장하는 게 이상적)
// 여기서는 단순 구현: 해당 post가 SCHOOL_ONLY면 댓글도 403
commentsRouter.get(
  "/posts/:id/comments",
  asyncHandler(async (req, res) => {
    const postId = req.params.id;
    if (typeof postId !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);

    const post = await prisma.post.findUnique({ where: { id: postId }, select: { id: true, visibility: true } });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (post.visibility === "SCHOOL_ONLY") throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");

    const rows = await prisma.comment.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      where: { postId },
      orderBy: { id: "desc" },
      include: { author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } } },
    });

    const hasNext = rows.length > limit;
    const items = hasNext ? rows.slice(0, limit) : rows;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    res.json({ items, nextCursor });
  })
);

// POST /posts/:id/comments (login)
commentsRouter.post(
  "/posts/:id/comments",
  requireAuth,
  asyncHandler(async (req, res) => {
    const postId = req.params.id;    if (typeof postId !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");    const body = CommentCreateSchema.parse(req.body);

    const post = await prisma.post.findUnique({ where: { id: postId }, select: { id: true } });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

    const created = await prisma.comment.create({
      data: {
        postId,
        authorId: req.user!.id,
        content: body.content,
        parentId: body.parentId ?? null,
      },
      include: { author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } } },
    });

    // commentCount 증가
    await prisma.post.update({ where: { id: postId }, data: { commentCount: { increment: 1 } } });

    res.status(201).json(created);
  })
);

// DELETE /comments/:id (작성자 or ADMIN)
commentsRouter.delete(
  "/comments/:id",
  requireAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
    const comment = await prisma.comment.findUnique({ where: { id }, select: { id: true, authorId: true, postId: true } });
    if (!comment) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (req.user!.role !== "ADMIN" && comment.authorId !== req.user!.id) throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");

    await prisma.comment.delete({ where: { id } });
    await prisma.post.update({ where: { id: comment.postId }, data: { commentCount: { decrement: 1 } } });

    res.status(204).send();
  })
);
