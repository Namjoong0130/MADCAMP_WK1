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

/**
 * ✅ optionalAuth
 * - Authorization이 없으면: 그대로 next()
 * - 있으면: 토큰 검증 후 req.user 세팅, 실패해도 next() (조회 API에서만 사용)
 */
export async function optionalAuth(req: Request, _res: Response, next: NextFunction) {
  const auth = req.headers.authorization;
  if (!auth?.startsWith("Bearer ")) return next();

  try {
    const token = auth.slice("Bearer ".length);
    const payload = await verifyAccessToken(token);

    const user = await prisma.user.findUnique({
      where: { id: payload.sub },
      select: { id: true, role: true, schoolId: true },
    });

    if (user) {
      req.user = { id: user.id, role: user.role, schoolId: user.schoolId };
    }
  } catch {
    // 조회 API에서는 인증 실패를 강제 에러로 만들지 않음
  }
  return next();
}

export function requireAdmin(req: Request, _res: Response, next: NextFunction) {
  if (req.user?.role !== "ADMIN") return next(new HttpError(403, "권한이 없습니다.", "FORBIDDEN"));
  next();
}
