// src/middlewares/auth.ts
import type { Request, Response, NextFunction } from "express";
import { prisma } from "../db/prisma.js";
import { HttpError } from "../utils/httpError.js";
import { verifyAccessToken } from "../utils/jwt.js";

export type AuthUser = { id: string; role: "USER" | "ADMIN"; schoolId: string | null };

declare global {
  namespace Express {
    interface Request {
      user?: AuthUser;
    }
  }
}

export async function requireAuth(req: Request, _res: Response, next: NextFunction) {
  const auth = req.headers.authorization;
  if (!auth?.startsWith("Bearer ")) return next(new HttpError(401, "인증이 필요합니다.", "UNAUTHORIZED"));

  try {
    const token = auth.slice("Bearer ".length);
    const payload = await verifyAccessToken(token);

    const user = await prisma.user.findUnique({
      where: { id: payload.sub },
      select: { id: true, role: true, schoolId: true },
    });
    if (!user) return next(new HttpError(401, "인증이 필요합니다.", "UNAUTHORIZED"));

    req.user = { id: user.id, role: user.role, schoolId: user.schoolId };
    return next();
  } catch {
    return next(new HttpError(401, "인증이 필요합니다.", "UNAUTHORIZED"));
  }
}

export function requireAdmin(req: Request, _res: Response, next: NextFunction) {
  if (req.user?.role !== "ADMIN") return next(new HttpError(403, "권한이 없습니다.", "FORBIDDEN"));
  next();
}
