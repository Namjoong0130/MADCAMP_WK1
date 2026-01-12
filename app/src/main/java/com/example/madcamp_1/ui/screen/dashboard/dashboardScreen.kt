package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    searchText: String,
    selectedTag: String,
    posts: List<Post>,
    isLoading: Boolean,
    onSearchChange: (String) -> Unit,
    onTagSelect: (String) -> Unit,
    onNavigateToWrite: () -> Unit,
    onPostClick: (Int) -> Unit
) {
    val tags = listOf("전체", "소통", "꿀팁", "Q&A", "공지")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWrite,
                containerColor = Color(0xFFC62828),
                contentColor = Color.White,
                shape = CircleShape
            ) { Icon(Icons.Default.Edit, contentDescription = null) }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {

            // [1] 검색바 복구
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                placeholder = { Text("글 제목, 내용 검색", fontFamily = UnivsFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFC62828)
                )
            )

            // [2] 태그 바 복구 (LazyRow)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tags) { tag ->
                    val isSelected = selectedTag == tag

                    FilterChip(
                        selected = isSelected,
                        onClick = { onTagSelect(tag) },
                        label = {
                            Text(
                                text = tag,
                                fontFamily = UnivsFontFamily,
                                fontSize = 14.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            // [수정] unselectedContainerColor -> containerColor
                            containerColor = Color.White,
                            // [수정] unselectedLabelColor -> labelColor
                            labelColor = Color.Gray,

                            selectedContainerColor = Color(0xFFC62828),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFFEEEEEE), // 일반 테두리
                            selectedBorderColor = Color.Transparent, // 선택 시 테두리
                            borderWidth = 1.dp
                        )
                    )
                }
            }

            // [3] 게시글 리스트
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFC62828))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(posts) { post ->
                        PostItem(post = post, onClick = { onPostClick(post.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                // 카테고리 배지
                Surface(color = Color(0xFFC62828).copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = post.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color(0xFFC62828),
                        fontSize = 10.sp,
                        fontFamily = UnivsFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // 제목 및 내용
                Text(text = post.title, fontFamily = UnivsFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1)
                Text(text = post.content, fontFamily = UnivsFontFamily, fontSize = 13.sp, color = Color.Gray, maxLines = 2, modifier = Modifier.padding(top = 4.dp))

                Spacer(modifier = Modifier.height(12.dp))

                // 하단 정보: 시간, 익명, 좋아요
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${formatPostTime(post.timestamp)} · 익명", fontFamily = UnivsFontFamily, fontSize = 11.sp, color = Color(0xFFBDBDBD))
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.likes.toString(), fontFamily = UnivsFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                }
            }

            // 이미지 영역
            if (post.imageUri != null) {
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = post.imageUri,
                    contentDescription = null,
                    modifier = Modifier.size(75.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}