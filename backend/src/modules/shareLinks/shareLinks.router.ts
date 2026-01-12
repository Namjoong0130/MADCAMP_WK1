import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { requireAuth } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";
import { z } from "zod";
import crypto from "crypto";

export const shareLinksRouter = Router();

const CreateSchema = z.object({
  entityType: z.enum(["POST", "SCHEDULE", "INFO", "CHEER"]),
  entityId: z.string().min(1),
  expiresAt: z.string().datetime().nullable().optional(),
});

function makeSlug() {
  return crypto.randomBytes(6).toString("base64url");
}

shareLinksRouter.post(
  "/",
  requireAuth,
  asyncHandler(async (req, res) => {
    const body = CreateSchema.parse(req.body);
    const slug = makeSlug();

    const created = await prisma.shareLink.create({
      data: {
        slug,
        entityType: body.entityType,
        entityId: body.entityId,
        expiresAt: body.expiresAt ? new Date(body.expiresAt) : null,
        createdById: req.user!.id,
      } as any,
      select: { slug: true },
    });

    const base = process.env.PUBLIC_BASE_URL ?? "";
    const url = `${base}/api/share-links/${created.slug}`;

    res.status(201).json({ slug: created.slug, url });
  })
);

shareLinksRouter.get(
  "/:slug",
  asyncHandler(async (req, res) => {
    const slug = req.params.slug;
    if (typeof slug !== 'string') throw new HttpError(400, "Invalid slug", "INVALID_SLUG");

    const link = await prisma.shareLink.findUnique({ where: { slug } });
    if (!link) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (link.expiresAt && link.expiresAt.getTime() < Date.now()) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

    let resolved: any = null;

    switch (link.entityType) {
      case "POST":
        resolved = await prisma.post.findUnique({
          where: { id: link.entityId },
          include: {
            author: { select: { id: true, nickname: true, schoolId: true, profileImageUrl: true } },
            tags: { include: { tag: { select: { id: true, name: true } } } },
            medias: { include: { media: { select: { id: true, url: true, mimeType: true, size: true, width: true, height: true } } } },
          },
        });
        break;
      case "SCHEDULE":
        resolved = await prisma.schedule.findUnique({ where: { id: link.entityId } });
        break;
      case "INFO":
        resolved = await prisma.info.findUnique({
          where: { id: link.entityId },
          include: { category: true, tags: { include: { tag: { select: { id: true, name: true } } } } },
        });
        break;
      case "CHEER":
        resolved = await prisma.cheerMethod.findUnique({ where: { id: link.entityId } });
        break;
    }

    if (!resolved) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    res.json({ entityType: link.entityType, entityId: link.entityId, resolved });
  })
);
