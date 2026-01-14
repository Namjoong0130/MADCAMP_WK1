import { asyncHandler } from "../../utils/asyncHandler.js";
import { MeService } from "./me.service.js";
import { MeUpdateSchema } from "./me.schemas.js";
import { HttpError } from "../../utils/httpError.js";

const service = new MeService();

export const getMe = asyncHandler(async (req, res) => {
  if (!req.user?.id) throw new HttpError(401, "Unauthorized", "UNAUTHORIZED");
  const me = await service.getMe(req.user.id);
  res.json(me);
});

export const patchMe = asyncHandler(async (req, res) => {
  if (!req.user?.id) throw new HttpError(401, "Unauthorized", "UNAUTHORIZED");
  const body = MeUpdateSchema.parse(req.body);
  const me = await service.updateMe(req.user.id, body);
  res.json(me);
});
