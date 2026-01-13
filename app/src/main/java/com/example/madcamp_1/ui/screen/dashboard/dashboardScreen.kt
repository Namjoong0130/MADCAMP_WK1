package com.example.madcamp_1.ui.screen.dashboard

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember // ✅ 필수 임포트
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap // ✅ 필수 임포트
import androidx.compose.ui.graphics.asImageBitmap // ✅ 필수 임포트
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.madcamp_1.data.utils.AuthManager
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import com.example.madcamp_1.ui.util.dataUrlToImageBitmapOrNull


// [1] 태그 디자인 데이터 모델
data class TagUIConfig(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

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
    onPostClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val schoolId = AuthManager.getSchoolId()
    val isPostech = schoolId.contains("postech", ignoreCase = true)
    val brandColor = if (isPostech) Color(0xFFE0224E) else Color(0xFF005EB8)

    val tagConfigs = listOf(
        TagUIConfig("공지", Icons.Outlined.Campaign, Color(0xFF9C27B0)),
        TagUIConfig("소통", Icons.Outlined.ChatBubbleOutline, Color(0xFF03A9F4)),
        TagUIConfig("꿀팁", Icons.Outlined.Lightbulb, Color(0xFFFFB300)),
        TagUIConfig("Q&A", Icons.Outlined.HelpOutline, Color(0xFF4CAF50))
    )

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

                Spacer(modifier = Modifier.height(16.dp))

                // --- 쫀쫀한 디자인의 태그 바 ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tagConfigs.forEach { config ->
                        val isSelected = selectedTag == config.name
                        val contentColor = if (isSelected) Color.White else config.color
                        val containerColor = if (isSelected) config.color else config.color.copy(alpha = 0.08f)
                        val borderColor = config.color.copy(alpha = 0.6f)

                        FilterChip(
                            selected = isSelected,
                            onClick = { onTagSelect(if (isSelected) "" else config.name) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = config.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = contentColor
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = config.name,
                                        fontFamily = UnivsFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = contentColor
                                    )
                                }
                            },
                            shape = RoundedCornerShape(13.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = containerColor,
                                labelColor = contentColor,
                                selectedContainerColor = config.color,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = borderColor,
                                selectedBorderColor = Color.Transparent,
                                borderWidth = 1.dp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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
                    Text(text = "${formatPostTime(post.timestamp)} · ${post.author}", fontFamily = UnivsFontFamily, fontSize = 11.sp, color = Color(0xFFBDBDBD))
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.likes.toString(), fontFamily = UnivsFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                }
            }

            if (!post.imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(12.dp))

                val decoded = remember(post.imageUri) {
                    post.imageUri?.let { dataUrlToImageBitmapOrNull(it) }
                }

                if (decoded != null) {
                    Image(
                        bitmap = decoded,
                        contentDescription = null,
                        modifier = Modifier
                            .size(75.dp) // ✅ 썸네일: 기존 크기 유지(원래값으로)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = post.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(75.dp) // ✅ 썸네일: 기존 크기 유지
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

// ✅ [중요] 누락되었던 이미지 디코딩 함수 추가
fun dataUrlToImageBitmapOrNull(dataUrl: String): ImageBitmap? {
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