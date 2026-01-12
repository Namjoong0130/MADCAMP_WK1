import { asyncHandler } from "../../utils/asyncHandler.js";
import { AuthService } from "./auth.service.js";
import { RegisterSchema, LoginSchema, RefreshSchema, LogoutSchema } from "./auth.schemas.js";

const service = new AuthService();

export const register = asyncHandler(async (req, res) => {
  const body = RegisterSchema.parse(req.body);
  const result = await service.register(body);
  res.status(201).json(result);
});

export const login = asyncHandler(async (req, res) => {
  const body = LoginSchema.parse(req.body);
  const result = await service.login(body);
  res.json(result);
});

export const refresh = asyncHandler(async (req, res) => {
  const body = RefreshSchema.parse(req.body);
  const result = await service.refresh(body);
  res.json(result);
});

export const logout = asyncHandler(async (req, res) => {
  const body = LogoutSchema.parse(req.body ?? {});
  await service.logout(body);
  res.status(204).send();
});
