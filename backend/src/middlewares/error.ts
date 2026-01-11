import type { Request, Response, NextFunction } from "express";
import { HttpError } from "../utils/httpError.js";

export function errorMiddleware(err: any, _req: Request, res: Response, _next: NextFunction) {
  const status = err instanceof HttpError ? err.status : 500;
  const message = err?.message ?? "Internal Server Error";

  res.status(status).json({
    message,
    code: err instanceof HttpError ? err.code : "INTERNAL_ERROR",
  });
}
