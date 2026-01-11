// src/db/prisma.ts
import mariadb from "mariadb";
import { PrismaClient } from "../generated/prisma/client.js";
import { env } from "../config/env.js";

// 패키지에서 export 이름이 케이스에 따라 다를 수 있어 둘 중 하나를 쓰세요.
// 1) PrismaMariaDb
import { PrismaMariaDb } from "@prisma/adapter-mariadb";
// 2) 만약 위가 import 에러면 아래로 바꾸세요.
// import { PrismaMariaDB as PrismaMariaDb } from "@prisma/adapter-mariadb";

function createMariaDbPool(databaseUrl: string) {
  const url = new URL(databaseUrl); // mysql://user:pass@host:3306/db
  return mariadb.createPool({
    host: url.hostname,
    port: Number(url.port || 3306),
    user: decodeURIComponent(url.username),
    password: decodeURIComponent(url.password),
    database: url.pathname.replace(/^\//, ""),
    connectionLimit: Number(process.env.DB_POOL_LIMIT ?? 10),
    // 운영에서 안정적으로: acquireTimeout 등 필요하면 추가
    acquireTimeout: Number(process.env.DB_ACQUIRE_TIMEOUT_MS ?? 10000),
  });
}

const pool = createMariaDbPool(env.DATABASE_URL);
const adapter = new PrismaMariaDb(env.DATABASE_URL);

export const prisma = new PrismaClient({ adapter });
