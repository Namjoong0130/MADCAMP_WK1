package com.example.madcamp_1.ui.screen.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScheduleScreen(
    events: List<ScheduleEvent>,
    userName: String,
    userSchool: String,
    pScore: Int,
    kScore: Int
) {
    val days = listOf("1일차", "2일차", "3일차")
    val startHour = 9
    val endHour = 24
    val totalHours = endHour - startHour

    // 학교별 메인 컬러 선정
    val schoolColor = if (userSchool == "POSTECH") Color(0xFFD32F2F) else Color(0xFF1976D2)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- 상단 헤더 영역 ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 사용자 및 학교 정보
            Column {
                Text(text = userName, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = userSchool,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = schoolColor
                )
            }

            // 점수판
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F0F0),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("POSTECH ", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    Text("$pScore : $kScore", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text(" KAIST", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 일정표 영역 ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        ) {
            val totalHeight = maxHeight
            val hourHeight = (totalHeight - 30.dp) / totalHours // 헤더 제외 높이 계산

            Column {
                // 요일 헤더
                Row(modifier = Modifier.fillMaxWidth().height(30.dp).background(Color(0xFFF9F9F9))) {
                    Spacer(modifier = Modifier.width(35.dp))
                    days.forEach { day ->
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.2.dp, Color.LightGray), contentAlignment = Alignment.Center) {
                            Text(day, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 시간 및 이벤트 본문
                Row(modifier = Modifier.fillMaxWidth()) {
                    // 시간 축
                    Column(modifier = Modifier.width(35.dp)) {
                        (startHour..endHour).forEach { hour ->
                            Box(modifier = Modifier.height(hourHeight).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                                Text("$hour", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    // 요일별 컬럼
                    days.forEachIndexed { dIndex, _ ->
                        Box(modifier = Modifier.weight(1f).height(hourHeight * totalHours).border(0.2.dp, Color.LightGray)) {
                            events.filter { it.dayIndex == dIndex }.forEach { event ->
                                val topMargin = (event.startHour - startHour) * hourHeight.value
                                val eventHeight = event.duration * hourHeight.value

                                Surface(
                                    modifier = Modifier
                                        .padding(top = topMargin.dp, start = 2.dp, end = 2.dp)
                                        .fillMaxWidth()
                                        .height(eventHeight.dp),
                                    color = event.color,
                                    shape = RoundedCornerShape(4.dp),
                                    shadowElevation = 1.dp
                                ) {
                                    Text(
                                        text = event.name,
                                        modifier = Modifier.padding(4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 13.sp
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