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
import com.example.madcamp_1.ui.theme.UnivsFontFamily

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

        // --- 상단 헤더 영역 (정렬 최적화) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 유저 정보 및 학교 로고
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // 학교 로고 (가독성을 위해 높이 35dp로 살짝 조정)
                Image(
                    painter = painterResource(id = if (userSchool == "POSTECH") R.drawable.postech else R.drawable.kaist),
                    contentDescription = userSchool,
                    modifier = Modifier.height(35.dp),
                    contentScale = ContentScale.Fit
                )

                // 사람 이름 (요청대로 폰트 크기 살짝 축소: 15sp -> 14sp)
                Text(
                    text = "${userName}님",
                    // MaterialTheme.typography를 사용하면 자동으로 UnivsFontFamily가 적용됩니다.
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }

            // 오른쪽: 점수보드 (대칭 및 간격 압축)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // KAIST 로고 (60dp)
                    Image(
                        painter = painterResource(id = R.drawable.kaist),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )

                    // 점수 텍스트
                    Text(
                        text = " $kScore : $pScore ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    // POSTECH 로고 (대칭을 위해 60dp로 통일)
                    Image(
                        painter = painterResource(id = R.drawable.postech),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        // --- 일정표 영역 (가독성 대폭 개선) ---
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
                            Text(day, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF495057))
                        }
                    }
                }

                // 시간 축 및 이벤트 본문
                Row(modifier = Modifier.fillMaxWidth()) {
                    // 시간 축
                    Column(modifier = Modifier.width(40.dp)) {
                        (startHour..endHour).forEach { hour ->
                            Box(modifier = Modifier.height(hourHeight).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                                Text("$hour", fontSize = 10.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }

                    // 이벤트 영역
                    days.forEachIndexed { dIndex, _ ->
                        Box(modifier = Modifier.weight(1f).height(hourHeight * totalHours).border(0.5.dp, Color(0xFFF1F3F5))) {
                            events.filter { it.dayIndex == dIndex }.forEach { event ->
                                val topMargin = (event.startHour - startHour) * hourHeight.value
                                val eventHeight = event.duration * hourHeight.value

                                // [핵심 수정] 가독성을 높인 카드 디자인
                                Surface(
                                    modifier = Modifier
                                        .padding(top = topMargin.dp, start = 3.dp, end = 3.dp)
                                        .fillMaxWidth()
                                        .height(eventHeight.dp),
                                    // 배경은 아주 연하게 (파스텔 느낌 유지)
                                    color = event.color.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp),
                                    // 테두리도 아주 연하게
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, event.color.copy(alpha = 0.3f))
                                ) {
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        // 1. 왼쪽 컬러 포인트 바 (노랑/초록이라도 이 바를 통해 종목 구분이 확실해짐)
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .fillMaxHeight()
                                                .background(event.color) // 원색 그대로 사용
                                        )

                                        // 2. 텍스트 영역
                                        Box(modifier = Modifier.padding(5.dp)) {
                                            Text(
                                                text = event.name,
                                                // 직접 fontFamily를 지정할 수도 있습니다.
                                                fontFamily = UnivsFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = event.color.copy(alpha = 1f)
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