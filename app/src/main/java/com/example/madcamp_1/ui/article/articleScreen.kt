package com.example.madcamp_1.ui.screen.article

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val urls = remember(post.medias) { post.medias.map { it.url } }

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

            // ✅ 여러 장 이미지
            if (urls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(urls) { u ->
                        val decoded = remember(u) { dataUrlToImageBitmapOrNull(u) }

                        val cardModifier = Modifier
                            .width(320.dp)
                            .heightIn(min = 180.dp, max = 360.dp) // 너무 길어지는 것 방지
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))

                        if (decoded != null) {
                            Image(
                                bitmap = decoded,
                                contentDescription = null,
                                modifier = cardModifier,
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            AsyncImage(
                                model = u,
                                contentDescription = null,
                                modifier = cardModifier,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))

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
