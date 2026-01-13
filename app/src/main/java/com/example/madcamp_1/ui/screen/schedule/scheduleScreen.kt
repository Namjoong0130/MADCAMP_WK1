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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.ui.theme.UnivsFontFamily

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // --- 상단 헤더 영역 ---
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
                        modifier = Modifier.padding(1.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "${userName}님", fontFamily = UnivsFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = userSchoolDisplayName, fontFamily = UnivsFontFamily, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                ScoreBadge("POSTECH ", pScore, Color(0xFFE0224E))
                Text(" : ", color = Color.LightGray, modifier = Modifier.padding(horizontal = 4.dp))
                ScoreBadge("KAIST ", kScore, Color(0xFF005EB8))
            }
        }

        // --- 시간표 컨텐츠 (수정된 로직) ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            val totalHeightDp = maxHeight
            val headerHeight = 44.dp
            val usableHeightDp = totalHeightDp - headerHeight
            val hourHeight = usableHeightDp / totalHours

            Column {
                // 요일 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .background(Color(0xFFF1F3F5))
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                    days.forEach { day ->
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            Text(day, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF495057))
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    // 왼쪽 시간축
                    Column(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight()
                            .background(Color(0xFFFBFBFC))
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
                                    .border(0.5.dp, Color(0xFFF1F3F5))
                            ) {
                                events.filter { it.dayIndex == dIndex }.forEach { event ->
                                    val topOffset = (event.startHour - startHour) * hourHeight.value
                                    val heightSize = event.duration * hourHeight.value

                                    Surface(
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .offset(y = topOffset.dp)
                                            .fillMaxWidth()
                                            .height(heightSize.dp),
                                        color = event.color.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.5.dp, event.color.copy(alpha = 0.6f))
                                    ) {
                                        Box(modifier = Modifier.padding(4.dp)) {
                                            Text(
                                                text = event.name,
                                                fontSize = 11.sp,
                                                color = event.color.copy(alpha = 1f),
                                                fontWeight = FontWeight.ExtraBold,
                                                lineHeight = 12.sp
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
@Composable
fun ScoreBadge(label: String, score: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = score.toString(), fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}