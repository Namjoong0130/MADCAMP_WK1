# UNIVS (유니브스)

**한 줄 소개**  
UNIVS는 더 나은 카포전, 포카전을 위한 다기능 통합 어플리케이션입니다.

**슬로건**  
대학 간 교류의 장을 펼치자

---

## ✨ Features

- JWT 기반 로그인/인증
- 게시판(게시글/태그/이미지 업로드)
- 좋아요 & 댓글 (구현/확장 예정 또는 적용 범위에 맞게 수정)
- 학교 기반 UI(학교 뱃지/브랜드 컬러 적용)
- EC2 + RDS 배포 운영 (PM2, Nginx)

---

## 🛠 Tech Stack

### Frontend
- **Kotlin** (Android)

### Backend
- **Node.js + Express (TypeScript)**
- **Prisma + MySQL (AWS RDS)**
- **Zod** (Request Validation)

### Infra / DevOps
- **AWS EC2 & RDS**
- **Nginx**
- **PM2**
- **JWT Authentication**

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (최신 권장)
- JDK 11 이상
- (Backend) Node.js 18+ 권장, npm
- (Backend) MySQL(RDS 사용 시 별도 준비) 또는 로컬 MySQL

---

## 📦 Installation

### 1) 저장소 클론
```bash
git clone <YOUR_GITHUB_REPO_URL>
cd UNIVS
