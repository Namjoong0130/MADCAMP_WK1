import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { prisma } from "../../db/prisma.js";

export const tagsRouter = Router();

// GET /tags?scope=post|info|all (public)
tagsRouter.get(
  "/",
  asyncHandler(async (req, res) => {
    const scope = String(req.query.scope ?? "all");
    // 모델 설계에 따라 tag에 scope 컬럼이 없을 수도 있어 "전체 반환"을 기본으로 둡니다.
    // 만약 Tag에 scope 컬럼이 있다면 where 조건을 추가하세요.
    const items = await prisma.tag.findMany({
      select: { id: true, name: true },
      orderBy: { name: "asc" },
    });
    res.json({ items });
  })
);
