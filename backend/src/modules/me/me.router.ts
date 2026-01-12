import { Router } from "express";
import { requireAuth } from "../../middlewares/auth.js";
import { getMe, patchMe } from "./me.controller.js";

export const meRouter = Router();

meRouter.get("/", requireAuth, getMe);
meRouter.patch("/", requireAuth, patchMe);
