import { Router } from "express";
import { requireAuth } from "../../middlewares/auth.js";
import { listSchedules, getSchedule, createSchedule, patchSchedule, deleteSchedule } from "./schedules.controller.js";

export const schedulesRouter = Router();

schedulesRouter.get("/", listSchedules);
schedulesRouter.get("/:id", getSchedule);

schedulesRouter.post("/", requireAuth, createSchedule);
schedulesRouter.patch("/:id", requireAuth, patchSchedule);
schedulesRouter.delete("/:id", requireAuth, deleteSchedule);
