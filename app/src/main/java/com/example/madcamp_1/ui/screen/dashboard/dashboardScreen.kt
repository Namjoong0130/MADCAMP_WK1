package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // Coil 라이브러리 필요

@Composable
fun DashboardScreen(
    searchText: String,
    selectedTag: String,
    posts: List<Post>,
    onSearchChange: (String) -> Unit,
    onTagSelect: (String) -> Unit,
    onNavigateToWrite: () -> Unit
) {
    val tags = listOf("전체", "소통", "꿀팁", "Q&A", "공지")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWrite,
                containerColor = Color(0xFFC62828),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Edit, contentDescription = "글쓰기")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                placeholder = { Text("글 제목, 내용 검색") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5))
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tags) { tag ->
                    val isSelected = selectedTag == tag
                    Surface(
                        modifier = Modifier.clickable { onTagSelect(tag) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFFC62828) else Color(0xFFF5F5F5),
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(posts) { post ->
                    PostItem(post)
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content, fontSize = 14.sp, color = Color.Gray, maxLines = 2)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(text = "${post.time} | ${post.author}", fontSize = 12.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = post.tag, fontSize = 12.sp, color = Color(0xFFC62828))
            }
        }

        if (post.imageUri != null) {
            Spacer(modifier = Modifier.width(12.dp))
            AsyncImage(
                model = post.imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
