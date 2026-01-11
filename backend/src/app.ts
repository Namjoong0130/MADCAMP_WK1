import express from "express";
import cors from "cors";
import morgan from "morgan";
import { env } from "./config/env.js";
import { routes } from "./routes.js";
import { errorMiddleware } from "./middlewares/error.js";

export function createApp() {
  const app = express();

  app.use(cors({ origin: env.CORS_ORIGIN === "*" ? true : env.CORS_ORIGIN, credentials: true }));
  app.use(express.json({ limit: "2mb" }));
  app.use(morgan("dev"));

  app.get("/health", (_req, res) => res.json({ ok: true }));

  app.use(routes);

  app.use(errorMiddleware);
  return app;
}
