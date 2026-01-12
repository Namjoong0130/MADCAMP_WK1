// src/server.ts
import { createApp } from "./app.js";
import { env } from "./config/env.js";

const app = createApp();

// 서버는 외부 접속을 위해 보통 0.0.0.0로 리슨(EC2/Nginx 환경에서 중요)
const host = env.NODE_ENV === "production" ? "0.0.0.0" : "127.0.0.1";

app.listen(env.PORT, host, () => {
  const baseUrl =
    env.PUBLIC_BASE_URL && env.PUBLIC_BASE_URL.length > 0
      ? env.PUBLIC_BASE_URL
      : `http://localhost:${env.PORT}`;

  // 터미널에서 cmd+클릭 잘 되는 형태
  console.log(`[server] listening on ${host}:${env.PORT} (${env.NODE_ENV})`);
  console.log(`[server] base url: ${baseUrl}`);
  console.log(`[server] docs: ${baseUrl}/docs`);
  console.log(`[server] health: ${baseUrl}/health`);
});
