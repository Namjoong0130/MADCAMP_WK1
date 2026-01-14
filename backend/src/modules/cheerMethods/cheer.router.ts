import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { requireAuth } from "../../middlewares/auth.js";
import { getActiveMatch, postCheerTap } from "./cheer.controller.js";

export const cheerRouter = Router();

// routes.ts에서 /cheer로 들어오므로, 최종 경로는 /api/cheer/active-match가 됩니다.
cheerRouter.get("/active-match", asyncHandler(getActiveMatch));

// 최종 경로: /api/cheer/taps
cheerRouter.post("/taps", requireAuth, asyncHandler(postCheerTap));