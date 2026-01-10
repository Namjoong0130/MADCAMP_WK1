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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ScheduleScreen(events: List<ScheduleEvent>) {
    val days = listOf("1일차", "2일차", "3일차")
    val times = (9..24).toList()
    val hourHeight = 60.dp // 1시간당 높이 고정

    // 스크롤 상태 기억
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("일정", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 전체 테이블 컨테이너
        Column(modifier = Modifier.fillMaxSize().border(1.dp, Color.LightGray)) {

            // 1. 고정 헤더 (요일 표시)
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5))) {
                Spacer(modifier = Modifier.width(30.dp)) // 시간 열 너비만큼 비우기
                days.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                            .border(0.5.dp, Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day, fontSize = 12.sp)
                    }
                }
            }

            // 2. 스크롤 가능한 내용 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState) // 세로 스크롤 활성화
            ) {
                // 시간 표시열
                Column(modifier = Modifier.width(30.dp)) {
                    times.forEach { time ->
                        Box(
                            modifier = Modifier
                                .height(hourHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text("$time", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                // 요일별 이벤트 열
                days.forEachIndexed { dIndex, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(hourHeight * times.size) // 전체 높이를 시간 수만큼 지정
                            .border(0.5.dp, Color.LightGray)
                    ) {
                        // 해당 요일의 이벤트 필터링
                        events.filter { it.dayIndex == dIndex }.forEach { event ->
                            // 시작 시간 기준 (9시 시작 기준 계산)
                            val topMargin = (event.startHour - 9) * hourHeight.value

                            Box(
                                modifier = Modifier
                                    .padding(top = topMargin.dp, start = 2.dp, end = 2.dp)
                                    .fillMaxWidth()
                                    .height((event.duration * hourHeight.value).dp)
                                    .background(event.color, RoundedCornerShape(4.dp))
                                    .padding(4.dp)
                            ) {
                                Text(event.name, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}