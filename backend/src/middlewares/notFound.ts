// src/middlewares/notFound.ts
import type { Request, Response } from "express";

export function notFound(_req: Request, res: Response) {
  res.status(404).json({ code: "NOT_FOUND", message: "존재하지 않는 경로입니다." });
}
