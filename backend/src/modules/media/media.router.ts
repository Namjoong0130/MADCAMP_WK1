// backend/src/modules/media/media.router.ts
import { Router } from "express";
import { z } from "zod";
import { prisma } from "../../db/prisma.js";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { requireAuth } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";

export const mediaRouter = Router();

const MediaCreateSchema = z.object({
  url: z.string().min(1), // 앱에서 data:image/jpeg;base64,... 형태로 보내는 값 허용
  mimeType: z.string().optional(),
  size: z.number().int().nonnegative().optional(),
  width: z.number().int().nonnegative().optional(),
  height: z.number().int().nonnegative().optional(),
});

mediaRouter.post(
  "/",
  requireAuth,
  asyncHandler(async (req, res) => {
    const body = MediaCreateSchema.parse(req.body);

    // 너무 큰 data URL은 운영에서 지양 (현재는 최소 방어만)
    if (body.url.length > 2_000_000) {
      throw new HttpError(413, "이미지가 너무 큽니다. 더 작게 압축해서 업로드하세요.", "PAYLOAD_TOO_LARGE");
    }

    const created = await prisma.media.create({
      data: {
        uploaderId: req.user!.id,
        url: body.url,
        mimeType: body.mimeType ?? null,
        size: body.size ?? null,
        width: body.width ?? null,
        height: body.height ?? null,
      },
      select: {
        id: true,
        url: true,
        mimeType: true,
        size: true,
        width: true,
        height: true,
        createdAt: true,
      },
    });

    res.status(201).json({
      id: created.id,
      url: created.url,
      mimeType: created.mimeType,
      size: created.size,
      width: created.width,
      height: created.height,
      createdAt: created.createdAt,
    });
  })
);
