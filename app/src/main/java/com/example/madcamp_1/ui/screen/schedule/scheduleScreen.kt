package com.example.madcamp_1.ui.screen.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScheduleScreen(
    events: List<ScheduleEvent>,
    userName: String,
    userLogoRes: Int,
    userSchoolDisplayName: String,
    pScore: Int,
    kScore: Int,
    selectedEvent: ScheduleEvent?,
    onEventClick: (ScheduleEvent) -> Unit,
    onDismissSheet: () -> Unit,
    onNavigateToInfo: (String) -> Unit
) {
    val days = listOf("1일차", "2일차", "3일차")
    val startHour = 9
    val endHour = 24
    val totalHours = endHour - startHour

    val schoolColor = if (userSchoolDisplayName == "POSTECH") Color(0xFFC62828) else Color(0xFF004191)
    val themeBorderColor = if (userSchoolDisplayName == "POSTECH") Color(0xFFFFCDD2) else Color(0xFFBBDEFB)
    val themeHeaderColor = if (userSchoolDisplayName == "POSTECH") Color(0xFFFFF5F5) else Color(0xFFF0F7FF)

    // [바텀 시트]
    if (selectedEvent != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            // 전야제, 개막식, 폐막식인지 확인
            val isGeneralEvent = selectedEvent.categoryKey in listOf("전야제", "개막식", "폐막식")
            // [수정 2] 버튼 유무에 따라 하단 패딩을 동적으로 조절
            val bottomPadding = if (!isGeneralEvent) 16.dp else 24.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = bottomPadding) // 고정값 40.dp 제거 후 동적 패딩 적용
            ) {
                // [수정 1] 기본 핸들과 겹치는 직접 그린 핸들바(Box) 삭제
                // Box(modifier = Modifier...) 제거됨

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = selectedEvent.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, fontFamily = UnivsFontFamily, color = schoolColor)
                Spacer(modifier = Modifier.height(16.dp))

                InfoDetailItem(Icons.Default.Info, "상세 안내", selectedEvent.content)
                InfoDetailItem(Icons.Default.LocationOn, "장소", selectedEvent.location)
                InfoDetailItem(Icons.Default.Timer, "시간", formatTimeRange(selectedEvent.startHour, selectedEvent.duration))

                // [수정 2] 버튼이 있을 때만 상단 간격과 버튼 표시
                if (!isGeneralEvent) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            onNavigateToInfo(selectedEvent.categoryKey)
                            onDismissSheet()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = schoolColor)
                    ) {
                        Text(text = "해당 경기 상세 정보 보기", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
                    }
                }
                // else 블록의 Spacer 제거됨 (동적 패딩으로 대체)
            }
        }
    }

    // --- 메인 레이아웃 (변경 없음) ---
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(52.dp), shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
                    Image(painter = painterResource(id = userLogoRes), contentDescription = null, modifier = Modifier.padding(1.5.dp), contentScale = ContentScale.Fit)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "${userName}님", fontFamily = UnivsFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = userSchoolDisplayName, fontFamily = UnivsFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = schoolColor)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color.White, RoundedCornerShape(20.dp)).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                ScoreBadge("POSTECH ", pScore, Color(0xFFC62828))
                Text(" : ", color = Color.LightGray, modifier = Modifier.padding(horizontal = 4.dp))
                ScoreBadge("KAIST ", kScore, Color(0xFF004191))
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)).border(2.dp, themeBorderColor, RoundedCornerShape(20.dp)).background(Color.White)) {
            val totalHeightDp = maxHeight
            val hourHeight = (totalHeightDp - 44.dp) / totalHours

            Column {
                Row(modifier = Modifier.fillMaxWidth().height(44.dp).background(themeHeaderColor)) {
                    Spacer(modifier = Modifier.width(40.dp))
                    days.forEach { day ->
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            Text(text = day, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = UnivsFontFamily)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    Column(modifier = Modifier.width(40.dp).fillMaxHeight().background(themeHeaderColor.copy(alpha = 0.5f))) {
                        for (hour in startHour until endHour) {
                            Box(modifier = Modifier.height(hourHeight).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                                Text(text = hour.toString(), fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxSize()) {
                        days.forEachIndexed { dIndex, _ ->
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, themeBorderColor.copy(alpha = 0.3f))) {
                                events.filter { it.dayIndex == dIndex }.forEach { event ->
                                    val topOffset = (event.startHour - startHour) * hourHeight.value
                                    val heightSize = event.duration * hourHeight.value

                                    Surface(
                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp).offset(y = topOffset.dp).fillMaxWidth().height(heightSize.dp).clickable { onEventClick(event) },
                                        color = event.color.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.5.dp, event.color.copy(alpha = 0.3f))
                                    ) {
                                        Row(modifier = Modifier.fillMaxSize()) {
                                            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(event.color, RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)))
                                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).fillMaxSize()) {
                                                Text(text = event.name, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = UnivsFontFamily, lineHeight = 14.sp, maxLines = if (event.duration < 0.8) 1 else 2, overflow = TextOverflow.Ellipsis)
                                                Text(text = formatTimeRange(event.startHour, event.duration), fontSize = 10.sp, color = Color(0xFF616161), fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
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

// InfoDetailItem, formatTimeRange, ScoreBadge 함수는 기존과 동일 (생략)
@Composable
fun InfoDetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = UnivsFontFamily)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = UnivsFontFamily, color = Color.DarkGray)
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
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = UnivsFontFamily)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = score.toString(), fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = UnivsFontFamily)
    }
}