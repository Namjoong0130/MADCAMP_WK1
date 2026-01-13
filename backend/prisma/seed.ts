// prisma/seed.ts
import "../src/config/env.js";
import {
  PrismaClient,
  Platform,
  Visibility,
  UserRole,
  NotificationType,
  ShareEntityType,
} from "../src/generated/prisma/client.js";
import { PrismaMariaDb } from "@prisma/adapter-mariadb";

const prisma = new PrismaClient({
  adapter: new PrismaMariaDb(process.env.DATABASE_URL!),
});

/**
 * seed에서 "고정 id"를 쓰면:
 * - 참조 관계(PostTag/PostMedia/ShareLink 등)가 안정적
 * - seed 반복 실행 시 upsert/skipDuplicates로 관리 가능
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
    QNA: "Q&A",
    TIP: "꿀팁",
    COMMUNITY: "소통",
    NOTICE: "공지",
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
  },
  posts: {
    WELCOME: "seed_post_welcome",
    NOTICE_1: "seed_post_notice_1",
    WITH_HTTP_IMAGE: "seed_post_with_http_image",
    WITH_BASE64_IMAGE: "seed_post_with_base64_image",
  },
  medias: {
    POSTER_1: "seed_media_poster_1",
    HTTP_POSTER: "seed_media_http_poster",
    BASE64_PNG_1PX: "seed_media_base64_png_1px",
  },
  shareLinks: {
    POST_WELCOME: "seed-post-welcome",
  },
};

/** bcrypt 해시처럼 보이는 더미 문자열(로그인 구현 전용) */
const DUMMY_PASSWORD_HASH =
  "$2b$10$seedseedseedseedseedseedseedseedseedseedseedseedseedse";

/**
 * 1x1 PNG base64 (아주 짧아서 seed 검증에 최적)
 * - prefix(data:image/png;base64,) 포함 "data URL" 형태
 */
const BASE64_1PX_PNG_DATA_URL =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+X2Z8AAAAASUVORK5CYII=";

async function main() {
  // -----------------------------
  // 0) seed 데이터만 "안전 정리"
  // -----------------------------
  await prisma.postLike
    .deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } })
    .catch(() => {});
  await prisma.comment
    .deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } })
    .catch(() => {});
  await prisma.postMedia
    .deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } })
    .catch(() => {});
  await prisma.postTag
    .deleteMany({ where: { postId: { in: Object.values(IDS.posts) } } })
    .catch(() => {});
  await prisma.infoTag
    .deleteMany({ where: { infoId: { in: Object.values(IDS.infos) } } })
    .catch(() => {});

  await prisma.notification
    .deleteMany({ where: { userId: { in: Object.values(IDS.users) } } })
    .catch(() => {});
  await prisma.deviceToken
    .deleteMany({ where: { userId: { in: Object.values(IDS.users) } } })
    .catch(() => {});
  await prisma.refreshToken
    .deleteMany({ where: { userId: { in: Object.values(IDS.users) } } })
    .catch(() => {});

  await prisma.shareLink
    .deleteMany({ where: { slug: { in: Object.values(IDS.shareLinks) } } })
    .catch(() => {});

  await prisma.post
    .deleteMany({ where: { id: { in: Object.values(IDS.posts) } } })
    .catch(() => {});
  await prisma.media
    .deleteMany({ where: { id: { in: Object.values(IDS.medias) } } })
    .catch(() => {});
  await prisma.schedule
    .deleteMany({ where: { id: { in: Object.values(IDS.schedules) } } })
    .catch(() => {});
  await prisma.cheerMethod
    .deleteMany({ where: { id: { in: Object.values(IDS.cheerMethods) } } })
    .catch(() => {});
  await prisma.info
    .deleteMany({ where: { id: { in: Object.values(IDS.infos) } } })
    .catch(() => {});
  await prisma.infoCategory
    .deleteMany({ where: { id: { in: Object.values(IDS.infoCategories) } } })
    .catch(() => {});
  await prisma.tag
    .deleteMany({ where: { id: { in: Object.values(IDS.tags) } } })
    .catch(() => {});
  await prisma.user
    .deleteMany({ where: { id: { in: Object.values(IDS.users) } } })
    .catch(() => {});
  await prisma.school
    .deleteMany({ where: { id: { in: Object.values(IDS.schools) } } })
    .catch(() => {});

  // -----------------------------
  // 1) School
  // -----------------------------
  await prisma.school.createMany({
    data: [
      { id: IDS.schools.KAIST, name: "KAIST", shortName: "KAIST" },
      { id: IDS.schools.POSTECH, name: "POSTECH", shortName: "POST" },
      { id: IDS.schools.YONSEI, name: "연세대", shortName: "YONSEI" },
      { id: IDS.schools.KOREA, name: "고려대", shortName: "KOREA" },
    ],
    skipDuplicates: true,
  });

  // -----------------------------
  // 2) User
  // -----------------------------
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

  // -----------------------------
  // 3) Tag (id를 사람이 읽는 문자열로 고정)
  // -----------------------------
  await prisma.tag.createMany({
    data: [
      { id: IDS.tags.QNA, name: "Q&A" },
      { id: IDS.tags.TIP, name: "꿀팁" },
      { id: IDS.tags.COMMUNITY, name: "소통" },
      { id: IDS.tags.NOTICE, name: "공지" },
    ],
    skipDuplicates: true,
  });

  // -----------------------------
  // 4) InfoCategory / Info / InfoTag (필요하면 유지)
  // -----------------------------
  await prisma.infoCategory.createMany({
    data: [
      { id: IDS.infoCategories.FOOD, name: "식사", sortOrder: 1 },
      { id: IDS.infoCategories.CAFE, name: "카페", sortOrder: 2 },
      { id: IDS.infoCategories.TRANSPORT, name: "교통", sortOrder: 3 },
      { id: IDS.infoCategories.CAMPUS, name: "캠퍼스", sortOrder: 4 },
    ],
    skipDuplicates: true,
  });

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
    ],
    skipDuplicates: true,
  });

  await prisma.infoTag.createMany({
    data: [{ infoId: IDS.infos.KAIMARU, tagId: IDS.tags.COMMUNITY }],
    skipDuplicates: true,
  });

  // -----------------------------
  // 5) Schedule (최소 1개만)
  // -----------------------------
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
        address: "대한민국 대전광역시 유성구 대학로 291",
        lat: 36.372389,
        lng: 127.361556,
      },
    ],
    skipDuplicates: true,
  });

  // -----------------------------
  // 6) Media 3개:
  //    - 기존 포스터(HTTP)
  //    - 검증용 HTTP
  //    - 검증용 Base64(data URL)
  // -----------------------------
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
      {
        id: IDS.medias.HTTP_POSTER,
        uploaderId: IDS.users.ADMIN,
        url: "https://example.com/seed/poster_http.png",
        mimeType: "image/png",
        size: 1000,
        width: 100,
        height: 100,
      },
      {
        id: IDS.medias.BASE64_PNG_1PX,
        uploaderId: IDS.users.ADMIN,
        url: BASE64_1PX_PNG_DATA_URL, // ✅ 핵심: data URL 저장
        mimeType: "image/png",
        size: BASE64_1PX_PNG_DATA_URL.length,
        width: 1,
        height: 1,
      },
    ],
    skipDuplicates: true,
  });

  // -----------------------------
  // 7) Post 4개:
  //    - 기존(WELCOME/NOTICE)
  //    - 검증용 HTTP 이미지 포함 글
  //    - 검증용 Base64 이미지 포함 글
  // -----------------------------
  await prisma.post.createMany({
    data: [
      {
        id: IDS.posts.WELCOME,
        authorId: IDS.users.ADMIN,
        title: "카포전 안내",
        content: "환영합니다! 일정/정보/응원법을 확인해보세요.",
        visibility: Visibility.PUBLIC,
      },
      {
        id: IDS.posts.NOTICE_1,
        authorId: IDS.users.ADMIN,
        title: "공지: 이동/안전 유의",
        content:
          "행사 기간 이동 시 안전에 유의해 주세요. 분실물/긴급 연락처는 추후 공지합니다.",
        visibility: Visibility.PUBLIC,
      },
      {
        id: IDS.posts.WITH_HTTP_IMAGE,
        authorId: IDS.users.ADMIN,
        title: "HTTP 이미지 포함 글(검증용)",
        content: "medias[0].url이 https:// 로 시작하는지 확인하세요.",
        visibility: Visibility.PUBLIC,
      },
      {
        id: IDS.posts.WITH_BASE64_IMAGE,
        authorId: IDS.users.ADMIN,
        title: "Base64 이미지 포함 글(검증용)",
        content: "medias[0].url이 data:image/png;base64, 로 시작하는지 확인하세요.",
        visibility: Visibility.PUBLIC,
      },
    ],
    skipDuplicates: true,
  });

  // PostMedia 연결
  await prisma.postMedia.createMany({
    data: [
      { postId: IDS.posts.WELCOME, mediaId: IDS.medias.POSTER_1, sortOrder: 0 },
      {
        postId: IDS.posts.WITH_HTTP_IMAGE,
        mediaId: IDS.medias.HTTP_POSTER,
        sortOrder: 0,
      },
      {
        postId: IDS.posts.WITH_BASE64_IMAGE,
        mediaId: IDS.medias.BASE64_PNG_1PX,
        sortOrder: 0,
      },
    ],
    skipDuplicates: true,
  });

  // PostTag 연결 (검증 포인트: 서로 다른 태그 조합)
  await prisma.postTag.createMany({
    data: [
      { postId: IDS.posts.WELCOME, tagId: IDS.tags.TIP },
      { postId: IDS.posts.WELCOME, tagId: IDS.tags.NOTICE },
      { postId: IDS.posts.NOTICE_1, tagId: IDS.tags.NOTICE },

      // ✅ 검증용
      { postId: IDS.posts.WITH_HTTP_IMAGE, tagId: IDS.tags.NOTICE },
      { postId: IDS.posts.WITH_HTTP_IMAGE, tagId: IDS.tags.COMMUNITY },

      { postId: IDS.posts.WITH_BASE64_IMAGE, tagId: IDS.tags.QNA },
      { postId: IDS.posts.WITH_BASE64_IMAGE, tagId: IDS.tags.COMMUNITY },
    ],
    skipDuplicates: true,
  });

  // -----------------------------
  // 8) Comment/Like 카운트(선택)
  // -----------------------------
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

  await prisma.postLike
    .create({
      data: {
        userId: IDS.users.POSTECH_1,
        postId: IDS.posts.WELCOME,
      },
    })
    .catch(() => {});

  const welcomeCommentCount = await prisma.comment.count({
    where: { postId: IDS.posts.WELCOME },
  });
  const welcomeLikeCount = await prisma.postLike.count({
    where: { postId: IDS.posts.WELCOME },
  });
  await prisma.post.update({
    where: { id: IDS.posts.WELCOME },
    data: { commentCount: welcomeCommentCount, likeCount: welcomeLikeCount },
  });

  // -----------------------------
  // 9) DeviceToken + Notification (샘플)
  // -----------------------------
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
    ],
    skipDuplicates: true,
  });

  // -----------------------------
  // 10) ShareLink (샘플)
  // -----------------------------
  await prisma.shareLink.upsert({
    where: { slug: IDS.shareLinks.POST_WELCOME },
    update: {
      entityType: ShareEntityType.POST,
      entityId: IDS.posts.WELCOME,
      expiresAt: null,
      createdById: IDS.users.ADMIN,
    },
    create: {
      slug: IDS.shareLinks.POST_WELCOME,
      entityType: ShareEntityType.POST,
      entityId: IDS.posts.WELCOME,
      expiresAt: null,
      createdById: IDS.users.ADMIN,
    },
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
