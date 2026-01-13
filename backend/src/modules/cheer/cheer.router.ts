import { Router } from "express";
import { asyncHandler } from "../../utils/asyncHandler.js";
import { requireAuth } from "../../middlewares/auth.js";
import { getActiveMatch, postCheerTap } from "./cheer.controller.js";

export const cheerRouter = Router();

// GET /cheer/active-match
cheerRouter.get("/cheer/active-match", asyncHandler(getActiveMatch));

// POST /cheer/taps  (인증 필요)
cheerRouter.post("/cheer/taps", requireAuth, asyncHandler(postCheerTap));
