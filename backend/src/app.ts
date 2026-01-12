// src/app.ts
import express from "express";
import morgan from "morgan";
import { routes } from "./routes.js";
import { notFound } from "./middlewares/notFound.js";
import { errorMiddleware } from "./middlewares/error.js";

import swaggerUi from "swagger-ui-express";
import YAML from "yamljs";
import path from "path";
import fs from "fs";

export function createApp() {
  const app = express();

  app.use(express.json({ limit: "2mb" }));
  app.use(morgan("dev"));

  app.get(["/health", "/api/health"], (_req, res) => res.json({ ok: true }));

  // ✅ Swagger/OpenAPI (docs/openapi.yaml 기준)
  const openapiPath = path.resolve(process.cwd(), "docs", "openapi.yaml");
  if (!fs.existsSync(openapiPath)) {
    console.warn("[swagger] docs/openapi.yaml not found:", openapiPath);
  } else {
    // 스펙 파일을 그대로 서빙 (Swagger UI가 이 URL로 fetch)
    app.get("/openapi.yaml", (_req, res) => {
      res.sendFile(openapiPath);
    });

    // Swagger UI는 url로 스펙을 불러오게 고정 (가장 안정적)
    app.use(
      "/docs",
      swaggerUi.serve,
      swaggerUi.setup(undefined, {
        explorer: true,
        swaggerOptions: {
          url: "/openapi.yaml",
          persistAuthorization: true,
        },
      })
    );

    console.log("[swagger] ui: /docs");
    console.log("[swagger] spec: /openapi.yaml");
  }

  app.use("/api",routes);
  app.use(notFound);
  app.use(errorMiddleware);

  return app;
}
