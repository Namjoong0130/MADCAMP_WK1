import { Router } from "express";
import path from "path";
import fs from "fs";
import { parse } from "yaml";
import swaggerUi from "swagger-ui-express";

import { authRouter } from "./routes/auth.route.js";
import { meRouter } from "./routes/me.route.js";
import { schoolsRouter } from "./routes/schools.route.js";
import { schedulesRouter } from "./routes/schedules.route.js";
import { infoRouter } from "./routes/info.route.js";
import { tagsRouter } from "./routes/tags.route.js";
import { cheerRouter } from "./routes/cheerMethods.route.js";
import { postsRouter } from "./routes/posts.route.js";
import { commentsRouter } from "./routes/comments.route.js";
import { mediaRouter } from "./routes/media.route.js";
import { deviceTokensRouter } from "./routes/deviceTokens.route.js";
import { notificationsRouter } from "./routes/notifications.route.js";
import { shareLinksRouter } from "./routes/shareLinks.route.js";

export const routes = Router();

// Swagger UI (/docs)
const openapiPath = path.resolve(process.cwd(), "openapi.yaml");
if (fs.existsSync(openapiPath)) {
  const spec = parse(fs.readFileSync(openapiPath, "utf8"));
  routes.use("/docs", swaggerUi.serve, swaggerUi.setup(spec));
}

// Modules
routes.use("/auth", authRouter);
routes.use("/me", meRouter);
routes.use("/schools", schoolsRouter);
routes.use("/schedules", schedulesRouter);
routes.use("/info-categories", infoRouter); // GET /info-categories
routes.use("/infos", infoRouter);           // /infos...
routes.use("/tags", tagsRouter);
routes.use("/cheer-methods", cheerRouter);
routes.use("/posts", postsRouter);          // /posts + /posts/:id/like
routes.use("/", commentsRouter);            // /posts/:id/comments, /comments/:id
routes.use("/media", mediaRouter);
routes.use("/device-tokens", deviceTokensRouter);
routes.use("/notifications", notificationsRouter);
routes.use("/share-links", shareLinksRouter);
