# UNIVS (유니브스)

> **UNIVS는 더 나은 카포전, 포카전을 위한 다기능 통합 어플리케이션입니다.**  
> 슬로건: **대학 간 교류의 장을 펼치자**

- **Frontend**: Android (Kotlin / Jetpack Compose)
- **Backend**: Node.js + Express (TypeScript) / Prisma / MySQL(RDS) / Zod
- **Infra**: AWS EC2 + RDS / Nginx / PM2 / JWT Auth

---

## 목차
- [1. 프로젝트 개요](#1-프로젝트-개요)
- [2. 핵심 기능](#2-핵심-기능)
- [3. 시스템 아키텍처](#3-시스템-아키텍처)
- [4. 기술 스택](#4-기술-스택)
- [5. 폴더 구조](#5-폴더-구조)
- [6. 데이터 모델 요약](#6-데이터-모델-요약)
- [7. API 요약](#7-api-요약)
- [8. 실행 방법](#8-실행-방법)
- [9. 배포 방법](#9-배포-방법)
- [10. 트러블슈팅](#10-트러블슈팅)
- [11. 팀/역할](#11-팀역할)
- [12. 로드맵](#12-로드맵)

---

## 1. 프로젝트 개요

UNIVS는 대학 간 교류전(카포전/포카전)에서 필요한 정보를 한 곳에 모아 제공하고,  
게시판/응원/일정/정보 기능을 통해 **행사 경험을 개선하는 통합 앱**입니다.

### 프로젝트 목표
- 행사 기간에 흩어진 정보를 통합 제공(일정/공지/팁/Q&A 등)
- **학교 기반 UX**: 색상/뱃지/상호작용(응원전 등)
- 안정적인 운영(EC2+RDS), 빠른 배포(PM2), 트래픽 대응(Nginx Reverse Proxy)

---

## 2. 핵심 기능

### 게시판
- 게시글 작성/목록/상세
- 태그 기반 분류 (공지/소통/꿀팁/Q&A)
- 이미지 첨부 (HTTP URL 또는 Base64 Data URL)
- 좋아요 / 댓글 (토글식 좋아요, 댓글 작성 및 목록)

### 학교 기반 UI
- 작성자 학교 뱃지 + 색상
- 학교에 따른 브랜드 컬러(예: KAIST/POSTECH)
- 댓글 “대화형 UI” (내 학교는 우측, 상대 학교는 좌측)

### 운영/인증
- JWT 인증 기반 API 보호
- PM2 프로세스 관리 + Nginx Reverse Proxy
- MySQL(RDS) 기반 데이터 관리 + Prisma Migrate/Seed

---

## 3. 시스템 아키텍처

```

[Android App (Kotlin)]
|
|  HTTPS / HTTP (REST API)
v
[Nginx (Reverse Proxy)]  --->  /api  --->  [Express (TypeScript) + Prisma]
|
v
[MySQL (AWS RDS)]

```

- Nginx가 `/api` 요청을 백엔드로 프록시합니다.
- 백엔드는 Prisma로 RDS(MySQL)와 연결됩니다.
- 인증이 필요한 API는 `Authorization: Bearer <token>`을 요구합니다.

---

## 4. 기술 스택

### Frontend
- Kotlin
- Jetpack Compose
- Retrofit + OkHttp (JWT Interceptor)
- Coil (이미지 로딩)

### Backend
- Node.js + Express (TypeScript)
- Prisma + MySQL
- Zod (Request validation)
- Swagger UI (OpenAPI)

### Infra/DevOps
- AWS EC2, AWS RDS
- Nginx, PM2
- JWT (Access/Refresh)

---

## 5. 폴더 구조

> 실제 저장소에 맞춰 이름이 다를 수 있으니, 아래는 “권장/대표 구조”입니다.

### Backend
```

backend/
prisma/
schema.prisma
migrations/
seed.ts
src/
app.ts
server.ts
routes.ts
db/
prisma.ts
middlewares/
auth.ts
error.ts
notFound.ts
modules/
posts/
posts.router.ts
media/
media.router.ts
comments/
comments.router.ts
likes/
likes.router.ts
utils/
jwt.ts
pagination.ts
asyncHandler.ts
httpError.ts
docs/
openapi.yaml

```

### Android
```

app/src/main/java/.../
data/
api/
PostApiService.kt
RetrofitClient.kt
model/
ApiDtos.kt
ui/
screen/
dashboard/
dashboardScreen.kt
dashboardViewModel.kt
article/
articleScreen.kt
articleViewModel.kt
write/
writeScreen.kt
writeViewModel.kt
util/
UiMappings.kt
dataUrlToImageBitmapOrNull.kt

````

---

## 6. 데이터 모델 요약

### 게시판 핵심
- `Post` : 게시글(작성자/내용/좋아요수/댓글수/공개범위)
- `Comment` : 댓글(대댓글 구조 `parentId`)
- `PostLike` : 좋아요 (userId + postId 복합키)
- `Tag`, `PostTag` : 태그 및 게시글-태그 M:N
- `Media`, `PostMedia` : 이미지 및 게시글-이미지 M:N

### 이미지 저장 정책 (핵심)
- `Media.url`은 **LongText**
- HTTP URL과 Base64 Data URL을 모두 허용
- Android 업로드 시 base64가 잘리지 않도록 Express body limit 상향 필요

### 응원전(확장 기능)
- `CheerTeam` / `CheerMatch` / `CheerTap`
- 사용자별/매치별/팀별 카운트 누적(무한 row 생성 방지)

---

## 7. API 요약

> 실제 상세 스펙은 `/docs` (Swagger UI)에서 확인합니다.

### Auth
- `POST /api/auth/login` : 로그인 (access/refresh 토큰 발급)
- `POST /api/auth/refresh` : 토큰 재발급

### Posts
- `GET /api/posts?tag=&limit=&cursor=` : 목록(대표 이미지 1장 포함)
- `GET /api/posts/:id` : 상세(이미지 전체 + 태그)
- `POST /api/posts` : 작성(태그/이미지 연결)
- `PATCH /api/posts/:id` : 수정(작성자/관리자)
- `DELETE /api/posts/:id` : 삭제(작성자/관리자)

### Media
- `POST /api/media` : media 생성(이미지 URL/base64 저장 후 id 반환)

### Likes
- `POST /api/posts/:id/like` : 좋아요 토글(좋아요/취소 + likeCount 갱신)

### Comments
- `GET /api/posts/:id/comments` : 댓글 목록(스레드 포함)
- `POST /api/posts/:id/comments` : 댓글 작성(parentId로 대댓글 가능)

---

## 8. 실행 방법

## 8.1 Backend 실행

### Prerequisites
- Node.js 18+ 권장
- MySQL (로컬 또는 RDS)
- `.env` 준비

### 설치 및 실행
```bash
cd backend
npm install

# Prisma
npx prisma generate
npx prisma migrate deploy
npm run db:seed

# 서버 실행
npm run dev
````

### 헬스체크

* `GET http://localhost:3000/health`
* `GET http://localhost:3000/api/health`

### Swagger

* `GET http://localhost:3000/docs`

---

## 8.2 Android 실행

### Prerequisites

* Android Studio 최신 권장
* JDK 11 이상

### 실행

1. Android Studio에서 프로젝트 Open
2. Gradle Sync
3. Run (에뮬레이터 또는 실기기)

### Base URL 주의

* 에뮬레이터에서 로컬 백엔드 접근 시: `http://10.0.2.2:3000/api`
* EC2 배포 백엔드 접근 시: `http://<EC2_PUBLIC_IP>/api`

---

## 9. 배포 방법 (EC2 + RDS + Nginx + PM2)

### 9.1 환경변수(운영)

운영 `.env`에서 반드시 설정:

```env
NODE_ENV=production
PORT=3000
DATABASE_URL="mysql://<USER>:<PASSWORD>@<RDS_ENDPOINT>:3306/<DB_NAME>"

JWT_ACCESS_SECRET="..."
JWT_REFRESH_SECRET="..."
JWT_ACCESS_EXPIRES_IN="1d"
JWT_REFRESH_EXPIRES_IN="14d"
```

### 9.2 Nginx Reverse Proxy (개요)

* `/api` 요청을 Node(3000)로 프록시
* 필요 시 gzip/timeout/body size 조정

### 9.3 배포 플로우(예시)

```bash
# EC2 접속
cd ~/apps/UNIVS/backend
git pull
npm install

npx prisma migrate deploy
npm run db:seed   # 초기화 필요 시

pm2 restart madcamp-backend --update-env
sudo systemctl reload nginx
```

---

## 10. 트러블슈팅

### (1) 이미지가 안 보이거나 `incomplete input` 오류

* base64 데이터가 **잘려서 저장**되면 디코더가 실패합니다.
* 해결:

  * Express limit 상향:

    * `express.json({ limit: "20mb" })`
    * `express.urlencoded({ limit: "20mb", extended: true })`
  * DB 컬럼이 LongText인지 확인:

    * `SHOW COLUMNS FROM Media LIKE 'url';`
  * API 응답에서 길이 확인:

    * `jq -r '.medias[0].url | length'`

### (2) migrate drift / reset 필요

* 개발/운영 DB가 migration history와 어긋나면 drift가 발생합니다.
* 데이터가 지워져도 되면:

  1. DB drop/create
  2. `npx prisma migrate deploy`
  3. `npm run db:seed`

### (3) 태그가 보이지 않음

* 프론트는 “tag 이름”을 보내는데 백엔드는 “tagId”를 기대하는 구조일 수 있습니다.
* 해결 방향:

  * API에서 tag를 **id+name**으로 내려주고
  * 프론트는 표시용은 `name`, 필터링은 `tag` 파라미터(이름 기준)로 맞추는 방식 권장

---

## 11. 팀/역할

* **임남중** : POSTECH 컴퓨터공학과 24학번
* **이준엽** : KAIST 기술경영학부 22학번

---

## 12. 로드맵

### v1 (현재)

* 게시판(작성/목록/상세)
* 태그/이미지 업로드
* 인증/배포 운영

### v1.1

* 좋아요 토글 안정화 + UI/UX 개선(학교 뱃지 포함)
* 댓글(작성/표시) 대화형 UI

### v2

* 응원전(버튼 탭 경쟁) + 실시간 집계
* 알림(댓글/좋아요/공지) 확장

---
