import { prisma } from "../../db/prisma.js";
import { HttpError } from "../../utils/httpError.js";
import { hashPassword, verifyPassword } from "../../utils/password.js";
import { signAccessToken, signRefreshToken, verifyRefreshToken } from "../../utils/jwt.js";

export class AuthService {
  async register(input: { email: string; password: string; nickname: string; schoolId?: string | null | undefined }) {
    const exists = await prisma.user.findUnique({ where: { email: input.email } });
    if (exists) throw new HttpError(409, "Email already exists", "EMAIL_EXISTS");

    const passwordHash = await hashPassword(input.password);
    const user = await prisma.user.create({
      data: {
        email: input.email,
        passwordHash,
        nickname: input.nickname,
        schoolId: input.schoolId ?? null,
      },
      select: { id: true, email: true, nickname: true, role: true, schoolId: true, profileImageUrl: true },
    });

    const accessToken = signAccessToken({ sub: user.id, role: user.role });
    const refreshToken = signRefreshToken({ sub: user.id });

    return { user, accessToken, refreshToken };
  }

  async login(input: { email: string; password: string }) {
    const user = await prisma.user.findUnique({ where: { email: input.email } });
    if (!user) throw new HttpError(401, "Invalid credentials", "INVALID_CREDENTIALS");

    const ok = await verifyPassword(input.password, user.passwordHash);
    if (!ok) throw new HttpError(401, "Invalid credentials", "INVALID_CREDENTIALS");

    const accessToken = signAccessToken({ sub: user.id, role: user.role });
    const refreshToken = signRefreshToken({ sub: user.id });

    const authUser = {
      id: user.id,
      email: user.email,
      nickname: user.nickname,
      role: user.role,
      schoolId: user.schoolId,
      profileImageUrl: user.profileImageUrl,
    };

    return { user: authUser, accessToken, refreshToken };
  }

  async refresh(input: { refreshToken: string }) {
    const payload = await verifyRefreshToken(input.refreshToken);
    const user = await prisma.user.findUnique({
      where: { id: payload.sub },
      select: { id: true, email: true, nickname: true, role: true, schoolId: true, profileImageUrl: true },
    });
    if (!user) throw new HttpError(401, "Invalid refresh token", "INVALID_REFRESH");

    const accessToken = signAccessToken({ sub: user.id, role: user.role });
    return { accessToken };
  }

  async logout(_input: { refreshToken?: string | undefined }) {
    // 현재는 stateless refresh 전략(DB 저장/폐기 없음)으로 no-op
    // 필요하면 RefreshToken 테이블을 추가해 revoke 전략으로 확장
    return;
  }
}
