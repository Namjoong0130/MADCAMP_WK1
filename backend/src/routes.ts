import { Router } from "express";
import path from "path";
import fs from "fs";
import { parse } from "yaml";
import swaggerUi from "swagger-ui-express";

import { authRouter } from "./modules/auth/auth.router.js";
import { meRouter } from "./modules/me/me.router.js";
import { schoolsRouter } from "./modules/schools/schools.router.js";
import { schedulesRouter } from "./modules/schedules/schedules.router.js";
import { infoRouter } from "./modules/info/info.router.js";
import { tagsRouter } from "./modules/tags/tags.router.js";
import { cheerRouter } from "./modules/cheerMethods/cheer.router.js";
import { postsRouter } from "./modules/posts/posts.router.js";
import { commentsRouter } from "./modules/comments/comments.router.js";
import { mediaRouter } from "./modules/media/media.router.js";
import { deviceTokensRouter } from "./modules/deviceTokens/deviceTokens.router.js";
import { notificationsRouter } from "./modules/notifications/notifications.router.js";
import { shareLinksRouter } from "./modules/shareLinks/shareLinks.router.js";

export const routes = Router();

// [1] Swagger UI
const openapiPath = path.resolve(process.cwd(), "docs", "openapi.yaml");
if (fs.existsSync(openapiPath)) {
  const spec = parse(fs.readFileSync(openapiPath, "utf8"));
  routes.use("/docs", swaggerUi.serve, swaggerUi.setup(spec));
}

// [2] Modules Mapping
routes.use("/auth", authRouter);
routes.use("/me", meRouter);
routes.use("/schools", schoolsRouter);
routes.use("/schedules", schedulesRouter);
routes.use("/info-categories", infoRouter);
routes.use("/infos", infoRouter);
routes.use("/tags", tagsRouter);

// ✅ 핵심 수정: 안드로이드에서 요청하는 /api/cheer 경로를 여기로 연결합니다.
routes.use("/cheer", cheerRouter);

routes.use("/posts", postsRouter);
routes.use("/media", mediaRouter);
routes.use("/device-tokens", deviceTokensRouter);
routes.use("/notifications", notificationsRouter);
routes.use("/share-links", shareLinksRouter);

// [3] Root Path Modules
routes.use("/", commentsRouter);