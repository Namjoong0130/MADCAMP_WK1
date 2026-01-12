import { Router } from "express";
import { getMe, patchMe } from "../modules/me/me.controller.js";

export const meRouter = Router();

meRouter.get("/", getMe);
meRouter.patch("/", patchMe);
