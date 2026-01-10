package com.example.madcamp_1.ui.screen.info

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun InfoScreen(
    category: String,
    videoId: String,
    location: LatLng,
    stadiumName: String,
    records: List<MatchRecord>, // [변경]
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("$category 정보", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        // 1. YouTube 영상
        AndroidView(
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).padding(16.dp),
            factory = { context ->
                YouTubePlayerView(context).apply {
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.cueVideo(videoId, 0f)
                        }
                    })
                }
            }
        )

        // 2. 지도 영역
        Text("경기 장소: $stadiumName", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(location, 15f) }
        Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 16.dp)) {
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
                Marker(state = MarkerState(position = location), title = stadiumName)
            }
        }

        // 3. 역대 전적 영역 [새로 추가]
        Text("역대 전적", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))

        HistoryTable(records)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HistoryTable(records: List<MatchRecord>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().border(1.dp, Color.LightGray)) {
        // 헤더
        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFEEEEEE)).padding(8.dp)) {
            Text("연도", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("승리", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("결과", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // 데이터 행
        records.forEach { record ->
            HorizontalDivider(color = Color.LightGray)
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(record.year, modifier = Modifier.weight(1f), fontSize = 14.sp)
                Text(record.winner, modifier = Modifier.weight(1.5f), color = if(record.winner == "KAIST") Color.Red else Color.Black, fontSize = 14.sp)
                Column(modifier = Modifier.weight(1.5f)) {
                    Text(record.score, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if(record.note.isNotEmpty()) Text(record.note, fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}