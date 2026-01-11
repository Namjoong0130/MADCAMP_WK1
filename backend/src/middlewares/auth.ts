import type { Request, Response, NextFunction } from "express";
import { HttpError } from "../utils/httpError.js";
import { verifyAccessToken } from "../utils/jwt.js";

export type AuthUser = { id: string; role?: string | undefined };

declare global {
  namespace Express {
    interface Request {
      user?: AuthUser;
    }
  }
}

export async function requireAuth(req: Request, _res: Response, next: NextFunction) {
  const auth = req.headers.authorization;
  if (!auth?.startsWith("Bearer ")) return next(new HttpError(401, "Unauthorized", "UNAUTHORIZED"));

  const token = auth.slice("Bearer ".length);
  try {
    const payload = await verifyAccessToken(token);
    req.user = { id: payload.sub, role: payload.role };
    next();
  } catch {
    next(new HttpError(401, "Invalid token", "INVALID_TOKEN"));
  }
}
