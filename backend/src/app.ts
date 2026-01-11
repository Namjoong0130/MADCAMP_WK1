// src/app.ts
import express from "express";
import morgan from "morgan";
import { routes } from "./routes.js";
import { notFound } from "./middlewares/notFound.js";
import { errorMiddleware } from "./middlewares/error.js";

export function createApp() {
  const app = express();

  app.use(express.json({ limit: "2mb" }));
  app.use(morgan("dev"));

  app.get("/health", (_req, res) => res.json({ ok: true }));

  app.use(routes);
  app.use(notFound);
  app.use(errorMiddleware);

  return app;
}
