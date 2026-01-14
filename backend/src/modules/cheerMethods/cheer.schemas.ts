import { z } from "zod";

export const CheerTapSchema = z.object({
  matchId: z.string().min(1),
  teamId: z.string().min(1),
  count: z.number().int().min(1).max(50), // 앱은 1만 보내지만, 비정상 폭주 방지
});

export type CheerTapInput = z.infer<typeof CheerTapSchema>;