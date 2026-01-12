import express from "express";
import swaggerUi from "swagger-ui-express";
import YAML from "yamljs";
import path from "path";

const app = express();

app.use(express.json());

// ✅ docs/openapi.yaml 경로 (backend 기준)
const openapiPath = path.resolve(process.cwd(), "docs", "openapi.yaml");
const swaggerDoc = YAML.load(openapiPath);

// ✅ Swagger UI
app.use("/docs", swaggerUi.serve, swaggerUi.setup(swaggerDoc, {
  explorer: true,
  swaggerOptions: {
    persistAuthorization: true, // Bearer 토큰 입력 유지
  },
}));

// 예: 기존 API 라우팅이 /api 아래라면
// app.use("/api", apiRouter);

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Server listening on :${port}`);
  console.log(`Swagger UI: http://localhost:${port}/docs`);
});
