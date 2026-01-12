package com.example.madcamp_1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// 본인의 프로젝트 패키지명에 맞게 R을 import 하세요
import com.example.madcamp_1.R

// 1. 커스텀 FontFamily 정의
val UnivsFontFamily = FontFamily(
    Font(R.font.univs_font, FontWeight.Normal),
    Font(R.font.univs_font, FontWeight.Medium),
    Font(R.font.univs_font, FontWeight.Bold),
    Font(R.font.univs_font, FontWeight.ExtraBold)
)

// 2. Material 3 Typography 설정
val Typography = Typography(
    // 앱 전체의 기본 본문 텍스트 (게시판 리스트 등)
    bodyLarge = TextStyle(
        fontFamily = UnivsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // 중간 크기 본문 (일정표 내 종목 이름 등)
    bodyMedium = TextStyle(
        fontFamily = UnivsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    // 큰 제목 (상단 "김철수님", 점수판 숫자 등)
    headlineSmall = TextStyle(
        fontFamily = UnivsFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // 아주 작은 텍스트 (시간 축 숫자 등)
    labelSmall = TextStyle(
        fontFamily = UnivsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)