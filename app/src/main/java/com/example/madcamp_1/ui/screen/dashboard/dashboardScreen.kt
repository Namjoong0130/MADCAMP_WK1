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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.example.madcamp_1.data.utils.AuthManager

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
    onPostClick: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    val schoolId = AuthManager.getSchoolId()
    val isPostech = schoolId.contains("postech", ignoreCase = true)

    val brandColor = if (isPostech) Color(0xFFE0224E) else Color(0xFF005EB8)
    val tags = listOf("전체", "소통", "꿀팁", "Q&A", "공지")

    val state = rememberPullToRefreshState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWrite,
                containerColor = brandColor,
                contentColor = Color.White,
                shape = CircleShape
            ) { Icon(Icons.Default.Edit, contentDescription = null) }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        // [수정] PullToRefreshBox 내부에 Column이 온전하게 들어가도록 구조를 정렬했습니다.
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, bottom = padding.calculateBottomPadding()),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = state,
                    isRefreshing = isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = Color.White,
                    color = brandColor
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // [1] 검색바
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchChange,
                    placeholder = { Text("글 제목, 내용 검색", fontFamily = UnivsFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = brandColor
                    )
                )

                // [2] 태그 바
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
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                                selectedContainerColor = brandColor,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0xFFEEEEEE),
                                selectedBorderColor = Color.Transparent,
                                borderWidth = 1.dp
                            )
                        )
                    }
                }

                // [3] 게시글 리스트
                Spacer(modifier = Modifier.height(12.dp))

                // 데이터가 비어있을 때만 중앙 로딩바를 보여주고,
                // 이미 데이터가 있다면 PullToRefresh의 상단 인디케이터만 사용하도록 개선했습니다.
                if (isLoading && posts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = brandColor)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(posts) { post ->
                            PostItem(
                                post = post,
                                brandColor = brandColor,
                                onClick = { onPostClick(post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// [중요] PostItem은 DashboardScreen 함수 중괄호 바깥에 위치해야 합니다.
@Composable
fun PostItem(post: Post, brandColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = brandColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = post.category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        color = brandColor,
                        fontSize = 10.sp,
                        fontFamily = UnivsFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = post.title, fontFamily = UnivsFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1)
                Text(text = post.content, fontFamily = UnivsFontFamily, fontSize = 13.sp, color = Color.Gray, maxLines = 2, modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "방금 전 · 익명", fontFamily = UnivsFontFamily, fontSize = 11.sp, color = Color(0xFFBDBDBD))
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.likes.toString(), fontFamily = UnivsFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                }
            }
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