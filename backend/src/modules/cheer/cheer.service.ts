import { prisma } from "../../db/prisma.js";
import { HttpError } from "../../utils/httpError.js";
import type { CheerTapInput } from "./cheer.schemas.js";

function sumOrZero(v: number | null | undefined) {
  return typeof v === "number" ? v : 0;
}

export async function getActiveMatchWithTotals() {
  const match = await prisma.cheerMatch.findFirst({
    where: { isActive: true, deletedAt: null },
    orderBy: [{ createdAt: "desc" }],
    select: {
      id: true,
      title: true,
      isActive: true,
      startsAt: true,
      endsAt: true,
      homeTeamId: true,
      awayTeamId: true,
      homeTeam: { select: { id: true, name: true, logoUrl: true } },
      awayTeam: { select: { id: true, name: true, logoUrl: true } },
    },
  });

  if (!match) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");

  const [homeAgg, awayAgg] = await Promise.all([
    prisma.cheerTap.aggregate({
      where: { matchId: match.id, teamId: match.homeTeamId },
      _sum: { count: true },
    }),
    prisma.cheerTap.aggregate({
      where: { matchId: match.id, teamId: match.awayTeamId },
      _sum: { count: true },
    }),
  ]);

  return {
    id: match.id,
    title: match.title,
    isActive: match.isActive,
    startsAt: match.startsAt,
    endsAt: match.endsAt,
    homeTeam: match.homeTeam,
    awayTeam: match.awayTeam,
    homeTotalTaps: sumOrZero(homeAgg._sum.count),
    awayTotalTaps: sumOrZero(awayAgg._sum.count),
  };
}

export async function tapCheer(input: CheerTapInput, userId: string) {
  return await prisma.$transaction(async (tx) => {
    const match = await tx.cheerMatch.findUnique({
      where: { id: input.matchId },
      select: {
        id: true,
        isActive: true,
        deletedAt: true,
        homeTeamId: true,
        awayTeamId: true,
      },
    });

    if (!match || match.deletedAt) throw new HttpError(404, "리소스를 찾을 수 없습니다.", "NOT_FOUND");
    if (!match.isActive) throw new HttpError(400, "활성화된 매치가 아닙니다.", "MATCH_INACTIVE");

    // teamId가 해당 매치의 home/away인지 검증
    const isValidTeam = input.teamId === match.homeTeamId || input.teamId === match.awayTeamId;
    if (!isValidTeam) throw new HttpError(400, "teamId가 유효하지 않습니다.", "INVALID_TEAM");

    // 유저가 같은 매치/팀에 대해 1개 row만 유지하고 count만 증가
    const row = await tx.cheerTap.upsert({
      where: {
        matchId_teamId_userId: {
          matchId: input.matchId,
          teamId: input.teamId,
          userId,
        },
      },
      create: {
        matchId: input.matchId,
        teamId: input.teamId,
        userId,
        count: input.count,
      },
      update: {
        count: { increment: input.count },
      },
      select: {
        id: true,
        matchId: true,
        teamId: true,
        userId: true,
        count: true,
        updatedAt: true,
      },
    });

    // totals 재계산(간단/정확 우선)
    const [homeAgg, awayAgg] = await Promise.all([
      tx.cheerTap.aggregate({
        where: { matchId: match.id, teamId: match.homeTeamId },
        _sum: { count: true },
      }),
      tx.cheerTap.aggregate({
        where: { matchId: match.id, teamId: match.awayTeamId },
        _sum: { count: true },
      }),
    ]);

    return {
      tap: row,
      homeTotalTaps: sumOrZero(homeAgg._sum.count),
      awayTotalTaps: sumOrZero(awayAgg._sum.count),
    };
  });
}
