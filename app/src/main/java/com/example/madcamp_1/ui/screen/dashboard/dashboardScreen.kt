// app/src/main/java/com/example/madcamp_1/ui/screen/dashboard/dashboardScreen.kt
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
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.animation.animateColorAsState // animateColorAsState를 위해 필요
import androidx.compose.animation.core.tween        // tween을 위해 필요
import androidx.compose.runtime.getValue

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
                containerColor = myBrandColor,
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
                        focusedBorderColor = myBrandColor,
                        cursorColor = myBrandColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ 애니메이션이 적용된 태그 바
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tagConfigs.forEach { config ->
                        val isSelected = selectedTag == config.name

                        // 1. 배경색 애니메이션 (투명 -> 진한 색)
                        val animatedContainerColor by animateColorAsState(
                            targetValue = if (isSelected) config.color else config.color.copy(alpha = 0.08f),
                            animationSpec = tween(durationMillis = 300),
                            label = "containerColor"
                        )

                        // 2. 콘텐츠색 애니메이션 (진한 색 -> 흰색)
                        val animatedContentColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else config.color,
                            animationSpec = tween(durationMillis = 300),
                            label = "contentColor"
                        )

                        // 3. 테두리색 애니메이션 (투명도 조절)
                        val animatedBorderColor by animateColorAsState(
                            targetValue = if (isSelected) Color.Transparent else config.color.copy(alpha = 0.3f),
                            animationSpec = tween(durationMillis = 300),
                            label = "borderColor"
                        )

                        FilterChip(
                            selected = isSelected,
                            onClick = { onTagSelect(if (isSelected) "" else config.name) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = config.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = animatedContentColor // 애니메이션 적용
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = config.name,
                                        fontFamily = UnivsFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = animatedContentColor // 애니메이션 적용
                                    )
                                }
                            },
                            shape = RoundedCornerShape(13.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = animatedContainerColor, // 애니메이션 적용
                                labelColor = animatedContentColor,
                                selectedContainerColor = animatedContainerColor,
                                selectedLabelColor = animatedContentColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = animatedBorderColor, // 애니메이션 적용
                                selectedBorderColor = Color.Transparent,
                                borderWidth = 1.dp
                            )
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
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(posts) { post ->
                            PostItem(
                                post = post,
                                mySchoolId = mySchoolId,
                                myBrandColor = myBrandColor,
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
    myBrandColor: Color,
    onClick: () -> Unit
) {
    val tagColor = UiMappings.tagColor(post.category)
    val authorSchoolId = post.authorSchoolId
    val authorSchoolColor = UiMappings.schoolColor(authorSchoolId)
    val isSameSchool = !authorSchoolId.isNullOrBlank() && authorSchoolId.equals(mySchoolId, ignoreCase = true)
    val heartColor = Color(0xFFE53935)

    // ✅ 카테고리에 맞는 아이콘 가져오기
    val tagIcon = when (post.category) {
        "공지" -> Icons.Outlined.Campaign
        "소통" -> Icons.Outlined.ChatBubbleOutline
        "꿀팁" -> Icons.Outlined.Lightbulb
        "Q&A" -> Icons.Outlined.HelpOutline
        else -> Icons.Outlined.Campaign // 기본값
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, myBrandColor.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // ✅ 아이콘이 추가된 태그 디자인
                Surface(
                    color = tagColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = tagColor.copy(alpha = 0.3f)
                    )
                ) {
                    // ✅ Row를 사용하여 아이콘과 텍스트 정렬
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = tagIcon,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = tagColor // 텍스트와 동일한 색상 적용
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.category,
                            color = tagColor,
                            fontSize = 11.sp,
                            fontFamily = UnivsFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = post.displayTitle,
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Color(0xFF111111)
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

                // 하단 메타 정보 (동일)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = heartColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.likes.toString(),
                        fontFamily = UnivsFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.commentCount.toString(),
                        fontFamily = UnivsFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text("|", fontSize = 9.sp, color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = UiMappings.formatDashboardTime(post.timestamp),
                        fontFamily = UnivsFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFFBDBDBD)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text("|", fontSize = 9.sp, color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.width(8.dp))

                    val isAnonymousPost = post.displayAuthor == "익명"
                    val bgAlpha = if (isAnonymousPost) 0.05f else (if (isSameSchool) 0.18f else 0.10f)
                    val authorColor = if (isAnonymousPost) Color.Gray else authorSchoolColor

                    Surface(
                        color = authorColor.copy(alpha = bgAlpha),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = post.displayAuthor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontFamily = UnivsFontFamily,
                            fontSize = 10.sp,
                            color = authorColor,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 우측 썸네일 (동일)
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
                            .size(90.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = post.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}