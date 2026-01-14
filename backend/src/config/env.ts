// src/config/env.ts
import path from "path";
import fs from "fs";
import dotenv from "dotenv";
import { z } from "zod";

const nodeEnv = process.env.NODE_ENV ?? "development";

// 우선순위:
// 1) production이면 .env
// 2) development/test면 .env.development가 있으면 그걸
// 3) 없으면 .env
const candidates =
  nodeEnv === "production"
    ? [".env"]
    : [".env.development", ".env"];

const picked = candidates.find((f) => fs.existsSync(path.resolve(process.cwd(), f)));

if (picked) dotenv.config({ path: path.resolve(process.cwd(), picked) });

// 검증 스키마
const EnvSchema = z.object({
  NODE_ENV: z.string().default("development"),
  PORT: z.coerce.number().default(3000),

  DATABASE_URL: z.string().min(1),

  JWT_ACCESS_SECRET: z.string().min(32),
  JWT_REFRESH_SECRET: z.string().min(32),
  JWT_ACCESS_EXPIRES_IN: z.string().default("15m"),
  JWT_REFRESH_EXPIRES_IN: z.string().default("30d"),

  PUBLIC_BASE_URL: z.string().default("http://localhost:3000"),

  AWS_REGION: z.string().optional(),
  S3_BUCKET: z.string().optional(),
});
console.log("[env] NODE_ENV =", nodeEnv);
console.log("[env] CWD =", process.cwd());
console.log("[env] picked =", picked);

export const env = EnvSchema.parse(process.env);
