package com.example.madcamp_1.ui.screen.article

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (post == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFC62828))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // 작성자 및 카테고리 정보
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "익명", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "${formatPostTime(post.timestamp)} | ${post.category}", fontFamily = UnivsFontFamily, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 제목 및 본문
                Text(text = post.title, fontFamily = UnivsFontFamily, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = post.content, fontFamily = UnivsFontFamily, fontSize = 16.sp, lineHeight = 26.sp, color = Color(0xFF424242))

                // 이미지 (있는 경우)
                if (post.imageUri != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    AsyncImage(
                        model = post.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))

                // 좋아요 정보 (왼쪽 정렬)
                Row(modifier = Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "좋아요 ${post.likes}", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}