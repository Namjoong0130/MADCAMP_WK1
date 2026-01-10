// backend/prisma/seed.ts
import { PrismaClient, Platform, Visibility, UserRole, NotificationType, ShareEntityType } from "@prisma/client";

const prisma = new PrismaClient();

/**
 * seed에서 "고정 id"를 쓰면:
 * - ShareLink.entityId 같은 참조도 안정적으로 고정 가능
 * - seed 반복 실행 시에도 매번 같은 레코드를 upsert로 관리 가능
 */
const IDS = {
  schools: {
    KAIST: "seed_school_kaist",
    POSTECH: "seed_school_postech",
    YONSEI: "seed_school_yonsei",
    KOREA: "seed_school_korea",
  },
  users: {
    ADMIN: "seed_user_admin",
    KAIST_1: "seed_user_kaist_1",
    POSTECH_1: "seed_user_postech_1",
  },
  tags: {
    FOOD: "seed_tag_food",
    CAFE: "seed_tag_cafe",
    TRANSPORT: "seed_tag_transport",
    EVENT: "seed_tag_event",
    NOTICE: "seed_tag_notice",
  },
  infoCategories: {
    FOOD: "seed_info_cat_food",
    CAFE: "seed_info_cat_cafe",
    TRANSPORT: "seed_info_cat_transport",
    CAMPUS: "seed_info_cat_campus",
  },
  infos: {
    KAIMARU: "seed_info_kaimaru",
    DINING_TIP: "seed_info_dining_tip",
    CAFE_SPOT: "seed_info_cafe_spot",
    BUS_GUIDE: "seed_info_bus_guide",
  },
  cheerMethods: {
    KAIST_1: "seed_cheer_kaist_1",
    POSTECH_1: "seed_cheer_postech_1",
  },
  schedules: {
    OPENING: "seed_schedule_opening",
    ESPORTS: "seed_schedule_esports",
    AI: "seed_schedule_ai",
    SHOW: "seed_schedule_show",
    SOCCER: "seed_schedule_soccer",
    BASEBALL: "seed_schedule_baseball",
    QUIZ: "seed_schedule_quiz",
    BASKETBALL: "seed_schedule_basketball",
    CLOSING: "seed_schedule_closing",
  },
  posts: {
    WELCOME: "seed_post_welcome",
    NOTICE_1: "seed_post_notice_1",
  },
  medias: {
    POSTER_1: "seed_media_poster_1",
  },
  shareLinks: {
    SCH_OPENING: "seed-schedule-opening",
    INFO_KAIMARU: "seed-info-kaimaru",
    CHEER_KAIST: "seed-cheer-kaist-1",
    POST_WELCOME: "seed-post-welcome",
  },
};

/** bcrypt 해시처럼 보이는 더미 문자열(로그인 구현 전용) */
const DUMMY_PASSWORD_HASH = "$2b$10$seedseedseedseedseedseedseedseedseedseedseedseedse";

async function main() {
  /**
   * 0) (선택) 기존 seed 데이터만 "안전하게" 정리
   * - FK 관계 때문에 전체 deleteMany는 순서가 중요합니다.
   * - 여기서는 seed가 만든 것만 지우도록 id prefix를 고정해 두었기 때문에
   *   where 조건으로 seed 데이터만 제거합니다.
   */
  // 조인/참조 먼저 제거
  await prisma.postLike.deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } }).catch(() => {});
  await prisma.comment.deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } }).catch(() => {});
  await prisma.postMedia.deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } }).catch(() => {});
  await prisma.postTag.deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } }).catch(() => {});
  await prisma.infoTag.deleteMany({ where: { infoId: { in: Object.values(IDS.infos) } } }).catch(() => {});

  await prisma.notification.deleteMany({ where: { userId: { in: Object.values(IDS.users) } } }).catch(() => {});
  await prisma.deviceToken.deleteMany({ where: { userId: { in: Object.values(IDS.users) } } }).catch(() => {});
  await prisma.refreshToken.deleteMany({ where: { userId: { in: Object.values(IDS.users) } } }).catch(() => {});

  // ShareLink는 slug가 unique라 slug로만 정리하는게 가장 안전
  await prisma.shareLink
    .deleteMany({
      where: { slug: { in: Object.values(IDS.shareLinks) } },
    })
    .catch(() => {});

  // 본 엔티티
  await prisma.post.deleteMany({ where: { id: { in: Object.values(IDS.posts) } } }).catch(() => {});
  await prisma.media.deleteMany({ where: { id: { in: Object.values(IDS.medias) } } }).catch(() => {});
  await prisma.schedule.deleteMany({ where: { id: { in: Object.values(IDS.schedules) } } }).catch(() => {});
  await prisma.cheerMethod.deleteMany({ where: { id: { in: Object.values(IDS.cheerMethods) } } }).catch(() => {});
  await prisma.info.deleteMany({ where: { id: { in: Object.values(IDS.infos) } } }).catch(() => {});
  await prisma.infoCategory.deleteMany({ where: { id: { in: Object.values(IDS.infoCategories) } } }).catch(() => {});
  await prisma.tag.deleteMany({ where: { id: { in: Object.values(IDS.tags) } } }).catch(() => {});
  await prisma.user.deleteMany({ where: { id: { in: Object.values(IDS.users) } } }).catch(() => {});
  await prisma.school.deleteMany({ where: { id: { in: Object.values(IDS.schools) } } }).catch(() => {});

  /**
   * 1) School
   */
  await prisma.school.createMany({
    data: [
      { id: IDS.schools.KAIST, name: "KAIST", shortName: "KAIST" },
      { id: IDS.schools.POSTECH, name: "POSTECH", shortName: "POST" },
      { id: IDS.schools.YONSEI, name: "연세대", shortName: "YONSEI" },
      { id: IDS.schools.KOREA, name: "고려대", shortName: "KOREA" },
    ],
    skipDuplicates: true,
  });

  /**
   * 2) User (필수: email unique, passwordHash, nickname)
   */
  await prisma.user.createMany({
    data: [
      {
        id: IDS.users.ADMIN,
        email: "admin@madcamp.local",
        passwordHash: DUMMY_PASSWORD_HASH,
        nickname: "관리자",
        role: UserRole.ADMIN,
        schoolId: IDS.schools.KAIST,
      },
      {
        id: IDS.users.KAIST_1,
        email: "kaist1@madcamp.local",
        passwordHash: DUMMY_PASSWORD_HASH,
        nickname: "카이스트러",
        role: UserRole.USER,
        schoolId: IDS.schools.KAIST,
      },
      {
        id: IDS.users.POSTECH_1,
        email: "postech1@madcamp.local",
        passwordHash: DUMMY_PASSWORD_HASH,
        nickname: "포스텍러",
        role: UserRole.USER,
        schoolId: IDS.schools.POSTECH,
      },
    ],
    skipDuplicates: true,
  });

  /**
   * 3) Tag
   */
  await prisma.tag.createMany({
    data: [
      { id: IDS.tags.FOOD, name: "맛집" },
      { id: IDS.tags.CAFE, name: "카페" },
      { id: IDS.tags.TRANSPORT, name: "교통" },
      { id: IDS.tags.EVENT, name: "행사" },
      { id: IDS.tags.NOTICE, name: "공지" },
    ],
    skipDuplicates: true,
  });

  /**
   * 4) InfoCategory
   */
  await prisma.infoCategory.createMany({
    data: [
      { id: IDS.infoCategories.FOOD, name: "식사", sortOrder: 1 },
      { id: IDS.infoCategories.CAFE, name: "카페", sortOrder: 2 },
      { id: IDS.infoCategories.TRANSPORT, name: "교통", sortOrder: 3 },
      { id: IDS.infoCategories.CAMPUS, name: "캠퍼스", sortOrder: 4 },
    ],
    skipDuplicates: true,
  });

  /**
   * 5) Info
   */
  await prisma.info.createMany({
    data: [
      {
        id: IDS.infos.KAIMARU,
        categoryId: IDS.infoCategories.FOOD,
        title: "카이마루",
        content: "점심 피크(12~13시) 피해 방문 추천. 메뉴 회전 빠릅니다.",
        sourceUrl: null,
        locationName: "카이마루",
        address: "대전광역시 유성구 대학로 291 카이마루",
        lat: 36.373769873203166,
        lng: 127.35919617360102,
      },
      {
        id: IDS.infos.DINING_TIP,
        categoryId: IDS.infoCategories.FOOD,
        title: "식사 팁",
        content: "카포전 기간에는 학내 식당이 붐빕니다. 11:30 이전/13:30 이후 추천.",
        sourceUrl: null,
        locationName: null,
        address: null,
        lat: null,
        lng: null,
      },
      {
        id: IDS.infos.CAFE_SPOT,
        categoryId: IDS.infoCategories.CAFE,
        title: "카페 추천",
        content: "대화/미팅용 좌석 많은 카페를 우선 추천합니다. 소음 적은 시간대 체크!",
        sourceUrl: null,
        locationName: null,
        address: null,
        lat: null,
        lng: null,
      },
      {
        id: IDS.infos.BUS_GUIDE,
        categoryId: IDS.infoCategories.TRANSPORT,
        title: "교통 가이드",
        content: "대전역/유성터미널 → KAIST 이동은 버스/택시 혼합이 편합니다.",
        sourceUrl: null,
        locationName: null,
        address: null,
        lat: null,
        lng: null,
      },
    ],
    skipDuplicates: true,
  });

  /**
   * 6) InfoTag (M:N)
   * - InfoTag는 @@id([infoId, tagId]) 복합키라 createMany가 가장 간단
   */
  await prisma.infoTag.createMany({
    data: [
      { infoId: IDS.infos.KAIMARU, tagId: IDS.tags.FOOD },
      { infoId: IDS.infos.DINING_TIP, tagId: IDS.tags.FOOD },
      { infoId: IDS.infos.BUS_GUIDE, tagId: IDS.tags.TRANSPORT },
      { infoId: IDS.infos.CAFE_SPOT, tagId: IDS.tags.CAFE },
    ],
    skipDuplicates: true,
  });

  /**
   * 7) CheerMethod
   */
  await prisma.cheerMethod.createMany({
    data: [
      {
        id: IDS.cheerMethods.KAIST_1,
        schoolId: IDS.schools.KAIST,
        title: "KAIST 응원 구호",
        content: "KAIST! KAIST! 파이팅!",
        mediaUrl: null,
      },
      {
        id: IDS.cheerMethods.POSTECH_1,
        schoolId: IDS.schools.POSTECH,
        title: "POSTECH 응원 구호",
        content: "포스텍! 포스텍! 파이팅!",
        mediaUrl: null,
      },
    ],
    skipDuplicates: true,
  });

  /**
   * 8) Schedule
   */
  await prisma.schedule.createMany({
    data: [
      {
        id: IDS.schedules.OPENING,
        title: "개막식",
        description: "개막식",
        startAt: new Date("2025-09-19T12:00:00.000Z"),
        endAt: new Date("2025-09-19T13:40:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        id: IDS.schedules.ESPORTS,
        title: "e-Sports",
        description: "e-Sports",
        startAt: new Date("2025-09-19T14:30:00.000Z"),
        endAt: new Date("2025-09-19T17:00:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "대전이스포츠경기장",
        address: "대한민국 대전광역시 유성구 대덕대로 480 대전 이스포츠경기장",
        lat: 36.376904,
        lng: 127.3872357,
      },
      {
        id: IDS.schedules.AI,
        title: "AI",
        description: "AI",
        startAt: new Date("2025-09-19T17:00:00.000Z"),
        endAt: new Date("2025-09-19T18:00:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "대전이스포츠경기장",
        address: "대한민국 대전광역시 유성구 대덕대로 480 대전 이스포츠경기장",
        lat: 36.376904,
        lng: 127.3872357,
      },
      {
        id: IDS.schedules.SHOW,
        title: "교류공연 & 영상제",
        description: "교류공연 & 영상제",
        startAt: new Date("2025-09-19T18:45:00.000Z"),
        endAt: new Date("2025-09-19T20:45:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        id: IDS.schedules.SOCCER,
        title: "축구",
        description: "축구",
        startAt: new Date("2025-09-19T21:30:00.000Z"),
        endAt: new Date("2025-09-19T23:30:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "KAIST 대운동장",
        address: "대전광역시 유성구 대학로 291",
        lat: 36.36955117920298,
        lng: 127.36847011943173,
      },
      {
        id: IDS.schedules.BASEBALL,
        title: "야구",
        description: "야구",
        startAt: new Date("2025-09-20T10:00:00.000Z"),
        endAt: new Date("2025-09-20T12:30:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "북측운동장",
        address: "대전광역시 유성구 대학로 291",
        lat: 36.372351932127145,
        lng: 127.36022599968764,
      },
      {
        id: IDS.schedules.QUIZ,
        title: "과학퀴즈",
        description: "과학퀴즈",
        startAt: new Date("2025-09-20T13:30:00.000Z"),
        endAt: new Date("2025-09-20T15:00:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        id: IDS.schedules.BASKETBALL,
        title: "농구",
        description: "농구",
        startAt: new Date("2025-09-20T15:30:00.000Z"),
        endAt: new Date("2025-09-20T18:00:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
      {
        id: IDS.schedules.CLOSING,
        title: "폐막식",
        description: "폐막식",
        startAt: new Date("2025-09-20T19:00:00.000Z"),
        endAt: new Date("2025-09-20T22:00:00.000Z"),
        homeSchoolId: IDS.schools.KAIST,
        awaySchoolId: IDS.schools.POSTECH,
        locationName: "KAIST 스포츠컴플렉스",
        address: "대한민국 대전광역시 유성구 온천2동 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
    ],
    skipDuplicates: true,
  });

  /**
   * 9) Media + PostMedia
   */
  await prisma.media.createMany({
    data: [
      {
        id: IDS.medias.POSTER_1,
        uploaderId: IDS.users.ADMIN,
        url: "https://example.com/seed/poster1.png",
        mimeType: "image/png",
        size: 123456,
        width: 1080,
        height: 1080,
      },
    ],
    skipDuplicates: true,
  });

  await prisma.post.createMany({
    data: [
      {
        id: IDS.posts.WELCOME,
        authorId: IDS.users.ADMIN,
        title: "카포전 안내",
        content: "환영합니다! 일정/정보/응원법을 확인해보세요.",
        visibility: Visibility.PUBLIC,
        likeCount: 0,
        commentCount: 0,
      },
      {
        id: IDS.posts.NOTICE_1,
        authorId: IDS.users.ADMIN,
        title: "공지: 이동/안전 유의",
        content: "행사 기간 이동 시 안전에 유의해 주세요. 분실물/긴급 연락처는 추후 공지합니다.",
        visibility: Visibility.PUBLIC,
        likeCount: 0,
        commentCount: 0,
      },
    ],
    skipDuplicates: true,
  });

  await prisma.postMedia.createMany({
    data: [{ postId: IDS.posts.WELCOME, mediaId: IDS.medias.POSTER_1, sortOrder: 0 }],
    skipDuplicates: true,
  });

  /**
   * 10) PostTag (M:N)
   */
  await prisma.postTag.createMany({
    data: [
      { postId: IDS.posts.WELCOME, tagId: IDS.tags.EVENT },
      { postId: IDS.posts.WELCOME, tagId: IDS.tags.NOTICE },
      { postId: IDS.posts.NOTICE_1, tagId: IDS.tags.NOTICE },
    ],
    skipDuplicates: true,
  });

  /**
   * 11) Comment + Like (간단 샘플)
   */
  const c1 = await prisma.comment.create({
    data: {
      postId: IDS.posts.WELCOME,
      authorId: IDS.users.KAIST_1,
      content: "오! 일정 한 번에 볼 수 있어서 좋네요.",
    },
  });

  await prisma.comment.create({
    data: {
      postId: IDS.posts.WELCOME,
      authorId: IDS.users.ADMIN,
      content: "감사합니다. 개선할 점 있으면 편하게 알려주세요!",
      parentId: c1.id,
    },
  });

  // 좋아요: 복합 PK (userId, postId)
  await prisma.postLike
    .create({
      data: {
        userId: IDS.users.POSTECH_1,
        postId: IDS.posts.WELCOME,
      },
    })
    .catch(() => {}); // 이미 있으면 무시

  // 카운트 필드는 트리거가 없으니 seed에서 맞춰주기(선택)
  const welcomeCommentCount = await prisma.comment.count({ where: { postId: IDS.posts.WELCOME } });
  const welcomeLikeCount = await prisma.postLike.count({ where: { postId: IDS.posts.WELCOME } });
  await prisma.post.update({
    where: { id: IDS.posts.WELCOME },
    data: { commentCount: welcomeCommentCount, likeCount: welcomeLikeCount },
  });

  /**
   * 12) DeviceToken + Notification (샘플)
   */
  await prisma.deviceToken
    .create({
      data: {
        userId: IDS.users.KAIST_1,
        token: "seed_device_token_kaist_1",
        platform: Platform.ANDROID,
        lastSeenAt: new Date(),
      },
    })
    .catch(() => {});

  await prisma.notification.createMany({
    data: [
      {
        userId: IDS.users.KAIST_1,
        type: NotificationType.GENERAL,
        title: "환영합니다!",
        body: "카포전 일정/정보를 확인해 보세요.",
        dataJson: JSON.stringify({ screen: "home" }),
      },
      {
        userId: IDS.users.POSTECH_1,
        type: NotificationType.GENERAL,
        title: "행사 안내",
        body: "교류전 정보가 업데이트되었습니다.",
        dataJson: JSON.stringify({ screen: "info" }),
      },
    ],
    skipDuplicates: true,
  });

  /**
   * 13) ShareLink (중요: entityType/entityId/slug 구조)
   * - slug @unique 이므로 upsert 기준으로 가장 안전
   */
  const shareLinks = [
    {
      slug: IDS.shareLinks.SCH_OPENING,
      entityType: ShareEntityType.SCHEDULE,
      entityId: IDS.schedules.OPENING,
      expiresAt: null as Date | null,
    },
    {
      slug: IDS.shareLinks.INFO_KAIMARU,
      entityType: ShareEntityType.INFO,
      entityId: IDS.infos.KAIMARU,
      expiresAt: null as Date | null,
    },
    {
      slug: IDS.shareLinks.CHEER_KAIST,
      entityType: ShareEntityType.CHEER,
      entityId: IDS.cheerMethods.KAIST_1,
      expiresAt: null as Date | null,
    },
    {
      slug: IDS.shareLinks.POST_WELCOME,
      entityType: ShareEntityType.POST,
      entityId: IDS.posts.WELCOME,
      expiresAt: null as Date | null,
    },
  ];

  for (const s of shareLinks) {
    await prisma.shareLink.upsert({
      where: { slug: s.slug },
      update: { entityType: s.entityType, entityId: s.entityId, expiresAt: s.expiresAt },
      create: { slug: s.slug, entityType: s.entityType, entityId: s.entityId, expiresAt: s.expiresAt },
    });
  }

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
