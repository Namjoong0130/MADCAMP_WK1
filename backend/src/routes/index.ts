// src/routes/index.ts
import { Router } from "express";

// 이미 있는 라우터들 import
import { authRouter } from "./auth.route.js";
import { meRouter } from "./me.route.js";
import { schoolsRouter } from "./schools.route.js";
import { schedulesRouter } from "./schedules.route.js";
import { tagsRouter } from "./tags.route.js";
import { infoRouter } from "./info.route.js";
import { cheerRouter } from "./cheerMethods.route.js";
import { postsRouter } from "./posts.route.js";
import { commentsRouter } from "./comments.route.js";
import { mediaRouter } from "./media.route.js";
import { deviceTokensRouter } from "./deviceTokens.route.js";
import { notificationsRouter } from "./notifications.route.js";
import { shareLinksRouter } from "./shareLinks.route.js";

export const routes = Router();

routes.use("/auth", authRouter);
routes.use("/me", meRouter);

routes.use("/schools", schoolsRouter);
routes.use("/schedules", schedulesRouter);

routes.use("/tags", tagsRouter);
routes.use("/info-categories", infoRouter);
routes.use("/infos", infoRouter);
routes.use("/cheer-methods", cheerRouter);

routes.use("/posts", postsRouter);
routes.use("/", commentsRouter); // /posts/:id/comments, /comments/:id 같은 형태라면

routes.use("/media", mediaRouter);
routes.use("/device-tokens", deviceTokensRouter);
routes.use("/notifications", notificationsRouter);
routes.use("/share-links", shareLinksRouter);
