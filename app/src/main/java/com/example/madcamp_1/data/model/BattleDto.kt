package com.example.madcamp_1.data.model

import com.google.gson.annotations.SerializedName

// GET /api/cheer/active-match 응답
data class CheerMatchResponse(
    val id: String,
    val title: String,
    val isActive: Boolean,
    val startsAt: String?,
    val endsAt: String?,
    val homeTeam: CheerTeamDto,
    val awayTeam: CheerTeamDto,
    val homeTotalTaps: Long,
    val awayTotalTaps: Long
)

data class CheerTeamDto(
    val id: String,
    val name: String,
    val logoUrl: String?
)

// POST /api/cheer/taps 요청
data class CheerTapRequest(
    val matchId: String,
    val teamId: String,
    val count: Int = 1
)

// POST /api/cheer/taps 응답 (백엔드 controller의 res.status(201).json 결과)
data class CheerTapResponse(
    val ok: Boolean,
    val tap: CheerTapInfo?, // 백엔드 result.tap 대응
    val homeTotalTaps: Long,
    val awayTotalTaps: Long
)

data class CheerTapInfo(
    val id: String,
    val count: Int
)