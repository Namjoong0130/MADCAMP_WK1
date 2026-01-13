// backend/src/modules/comments/comments.router.ts
import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { requireAuth } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import { z } from "zod";

export const commentsRouter = Router();

const CommentCreateSchema = z.object({
  content: z.string().min(1).max(1000),
  parentId: z.string().optional().nullable(),
});

// GET /posts/:id/comments?cursor=&limit=
commentsRouter.get(
  "/posts/:id/comments",
  asyncHandler(async (req, res) => {
    const postId = req.params.id;
    if (typeof postId !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const limit = parseLimit(req.query.limit);
    const cursor = parseCursor(req.query.cursor);

    // post 존재 체크
    const post = await prisma.post.findUnique({ where: { id: postId }, select: { id: true, visibility: true } });
    if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (post.visibility === "SCHOOL_ONLY") {
      // 현재 정책: 학교전용은 차단
      throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");
    }

    const rows = await prisma.comment.findMany({
      where: { postId },
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      orderBy: [{ createdAt: "desc" }, { id: "desc" }],
      select: {
        id: true,
        postId: true,
        authorId: true,
        parentId: true,
        content: true,
        createdAt: true,
        updatedAt: true,
        deletedAt: true,
        author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
      },
    });

    const hasNext = rows.length > limit;
    const items = hasNext ? rows.slice(0, limit) : rows;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    res.json({ items, nextCursor });
  })
);

// POST /posts/:id/comments
commentsRouter.post(
  "/posts/:id/comments",
  requireAuth,
  asyncHandler(async (req, res) => {
    const postId = req.params.id;
    if (typeof postId !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    const body = CommentCreateSchema.parse(req.body);
    const userId = req.user!.id;

    const created = await prisma.$transaction(async (tx) => {
      const post = await tx.post.findUnique({ where: { id: postId }, select: { id: true } });
      if (!post) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

      // parentId가 있으면: 같은 post의 댓글인지 검증
      if (body.parentId) {
        const parent = await tx.comment.findUnique({
          where: { id: body.parentId },
          select: { id: true, postId: true },
        });
        if (!parent) throw new HttpError(400, "parentId가 유효하지 않습니다.", "INVALID_PARENT");
        if (parent.postId !== postId) throw new HttpError(400, "parentId가 다른 게시글의 댓글입니다.", "INVALID_PARENT");
      }

      const comment = await tx.comment.create({
        data: {
          postId,
          authorId: userId,
          content: body.content,
          parentId: body.parentId ?? null,
        },
        select: {
          id: true,
          postId: true,
          authorId: true,
          parentId: true,
          content: true,
          createdAt: true,
          updatedAt: true,
          deletedAt: true,
          author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
        },
      });

      // ✅ commentCount도 실데이터로 동기화(삭제/추가 불일치 방지)
      const commentCount = await tx.comment.count({ where: { postId, deletedAt: null } });
      await tx.post.update({ where: { id: postId }, data: { commentCount } });

      return comment;
    });

    res.status(201).json(created);
  })
);

// DELETE /comments/:id  (작성자 or ADMIN)
commentsRouter.delete(
  "/comments/:id",
  requireAuth,
  asyncHandler(async (req, res) => {
    const id = req.params.id;
    if (typeof id !== "string") throw new HttpError(400, "Invalid id", "INVALID_ID");

    await prisma.$transaction(async (tx) => {
      const target = await tx.comment.findUnique({
        where: { id },
        select: { id: true, postId: true, authorId: true, deletedAt: true },
      });
      if (!target) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

      if (req.user!.role !== "ADMIN" && target.authorId !== req.user!.id) {
        throw new HttpError(403, "권한이 없습니다.", "FORBIDDEN");
      }

      // 이미 삭제된 댓글이면 멱등 처리(204)
      if (target.deletedAt) return;

      // 대댓글 존재 여부
      const hasReplies = (await tx.comment.count({ where: { parentId: id } })) > 0;

      // ✅ 완성본 정책: 스레드 보호를 위해 soft delete
      // - 대댓글이 있으면 반드시 soft delete
      // - 대댓글이 없어도 soft delete로 통일(일관성)
      await tx.comment.update({
        where: { id },
        data: {
          deletedAt: new Date(),
          content: hasReplies ? "삭제된 댓글입니다." : "삭제된 댓글입니다.",
        },
      });

      // ✅ commentCount 실데이터 동기화
      const commentCount = await tx.comment.count({ where: { postId: target.postId, deletedAt: null } });
      await tx.post.update({ where: { id: target.postId }, data: { commentCount } });
    });

    res.status(204).send();
  })
);
