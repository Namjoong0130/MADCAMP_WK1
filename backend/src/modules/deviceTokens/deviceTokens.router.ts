import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";
import { requireAuth } from "../../middlewares/auth.js";
import { HttpError } from "../../utils/httpError.js";
import { z } from "zod";

export const deviceTokensRouter = Router();

const DeviceTokenSchema = z.object({
  token: z.string().min(1),
  platform: z.enum(["ANDROID", "IOS", "WEB"]),
});

deviceTokensRouter.post(
  "/",
  requireAuth,
  asyncHandler(async (req, res) => {
    const body = DeviceTokenSchema.parse(req.body);

    // token 유니크라고 가정(이미 있으면 409)
    const exists = await prisma.deviceToken.findUnique({ where: { token: body.token } });
    if (exists) throw new HttpError(409, "이미 존재하는 리소스입니다.", "CONFLICT");

    await prisma.deviceToken.create({
      data: {
        token: body.token,
        platform: body.platform,
        userId: req.user!.id,
      },
    });

    res.status(201).send();
  })
);
