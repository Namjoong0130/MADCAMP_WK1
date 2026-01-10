package com.example.madcamp_1.ui.screen.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.R

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

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(16.dp)) {

        // --- 상단 헤더 영역 (로고 상단, 이름 하단) ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center) {

                Image(
                    painter = painterResource(id = if (userSchool == "POSTECH") R.drawable.postech else R.drawable.kaist),
                    contentDescription = userSchool,
                    modifier = Modifier.height(20.dp), // 로고 크기 대폭 확대
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(3.dp))

                // 1. 학교 로고 (크기를 키우고 상단에 배치)
                Text(
                    text = userName + "님",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.DarkGray
                )
            }

            // 3. 스코어보드 (디자인 개선)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(id = R.drawable.kaist), contentDescription = null, modifier = Modifier.size(60.dp))
                    Text(
                        text = " $kScore : $pScore  ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Image(painter = painterResource(id = R.drawable.postech), contentDescription = null, modifier = Modifier.size(80.dp))
                }
            }
        }

        // --- 일정표 영역 ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
        ) {
            val totalHeight = maxHeight
            val hourHeight = (totalHeight - 44.dp) / totalHours

            Column {
                // 요일 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(Color(0xFFF1F3F5), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                    days.forEach { day ->
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            Text(day, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF495057))
                        }
                    }
                }

                // 시간 축 및 이벤트 본문
                Row(modifier = Modifier.fillMaxWidth()) {
                    // 시간 축
                    Column(modifier = Modifier.width(40.dp)) {
                        (startHour..endHour).forEach { hour ->
                            Box(modifier = Modifier.height(hourHeight).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                                Text("$hour", fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }

                    // 이벤트 영역
                    days.forEachIndexed { dIndex, _ ->
                        Box(modifier = Modifier.weight(1f).height(hourHeight * totalHours).border(0.5.dp, Color(0xFFF1F3F5))) {
                            events.filter { it.dayIndex == dIndex }.forEach { event ->
                                val topMargin = (event.startHour - startHour) * hourHeight.value
                                val eventHeight = event.duration * hourHeight.value

                                // 가독성 해결: 파스텔톤 배경 + 진한 텍스트 조합
                                Surface(
                                    modifier = Modifier
                                        .padding(top = topMargin.dp, start = 4.dp, end = 4.dp)
                                        .fillMaxWidth()
                                        .height(eventHeight.dp),
                                    // 배경은 연하게(파스텔), 테두리는 진하게 하여 대비 생성
                                    color = event.color.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, event.color.copy(alpha = 0.6f))
                                ) {
                                    Box(modifier = Modifier.padding(6.dp)) {
                                        Text(
                                            text = event.name,
                                            fontSize = 12.sp,
                                            // 텍스트를 배경보다 훨씬 진한 색으로 설정하여 가독성 확보
                                            color = event.color.copy(alpha = 1f),
                                            fontWeight = FontWeight.ExtraBold,
                                            lineHeight = 14.sp
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