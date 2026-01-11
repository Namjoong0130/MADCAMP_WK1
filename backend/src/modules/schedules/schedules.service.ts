import { prisma } from "../../db/prisma.js";
import { parseCursor, parseLimit } from "../../utils/pagination.js";
import type { CursorPage } from "../../utils/pagination.js";

export class SchedulesService {
  async list(query: { cursor?: unknown; limit?: unknown }) {
    const limit = parseLimit(query.limit);
    const cursor = parseCursor(query.cursor);

    const rows = await prisma.schedule.findMany({
      take: limit + 1,
      ...(cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
      orderBy: { id: "desc" },
    });

    const hasNext = rows.length > limit;
    const items = hasNext ? rows.slice(0, limit) : rows;
    const nextCursor = hasNext ? items[items.length - 1]?.id ?? null : null;

    const page: CursorPage<any> = { items, nextCursor };
    return page;
  }

  async get(id: string) {
    return prisma.schedule.findUnique({ where: { id } });
  }

  async create(userId: string, input: any) {
    return prisma.schedule.create({
      data: {
        ...input,
        createdById: userId,
      },
    });
  }

  async patch(id: string, userId: string, input: any) {
    // 권한 로직은 프로젝트 정책에 맞게 확장(작성자/리더 등)
    return prisma.schedule.update({
      where: { id },
      data: { ...input, updatedById: userId },
    });
  }

  async remove(id: string) {
    await prisma.schedule.delete({ where: { id } });
  }
}
