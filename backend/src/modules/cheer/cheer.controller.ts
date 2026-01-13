import type { Request, Response } from "express";
import { CheerTapSchema } from "./cheer.schemas.js";
import { getActiveMatchWithTotals, tapCheer } from "./cheer.service.js";

export async function getActiveMatch(_req: Request, res: Response) {
  const data = await getActiveMatchWithTotals();
  res.json(data);
}

export async function postCheerTap(req: Request, res: Response) {
  const body = CheerTapSchema.parse(req.body);
  const userId = req.user!.id;

  const result = await tapCheer(body, userId);

  // 앱은 응답으로 totals만 받아도 충분
  res.status(201).json({
    ok: true,
    tap: result.tap,
    homeTotalTaps: result.homeTotalTaps,
    awayTotalTaps: result.awayTotalTaps,
  });
}
