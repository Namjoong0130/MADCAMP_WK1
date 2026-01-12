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

        // --- 상단 헤더 영역 (정렬 및 크기 최적화) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // 점수판과 유저 정보의 중앙을 맞춤
        ) {
            // 왼쪽: 유저 정보 및 학교 로고
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp), // 로고와 이름 사이 간격 밀착
                horizontalAlignment = Alignment.Start
            ) {
                // 학교 로고 크기 확대 (20dp -> 45dp)
                Image(
                    painter = painterResource(id = if (userSchool == "POSTECH") R.drawable.postech else R.drawable.kaist),
                    contentDescription = userSchool,
                    modifier = Modifier.height(30.dp),
                    contentScale = ContentScale.Fit
                )

                // 사람 이름 폰트 크기 축소 (headlineSmall -> 20.sp)
                Text(
                    text = " ${userName}님",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 2.dp) // 로고 라인과 정렬
                )
            }

            // 오른쪽: 점수보드 (대칭 및 간격 압축)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), // 세로 간격 최소화
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // KAIST 로고
                    Image(
                        painter = painterResource(id = R.drawable.kaist),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )

                    // 점수 텍스트 (로고 크기에 맞춰 살짝 키움)
                    Text(
                        text = " $kScore : $pScore ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )

                    // POSTECH 로고 (KAIST와 크기 통일하여 대칭 확보)
                    Image(
                        painter = painterResource(id = R.drawable.postech),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        // --- 일정표 영역 (기존 코드 유지) ---
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

                                Surface(
                                    modifier = Modifier
                                        .padding(top = topMargin.dp, start = 4.dp, end = 4.dp)
                                        .fillMaxWidth()
                                        .height(eventHeight.dp),
                                    color = event.color.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, event.color.copy(alpha = 0.6f))
                                ) {
                                    Box(modifier = Modifier.padding(6.dp)) {
                                        Text(
                                            text = event.name,
                                            fontSize = 12.sp,
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