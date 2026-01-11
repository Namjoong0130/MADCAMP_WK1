import { asyncHandler } from "../../utils/asyncHandler.js";
import { SchedulesService } from "./schedules.service.js";
import { ScheduleCreateSchema, ScheduleUpdateSchema } from "./schedules.schemas.js";
import { HttpError } from "../../utils/httpError.js";

const service = new SchedulesService();

export const listSchedules = asyncHandler(async (req, res) => {
  const page = await service.list(req.query);
  res.json(page);
});

export const getSchedule = asyncHandler(async (req, res) => {
  const id = req.params.id;
  if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
  const item = await service.get(id);
  if (!item) throw new HttpError(404, "Schedule not found", "SCHEDULE_NOT_FOUND");
  res.json(item);
});

export const createSchedule = asyncHandler(async (req, res) => {
  if (!req.user?.id) throw new HttpError(401, "Unauthorized", "UNAUTHORIZED");
  const body = ScheduleCreateSchema.parse(req.body);
  const item = await service.create(req.user.id, body);
  res.status(201).json(item);
});

export const patchSchedule = asyncHandler(async (req, res) => {
  if (!req.user?.id) throw new HttpError(401, "Unauthorized", "UNAUTHORIZED");
  const id = req.params.id;
  if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
  const body = ScheduleUpdateSchema.parse(req.body);
  const item = await service.patch(id, req.user.id, body);
  res.json(item);
});

export const deleteSchedule = asyncHandler(async (req, res) => {
  if (!req.user?.id) throw new HttpError(401, "Unauthorized", "UNAUTHORIZED");
  const id = req.params.id;
  if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
  await service.remove(id);
  res.status(204).send();
});
