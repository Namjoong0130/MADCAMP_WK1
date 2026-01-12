import { prisma } from "../../db/prisma.js";

export class SchoolsService {
  async list() {
    const items = await prisma.school.findMany({
      select: { id: true, name: true, shortName: true, logoUrl: true },
      orderBy: { name: "asc" },
    });
    return { items };
  }

  async detail(id: string) {
    const school = await prisma.school.findUnique({
      where: { id },
      select: { id: true, name: true, shortName: true, logoUrl: true },
    });
    return school;
  }
}
