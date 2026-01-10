package com.example.madcamp_1.ui.screen.info

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// 디자인 테마 컬러 선언
val KaistBlue = Color(0xFF004191)
val PostechRed = Color(0xFFE4002B)
val BgGray = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    category: String,
    videoId: String,
    location: LatLng,
    stadiumName: String,
    records: List<MatchRecord>,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 16f)
    }

    // 위치 데이터 변경 시 카메라 부드럽게 이동
    LaunchedEffect(location) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, 16f))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(category, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                // [수정] 상단바 배경색을 메인 배경색과 통일
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BgGray,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = BgGray,
        // [수정] 시스템 기본 여백 초기화하여 하단 여백 버그 수정
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // [수정] 상단바 영역만 패딩 적용 (하단 여백 중복 방지)
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. 하이라이트 영상 섹션
            SectionHeader(title = "실시간 중계 및 하이라이트", icon = Icons.Default.PlayCircle)
            YouTubeModernCard(videoId = videoId) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
                context.startActivity(intent)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. 경기 장소 섹션
            SectionHeader(title = "경기 장소", icon = Icons.Default.Map)
            StadiumLocationCard(stadiumName, cameraPositionState, location)

            Spacer(modifier = Modifier.height(28.dp))

            // 3. 역대 전적 섹션
            SectionHeader(title = "최근 전적", icon = Icons.Default.History)
            records.forEach { record ->
                MatchResultItem(record)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // [수정] 하단 바와의 간격 최적화
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        // [추가] 텍스트 앞 아이콘
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun YouTubeModernCard(videoId: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = rememberAsyncImagePainter("https://img.youtube.com/vi/$videoId/maxresdefault.jpg"),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )
            Surface(
                modifier = Modifier.size(64.dp),
                color = Color.White.copy(alpha = 0.9f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = KaistBlue,
                    modifier = Modifier.size(40.dp).padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun StadiumLocationCard(name: String, state: CameraPositionState, location: LatLng) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = state,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                ) {
                    Marker(state = MarkerState(position = location))
                }
            }
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = KaistBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MatchResultItem(record: MatchRecord) {
    // [수정] 이긴 팀에 따라 컬러 자동 결정 (KAIST: 파랑, POSTECH: 빨강)
    val winColor = if (record.winner == "KAIST") KaistBlue else PostechRed

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${record.year}년 경기", fontSize = 12.sp, color = Color.Gray)
                // [수정] 승리 텍스트(홈승 등)도 팀 컬러에 맞게 변경
                Text(
                    text = record.note,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = winColor
                )
            }

            // 승리 팀 태그
            Surface(
                color = winColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = record.winner,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = winColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = record.score,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
        }
    }
}