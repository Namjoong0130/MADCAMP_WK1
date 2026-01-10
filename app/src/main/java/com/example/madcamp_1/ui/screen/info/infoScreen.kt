package com.example.madcamp_1.ui.screen.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun InfoScreen(
    videoId: String,
    location: LatLng,
    stadiumName: String
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // 전체 화면 스크롤 가능
    ) {
        // 1. 상단: YouTube 영상 영역
        Text(
            "경기 생중계",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
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

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 하단: Google 지도 영역
        Text(
            "경기 장소: $stadiumName",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, 15f)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp) // 지도 높이 고정
                .padding(bottom = 16.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // 경기장 위치에 핀 꽂기
                Marker(
                    state = MarkerState(position = location),
                    title = stadiumName,
                    snippet = "여기서 경기가 진행됩니다"
                )
            }
        }
    }
}