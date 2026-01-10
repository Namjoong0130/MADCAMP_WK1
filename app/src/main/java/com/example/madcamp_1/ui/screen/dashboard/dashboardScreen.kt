package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    searchText: String,
    selectedTag: String,
    posts: List<Post>,
    onSearchChange: (String) -> Unit,
    onTagSelect: (String) -> Unit
) {
    val tags = listOf("전체", "소통", "꿀팁", "Q&A", "공지")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 1. 검색창
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchChange,
            placeholder = { Text("글 제목, 내용, 해시태그 검색") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 태그 나열 (가로 스크롤)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tags) { tag ->
                val isSelected = selectedTag == tag
                Surface(
                    modifier = Modifier.clickable { onTagSelect(tag) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFFC62828) else Color(0xFFF5F5F5), // 에브리타임 레드 컬러 느낌
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else Color.Black,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 게시글 리스트 (세로 스크롤)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            items(posts) { post ->
                PostItem(post)
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 상세 페이지 이동 */ }
            .padding(vertical = 12.dp)
    ) {
        Text(text = post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = post.content, fontSize = 14.sp, color = Color.Gray, maxLines = 1)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(text = post.time, fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "|", fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = post.author, fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = post.tag, fontSize = 12.sp, color = Color(0xFFC62828))
        }
    }
}