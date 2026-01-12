import { z } from "zod";

export const MeUpdateSchema = z.object({
  nickname: z.string().min(1).optional(),
  schoolId: z.string().nullable().optional(),
  profileImageUrl: z.string().nullable().optional(),
});
