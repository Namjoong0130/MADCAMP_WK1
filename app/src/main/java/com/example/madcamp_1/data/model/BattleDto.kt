package com.example.madcamp_1.data.model

data class CheerMatchResponse(
    val id: String,
    val title: String,
    val homeTeam: CheerTeamDto,
    val awayTeam: CheerTeamDto,
    val homeTotalTaps: Long,    // ✅ 서버 필드명과 일치
    val awayTotalTaps: Long,    // ✅ 서버 필드명과 일치
    val topHomeTappers: List<TapperDto> = emptyList(),
    val topAwayTappers: List<TapperDto> = emptyList()
)

data class CheerTeamDto(
    val id: String,
    val name: String
)

data class TapperDto(
    val nickname: String,
    val count: Int
)

data class CheerTapRequest(
    val matchId: String,
    val teamId: String,
    val count: Int = 1
)

// ✅ POST 응답 전용 DTO
data class CheerTapResponse(
    val ok: Boolean,
    val homeTotalTaps: Long,
    val awayTotalTaps: Long
)