package com.example.madcamp_1.data.model

// 현재 활성화된 응원전 매치 정보
data class CheerMatchResponse(
    val id: String,
    val title: String,
    val homeTeam: CheerTeamDto,
    val awayTeam: CheerTeamDto,
    val homeTotalTaps: Long,
    val awayTotalTaps: Long
)

data class CheerTeamDto(
    val id: String,
    val name: String, // "KAIST" or "POSTECH"
    val logoUrl: String?
)

// 터치 전송 요청 (한 번에 여러 번의 탭을 묶어서 보낼 수 있도록 구성)
data class CheerTapRequest(
    val matchId: String,
    val teamId: String,
    val count: Int = 1
)