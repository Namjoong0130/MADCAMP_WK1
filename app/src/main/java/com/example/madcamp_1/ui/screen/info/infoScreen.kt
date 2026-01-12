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
import com.example.madcamp_1.ui.theme.UnivsFontFamily // [추가] 폰트 임포트
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// 디자인 테마 컬러 선언 (기존 동일)
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

    LaunchedEffect(location) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, 16f))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = category,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = UnivsFontFamily // [수정] 폰트 적용
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BgGray,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = BgGray,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            fontFamily = UnivsFontFamily, // [수정] 섹션 헤더 폰트 적용
            color = Color.DarkGray
        )
    }
}

// YouTubeModernCard는 이미지가 주를 이루므로 텍스트 수정 없음

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
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = UnivsFontFamily, // [수정] 경기장 이름 폰트 적용
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MatchResultItem(record: MatchRecord) {
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
                Text(
                    text = "${record.year}년 경기",
                    fontSize = 12.sp,
                    fontFamily = UnivsFontFamily, // [수정] 연도 폰트 적용
                    color = Color.Gray
                )
                Text(
                    text = record.note,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = UnivsFontFamily, // [수정] 비고 폰트 적용
                    color = winColor
                )
            }

            Surface(
                color = winColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = record.winner,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = winColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = UnivsFontFamily, // [수정] 승리팀 폰트 적용
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = record.score,
                fontWeight = FontWeight.Black,
                fontFamily = UnivsFontFamily, // [수정] 점수 폰트 적용
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
        }
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
            // 1. 유튜브 썸네일 이미지
            Image(
                painter = rememberAsyncImagePainter("https://img.youtube.com/vi/$videoId/maxresdefault.jpg"),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2. 어두운 그라데이션 오버레이
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )

            // 3. 중앙 재생 버튼 아이콘
            Surface(
                modifier = Modifier.size(64.dp),
                color = Color.White.copy(alpha = 0.9f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = KaistBlue, // 상단에 선언한 KaistBlue 사용
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 4.dp)
                )
            }

            // 4. (선택사항) 우측 하단에 '하이라이트 보기' 텍스트 추가 시 폰트 적용
            Text(
                text = "하이라이트 재생",
                fontFamily = UnivsFontFamily, // 새로 넣으신 폰트 적용
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}