import { Router } from "express";
import { listSchools, getSchool } from "./schools.controller.js";

export const schoolsRouter = Router();
schoolsRouter.get("/", listSchools);
schoolsRouter.get("/:id", getSchool);
