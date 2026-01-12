import { z } from "zod";

export const ScheduleCreateSchema = z.object({
  title: z.string().min(1),
  description: z.string().nullable().optional(),
  startAt: z.string().datetime(),
  endAt: z.string().datetime().nullable().optional(),
  status: z.string().optional(),
  locationName: z.string().nullable().optional(),
  address: z.string().nullable().optional(),
  lat: z.number().nullable().optional(),
  lng: z.number().nullable().optional(),
  homeSchoolId: z.string().nullable().optional(),
  awaySchoolId: z.string().nullable().optional(),
});

export const ScheduleUpdateSchema = ScheduleCreateSchema.partial();
