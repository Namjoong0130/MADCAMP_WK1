import { prisma } from "../../db/prisma.js";
import { HttpError } from "../../utils/httpError.js";

export class MeService {
  async getMe(userId: string) {
    const me = await prisma.user.findUnique({
      where: { id: userId },
      select: { id: true, email: true, nickname: true, role: true, schoolId: true, profileImageUrl: true },
    });
    if (!me) throw new HttpError(404, "User not found", "USER_NOT_FOUND");
    return me;
  }

  async updateMe(userId: string, input: { nickname?: string | undefined; schoolId?: string | null | undefined; profileImageUrl?: string | null | undefined }) {
    const me = await prisma.user.update({
      where: { id: userId },
      data: {
        ...(input.nickname !== undefined ? { nickname: input.nickname } : {}),
        ...(input.schoolId !== undefined ? { schoolId: input.schoolId } : {}),
        ...(input.profileImageUrl !== undefined ? { profileImageUrl: input.profileImageUrl } : {}),
      },
      select: { id: true, email: true, nickname: true, role: true, schoolId: true, profileImageUrl: true },
    });
    return me;
  }
}
