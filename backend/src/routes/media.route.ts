import { Router } from "express";
import { prisma } from "../db/prisma.js";

export const mediaRouter = Router();

/**
 * POST /api/media
 * body: { url: string, mimeType?: string, size?: number, width?: number, height?: number }
 * return: { id, url, mimeType, size, width, height }
 */
mediaRouter.post("/", async (req, res, next) => {
  try {
    const { url, mimeType, size, width, height } = req.body ?? {};

    if (!url || typeof url !== "string") {
      return res.status(400).json({ message: "url is required (string)" });
    }

    // 프로젝트마다 auth middleware가 req.user / res.locals.user 등에 userId를 주입하는 방식이 다름
    // 없으면 uploaderId는 null로 들어가도 Prisma 스키마상 허용됨
    const userId =
      (req as any).user?.id ??
      (req as any).userId ??
      (res.locals as any)?.user?.id ??
      null;

    const media = await prisma.media.create({
      data: {
        url,
        mimeType: typeof mimeType === "string" ? mimeType : null,
        size: typeof size === "number" ? Math.trunc(size) : null,
        width: typeof width === "number" ? Math.trunc(width) : null,
        height: typeof height === "number" ? Math.trunc(height) : null,
        uploaderId: typeof userId === "string" ? userId : null,
      },
      select: {
        id: true,
        url: true,
        mimeType: true,
        size: true,
        width: true,
        height: true,
      },
    });

    return res.status(201).json(media);
  } catch (err) {
    return next(err);
  }
});
