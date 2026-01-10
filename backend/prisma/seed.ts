// backend/prisma/seed.ts
import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

async function main() {
  // (선택) 시드 반복 실행 시 중복 방지용 정리
  // 관계 제약 때문에 에러가 나면 일단 이 deleteMany 블록을 주석 처리하고 진행하세요.
  await prisma.shareLink?.deleteMany?.().catch(() => {});
  await prisma.schedule?.deleteMany?.().catch(() => {});
  await prisma.info?.deleteMany?.().catch(() => {});
  await prisma.cheerMethod?.deleteMany?.().catch(() => {});
  await prisma.infoTag?.deleteMany?.().catch(() => {});
  await prisma.infoCategory?.deleteMany?.().catch(() => {});
  await prisma.school?.deleteMany?.().catch(() => {});

  // 1) School
  await prisma.school.createMany({
    data: [{ name: "KAIST" }, { name: "POSTECH" }, { name: "연세대" }, { name: "고려대" }],
    skipDuplicates: true,
  });

  const kaist = await prisma.school.findFirst({ where: { name: "KAIST" } });
  const postech = await prisma.school.findFirst({ where: { name: "POSTECH" } });
  if (!kaist || !postech) throw new Error("Seed: school create failed");

  // 2) Schedule
  await prisma.schedule.createMany({
    data: [
      {
        title: "개막식",
        description: "개막식",
        startAt: new Date("2025-09-19T12:00:00.000Z"),
        endAt: new Date("2025-09-19T13:40:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        title: "e-Sports",
        description: "e-Sports",
        startAt: new Date("2025-09-19T14:30:00.000Z"),
        endAt: new Date("2025-09-19T17:00:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "대전이스포츠경기장",
        address: "대한민국 대전광역시 유성구 대덕대로 480 대전 이스포츠경기장",
        lat: 36.376904,
        lng: 127.3872357,
      },
      {
        title: "AI",
        description: "AI",
        startAt: new Date("2025-09-19T17:00:00.000Z"),
        endAt: new Date("2025-09-19T18:00:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "대전이스포츠경기장",
        address: "대한민국 대전광역시 유성구 대덕대로 480 대전 이스포츠경기장",
        lat: 36.376904,
        lng: 127.3872357,
      },
      {
        title: "교류공연 & 영상제",
        description: "교류공연 & 영상제",
        startAt: new Date("2025-09-19T18:45:00.000Z"),
        endAt: new Date("2025-09-19T20:45:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        title: "축구",
        description: "축구",
        startAt: new Date("2025-09-19T21:30:00.000Z"),
        endAt: new Date("2025-09-19T23:30:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "KAIST 대운동장",
        address: "대전광역시 유성구 대학로 291",
        lat: 36.36955117920298,
        lng: 127.36847011943173,
      },
      {
        title: "야구",
        description: "야구",
        startAt: new Date("2025-09-20T10:00:00.000Z"),
        endAt: new Date("2025-09-20T12:30:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "북측운동장",
        address: "대전광역시 유성구 대학로 291",
        lat: 36.372351932127145,
        lng: 127.36022599968764,
      },
      {
        title: "과학퀴즈",
        description: "과학퀴즈",
        startAt: new Date("2025-09-20T13:30:00.000Z"),
        endAt: new Date("2025-09-20T15:00:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        title: "농구",
        description: "농구",
        startAt: new Date("2025-09-20T15:30:00.000Z"),
        endAt: new Date("2025-09-20T18:00:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        title: "폐막식",
        description: "폐막식",
        startAt: new Date("2025-09-20T19:00:00.000Z"),
        endAt: new Date("2025-09-20T22:00:00.000Z"),
        homeSchoolId: kaist.id,
        awaySchoolId: postech.id,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
    ],
    skipDuplicates: true,
  });

  // 3) InfoCategory / Info (모델이 있으면 동작)
  const catFood = await prisma.infoCategory
    ?.create({ data: { name: "식사" } })
    .catch(() => null);

  await prisma.info.createMany({
    data: [
      {
        categoryId: catFood?.id ?? null,
        title: "카이마루",
        content: "점심 피크 시간(12~1시) 피해 방문 추천",
        sourceUrl: null,
        locationName: "카이마루",
        address: "대전광역시 유성구 대학로 291 카이마루",
        lat: 36.373769873203166,
        lng: 127.35919617360102,
      },
    ],
    skipDuplicates: true,
  });

  console.log("✅ Seed completed");
}

main()
  .catch((e) => {
    console.error("❌ Seed failed", e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
