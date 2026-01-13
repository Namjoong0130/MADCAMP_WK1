package com.example.madcamp_1.ui.screen.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import kotlin.math.floor

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScheduleScreen(
    events: List<ScheduleEvent>,
    userName: String,
    userLogoRes: Int,
    userSchoolDisplayName: String,
    pScore: Int,
    kScore: Int
) {
    val days = listOf("1일차", "2일차", "3일차")
    val startHour = 9
    val endHour = 24
    val totalHours = endHour - startHour

    // [테마 설정] 학교에 따른 파스텔 컬러 정의
    val themeBorderColor = when (userSchoolDisplayName) {
        "POSTECH" -> Color(0xFFFFCDD2) // 파스텔 레드 테두리
        "KAIST" -> Color(0xFFBBDEFB)   // 파스텔 블루 테두리
        else -> Color(0xFFE0E0E0)
    }

    val themeHeaderColor = when (userSchoolDisplayName) {
        "POSTECH" -> Color(0xFFFFF5F5) // 아주 연한 레드 헤더
        "KAIST" -> Color(0xFFF0F7FF)   // 아주 연한 블루 헤더
        else -> Color(0xFFF1F3F5)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // --- [1] 상단 헤더 영역 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Image(
                        painter = painterResource(id = userLogoRes),
                        contentDescription = "Logo",
                        modifier = Modifier.padding(1.5.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${userName}님",
                        fontFamily = UnivsFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = userSchoolDisplayName,
                        fontFamily = UnivsFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (userSchoolDisplayName) {
                            "POSTECH" -> Color(0xFFC62828)
                            "KAIST" -> Color(0xFF004191)
                            else -> Color.Gray
                        }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                ScoreBadge("POSTECH ", pScore, Color(0xFFC62828))
                Text(" : ", color = Color.LightGray, modifier = Modifier.padding(horizontal = 4.dp))
                ScoreBadge("KAIST ", kScore, Color(0xFF004191))
            }
        }

        // --- [2] 시간표 컨텐츠 메인 박스 (테마 적용된 틀) ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                // [변경] 외부 틀 테두리 색상 적용
                .border(2.dp, themeBorderColor, RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            val totalHeightDp = maxHeight
            val headerHeight = 44.dp
            val usableHeightDp = totalHeightDp - headerHeight
            val hourHeight = usableHeightDp / totalHours

            Column {
                // 요일 헤더 (테마 배경색 적용)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .background(themeHeaderColor) // [변경] 헤더 파스텔 배경색
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                    days.forEach { day ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF495057),
                                fontFamily = UnivsFontFamily
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    // 왼쪽 시간축
                    Column(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight()
                            .background(themeHeaderColor.copy(alpha = 0.5f)) // [변경] 시간축 배경 연하게 유지
                    ) {
                        for (hour in startHour until endHour) {
                            Box(
                                modifier = Modifier
                                    .height(hourHeight)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = hour.toString(),
                                    fontSize = 11.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // 일정 본문 영역
                    Row(modifier = Modifier.fillMaxSize()) {
                        days.forEachIndexed { dIndex, _ ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(0.5.dp, themeBorderColor.copy(alpha = 0.3f)) // [변경] 내부 구분선도 테마색 적용
                            ) {
                                events.filter { it.dayIndex == dIndex }.forEach { event ->
                                    val topOffset = (event.startHour - startHour) * hourHeight.value
                                    val heightSize = event.duration * hourHeight.value

                                    Surface(
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp, vertical = 1.dp)
                                            .offset(y = topOffset.dp)
                                            .fillMaxWidth()
                                            .height(heightSize.dp),
                                        color = event.color.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.5.dp, event.color.copy(alpha = 0.3f))
                                    ) {
                                        Row(modifier = Modifier.fillMaxSize()) {
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .fillMaxHeight()
                                                    .background(
                                                        color = event.color,
                                                        shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                                                    )
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                                    .fillMaxSize(),
                                                verticalArrangement = Arrangement.Top
                                            ) {
                                                Spacer(modifier = Modifier.height(1.dp))

                                                Text(
                                                    text = event.name,
                                                    fontSize = 14.sp,
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    lineHeight = 16.sp,
                                                    fontFamily = UnivsFontFamily,
                                                    maxLines = if (event.duration < 0.8) 1 else 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )

                                                Text(
                                                    text = formatTimeRange(event.startHour, event.duration),
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF616161),
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = UnivsFontFamily
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTimeRange(start: Double, duration: Double): String {
    val end = start + duration
    fun toStr(time: Double): String {
        val h = floor(time).toInt()
        val m = ((time - h) * 60).toInt()
        return "%02d:%02d".format(h, m)
    }
    return "${toStr(start)} - ${toStr(end)}"
}

@Composable
fun ScoreBadge(label: String, score: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = UnivsFontFamily
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = score.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            fontFamily = UnivsFontFamily
        )
    }
}