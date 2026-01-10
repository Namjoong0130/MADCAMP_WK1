package com.example.madcamp_1.ui.screen.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScheduleScreen(events: List<ScheduleEvent>) {
    val days = listOf("1일차", "2일차", "3일차")
    val times = (9..22).toList() // 9시 ~ 22시

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("3박 2일 일정", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 타임라인 테이블
        Row(modifier = Modifier.fillMaxSize().border(1.dp, Color.LightGray)) {
            // 시간 표시열
            Column(modifier = Modifier.width(30.dp)) {
                Spacer(modifier = Modifier.height(30.dp))
                times.forEach { time ->
                    Box(modifier = Modifier.height(60.dp).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                        Text("$time", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            // 요일별 열
            days.forEachIndexed { dIndex, day ->
                Column(modifier = Modifier.weight(1f).border(0.5.dp, Color.LightGray)) {
                    Box(modifier = Modifier.height(30.dp).fillMaxWidth().background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                        Text(day, fontSize = 12.sp)
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        // 해당 요일의 이벤트만 필터링해서 그리기
                        events.filter { it.dayIndex == dIndex }.forEach { event ->
                            val topMargin = (event.startHour - 9) * 60
                            Box(
                                modifier = Modifier
                                    .padding(top = topMargin.dp, start = 2.dp, end = 2.dp)
                                    .fillMaxWidth()
                                    .height((event.duration * 60).dp)
                                    .background(event.color, RoundedCornerShape(4.dp))
                                    .padding(4.dp)
                            ) {
                                Text(event.name, fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}