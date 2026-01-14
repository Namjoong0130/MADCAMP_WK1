package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.madcamp_1.data.utils.AuthManager
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import com.example.madcamp_1.ui.util.UiMappings
import com.example.madcamp_1.ui.util.dataUrlToImageBitmapOrNull

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
    val myBrandColor = UiMappings.schoolColor(mySchoolId)

    // ✅ 학교별 배경 테마색 (포스텍: 연한 레드, 카이스트: 연한 블루)
    val isPostech = mySchoolId.contains("postech", ignoreCase = true)
    val themeBackgroundColor = if (isPostech) Color(0xFFFFF5F5) else Color(0xFFF0F7FF)

    val tagConfigs = listOf(
        TagUIConfig("공지", Icons.Outlined.Campaign, UiMappings.tagColor("공지")),
        TagUIConfig("소통", Icons.Outlined.ChatBubbleOutline, UiMappings.tagColor("소통")),
        TagUIConfig("꿀팁", Icons.Outlined.Lightbulb, UiMappings.tagColor("꿀팁")),
        TagUIConfig("Q&A", Icons.Outlined.HelpOutline, UiMappings.tagColor("Q&A"))
    )

    val state = rememberPullToRefreshState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWrite,
                containerColor = myBrandColor, // ✅ 카이스트일 땐 블루 버튼
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 0.dp, end = 0.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        },
        containerColor = themeBackgroundColor
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, bottom = 0.dp),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = state,
                    isRefreshing = isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = Color.White,
                    color = myBrandColor
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // ✅ 학교 테마가 완벽 적용된 검색창
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchChange,
                    placeholder = { Text("글 제목, 내용 검색", fontFamily = UnivsFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = myBrandColor.copy(alpha = 0.7f) // ✅ 아이콘 색상 학교 테마 반영
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = myBrandColor.copy(alpha = 0.2f), // ✅ 테두리 학교 테마
                        focusedBorderColor = myBrandColor, // ✅ 포커스 시 학교 테마
                        cursorColor = myBrandColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 태그 바 (기존 애니메이션 로직 유지)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tagConfigs.forEach { config ->
                        val isSelected = selectedTag == config.name
                        val animatedContainerColor by animateColorAsState(if (isSelected) config.color else config.color.copy(alpha = 0.08f), label = "")
                        val animatedContentColor by animateColorAsState(if (isSelected) Color.White else config.color, label = "")
                        val animatedBorderColor by animateColorAsState(if (isSelected) Color.Transparent else config.color.copy(alpha = 0.3f), label = "")

                        FilterChip(
                            selected = isSelected,
                            onClick = { onTagSelect(if (isSelected) "" else config.name) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(config.icon, null, modifier = Modifier.size(15.dp), tint = animatedContentColor)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(config.name, fontFamily = UnivsFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = animatedContentColor)
                                }
                            },
                            shape = RoundedCornerShape(13.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = animatedContainerColor,
                                labelColor = animatedContentColor,
                                selectedContainerColor = animatedContainerColor,
                                selectedLabelColor = animatedContentColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = animatedBorderColor, selectedBorderColor = Color.Transparent)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading && posts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = myBrandColor)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 0.dp)
                    ) {
                        items(posts) { post ->
                            PostItem(post = post, mySchoolId = mySchoolId, myBrandColor = myBrandColor, onClick = { onPostClick(post.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostItem(post: Post, mySchoolId: String, myBrandColor: Color, onClick: () -> Unit) {
    val tagColor = UiMappings.tagColor(post.category)
    val authorSchoolColor = UiMappings.schoolColor(post.authorSchoolId)
    val isSameSchool = post.authorSchoolId?.equals(mySchoolId, ignoreCase = true) == true
    val tagIcon = when (post.category) {
        "공지" -> Icons.Outlined.Campaign
        "소통" -> Icons.Outlined.ChatBubbleOutline
        "꿀팁" -> Icons.Outlined.Lightbulb
        "Q&A" -> Icons.Outlined.HelpOutline
        else -> Icons.Outlined.Campaign
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, myBrandColor.copy(alpha = 0.15f)), // ✅ 카드 테두리 학교색 반영
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(color = tagColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, tagColor.copy(alpha = 0.3f))) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(tagIcon, null, modifier = Modifier.size(12.dp), tint = tagColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(post.category, color = tagColor, fontSize = 11.sp, fontFamily = UnivsFontFamily, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(post.displayTitle, fontFamily = UnivsFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1, color = Color(0xFF111111))
                Text(post.content, fontFamily = UnivsFontFamily, fontSize = 13.sp, color = Color.Gray, maxLines = 2, modifier = Modifier.padding(top = 6.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, null, tint = Color(0xFFE53935), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.likes.toString(), fontFamily = UnivsFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.commentCount.toString(), fontFamily = UnivsFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("|", fontSize = 9.sp, color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(UiMappings.formatDashboardTime(post.timestamp), fontFamily = UnivsFontFamily, fontSize = 11.sp, color = Color(0xFFBDBDBD))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("|", fontSize = 9.sp, color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.width(8.dp))
                    val isAnonymous = post.displayAuthor == "익명"
                    val authorColor = if (isAnonymous) Color.Gray else authorSchoolColor
                    Surface(color = authorColor.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp)) {
                        Text(post.displayAuthor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontFamily = UnivsFontFamily, fontSize = 10.sp, color = authorColor, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            if (!post.imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(12.dp))
                val decoded = dataUrlToImageBitmapOrNull(post.imageUri)
                if (decoded != null) {
                    Image(bitmap = decoded, null, modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                } else {
                    AsyncImage(model = post.imageUri, null, modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                }
            }
        }
    }
}