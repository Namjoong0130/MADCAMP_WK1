// src/middlewares/error.ts
import type { NextFunction, Request, Response } from "express";
import { HttpError } from "../utils/httpError.js";

export function errorMiddleware(err: unknown, _req: Request, res: Response, _next: NextFunction) {
  if (err instanceof HttpError) {
    return res.status(err.status).json({
      code: err.code ?? "ERROR",
      message: err.message,
    });
  }

  // zod 에러 등 일반 에러 처리
  const anyErr = err as any;
  if (anyErr?.name === "ZodError") {
    return res.status(400).json({
      code: "VALIDATION_ERROR",
      message: "요청 값이 올바르지 않습니다.",
      details: anyErr.issues,
    });
  }

  console.error("[UNHANDLED_ERROR]", err);
  return res.status(500).json({
    code: "INTERNAL_ERROR",
    message: "서버 오류가 발생했습니다.",
  });
}
