// src/modules/tags/tags.router.ts
import { Router } from "express";
import { prisma } from "../../db/prisma.js";
import { asyncHandler } from "../../utils/asyncHandler.js";

export const tagsRouter = Router();

// GET /tags
tagsRouter.get(
  "/",
  asyncHandler(async (_req, res) => {
    const tags = await prisma.tag.findMany({
      orderBy: { name: "asc" },
      select: { id: true, name: true },
    });
    res.json({ items: tags });
  })
);
