package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.madcamp_1.data.utils.AuthManager
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import com.example.madcamp_1.ui.util.dataUrlToImageBitmapOrNull
import java.util.concurrent.TimeUnit

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
    val mySchoolId = AuthManager.getSchoolId()
    val isPostech = mySchoolId.contains("postech", ignoreCase = true)
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

                // 태그 바
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
                                mySchoolId = mySchoolId,
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
private fun PostItem(
    post: Post,
    mySchoolId: String,
    onClick: () -> Unit
) {
    val tagColor = tagColorFor(post.category)
    val authorSchoolId = post.authorSchoolId
    val isSameSchool = authorSchoolId != null && mySchoolId.isNotBlank() &&
            authorSchoolId.equals(mySchoolId, ignoreCase = true)

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

                // ✅ 태그 칩(태그별 색)
                Surface(
                    color = tagColor.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = post.category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = tagColor,
                        fontSize = 11.sp,
                        fontFamily = UnivsFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = post.title,
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1
                )

                Text(
                    text = post.content,
                    fontFamily = UnivsFontFamily,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ 하단 메타(시간 · 작성자 + 학교뱃지 · 좋아요/댓글)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDashboardTime(post.timestamp),
                        fontFamily = UnivsFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFFBDBDBD)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "·",
                        fontFamily = UnivsFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFFBDBDBD)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = post.author,
                        fontFamily = UnivsFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Bold
                    )

                    // ✅ 작성자 학교 인증 뱃지(작성자 이름 옆)
                    if (!authorSchoolId.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        SchoolBadge(
                            schoolId = authorSchoolId,
                            emphasize = isSameSchool
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 좋아요
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.likes.toString(),
                        fontFamily = UnivsFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )

                    // (선택) 댓글 수: Post에 commentCount가 있을 때만 의미 있음
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Filled.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.commentCount.toString(),
                        fontFamily = UnivsFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                }
            }

            // 썸네일
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
                            .size(75.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = post.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun SchoolBadge(
    schoolId: String,
    emphasize: Boolean
) {
    val (label, color) = schoolBadgeLabelAndColor(schoolId)
    val bg = if (emphasize) color.copy(alpha = 0.18f) else color.copy(alpha = 0.12f)
    val textColor = color

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontFamily = UnivsFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}

private fun tagColorFor(tagName: String): Color {
    return when (tagName.trim()) {
        "공지" -> Color(0xFF9C27B0)
        "소통" -> Color(0xFF03A9F4)
        "꿀팁" -> Color(0xFFFFB300)
        "Q&A" -> Color(0xFF4CAF50)
        else -> Color(0xFF607D8B) // fallback
    }
}

private fun schoolBadgeLabelAndColor(schoolId: String): Pair<String, Color> {
    val lower = schoolId.lowercase()
    return when {
        lower.contains("kaist") -> "KAIST" to Color(0xFF005EB8)
        lower.contains("postech") -> "POSTECH" to Color(0xFFE0224E)
        lower.contains("yonsei") -> "YONSEI" to Color(0xFF0D47A1)
        lower.contains("korea") -> "KOREA" to Color(0xFFB71C1C)
        else -> "SCHOOL" to Color(0xFF616161)
    }
}

/**
 * ✅ 작성 시간 표시 기준(대시보드)
 * - 0~59초: 방금
 * - 1~59분: N분 전
 * - 1~23시간: N시간 전
 * - 어제: 어제
 * - 그 외: MM/dd
 */
private fun formatDashboardTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    if (diff < 0) return "방금"

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        diff < 60_000L -> "방금"
        minutes < 60 -> "${minutes}분 전"
        hours < 24 -> "${hours}시간 전"
        days == 1L -> "어제"
        else -> {
            // 가벼운 날짜 포맷 (MM/dd)
            val month = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }.get(java.util.Calendar.MONTH) + 1
            val day = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }.get(java.util.Calendar.DAY_OF_MONTH)
            "%02d/%02d".format(month, day)
        }
    }
}
