package com.example.madcamp_1.ui.screen.article

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.madcamp_1.ui.screen.dashboard.Post
import com.example.madcamp_1.ui.screen.dashboard.formatPostTime
import com.example.madcamp_1.ui.theme.UnivsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(post: Post?, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게시글", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (post == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFC62828))
            }
            return@Scaffold
        }

        // base64(data URL) 디코딩 시도
        val decodedImage = remember(post.imageUri) {
            post.imageUri?.let { dataUrlToImageBitmapOrNull(it) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // 작성자/시간/카테고리
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = post.author.ifBlank { "익명" },
                        fontFamily = UnivsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${formatPostTime(post.timestamp)} | ${post.category}",
                        fontFamily = UnivsFontFamily,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 제목/본문
            Text(
                text = post.title,
                fontFamily = UnivsFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = post.content,
                fontFamily = UnivsFontFamily,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                color = Color(0xFF424242)
            )

            // 이미지: base64 우선 → 실패 시 URL/데이터스킴을 Coil로 폴백
            if (!post.imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(24.dp))

                if (decodedImage != null) {
                    Image(
                        bitmap = decodedImage,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                } else {
                    AsyncImage(
                        model = post.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))

            // 좋아요
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "좋아요 ${post.likes}",
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * "data:image/jpeg;base64,...." 같은 data URL을 ImageBitmap으로 변환합니다.
 * - base64가 아니거나 디코딩 실패 시 null (AsyncImage로 폴백)
 */
private fun dataUrlToImageBitmapOrNull(dataUrl: String): ImageBitmap? {
    return try {
        val base64Part = dataUrl.substringAfter("base64,", missingDelimiterValue = "")
        if (base64Part.isBlank()) return null

        val bytes = Base64.decode(base64Part, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        bitmap.asImageBitmap()
    } catch (_: Exception) {
        null
    }
}
