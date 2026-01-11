import { asyncHandler } from "../../utils/asyncHandler.js";
import { SchoolsService } from "./schools.service.js";
import { HttpError } from "../../utils/httpError.js";

const service = new SchoolsService();

export const listSchools = asyncHandler(async (_req, res) => {
  const result = await service.list();
  res.json(result);
});

export const getSchool = asyncHandler(async (req, res) => {
  const id = req.params.id;
  if (typeof id !== 'string') throw new HttpError(400, "Invalid id", "INVALID_ID");
  const school = await service.detail(id);
  if (!school) throw new HttpError(404, "School not found", "SCHOOL_NOT_FOUND");
  res.json(school);
});
