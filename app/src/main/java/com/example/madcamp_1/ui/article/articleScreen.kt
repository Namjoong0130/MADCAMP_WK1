package com.example.madcamp_1.ui.screen.article

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.madcamp_1.ui.screen.dashboard.Post
import com.example.madcamp_1.ui.util.UiMappings
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import com.example.madcamp_1.ui.util.dataUrlToImageBitmapOrNull
import com.example.madcamp_1.data.utils.AuthManager // 학교 정보 확인을 위해 필요

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    post: Post?,
    comments: List<UiComment>,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onToggleLike: () -> Unit,
    onSendComment: () -> Unit,
    onBack: () -> Unit
) {
    // 🎨 학교별 색상 정의
    val mySchoolId = AuthManager.getSchoolId() // 현재 로그인한 사용자의 학교 ID
    val myBrandColor = UiMappings.schoolColor(mySchoolId) // 포스텍: 빨강, 카이스트: 파랑

    val authorSchoolColor = UiMappings.schoolColor(post?.authorSchoolId)
    val isAnonymousPost = post?.isAnonymousPost ?: true
    val authorColor = if (isAnonymousPost) Color.Gray else (authorSchoolColor ?: Color.Gray)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게시글", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (post == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1️⃣ 스크롤 영역 (본문 + 댓글)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // 작성자 정보
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileCircle(color = authorColor, isAnonymous = isAnonymousPost)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = post.displayAuthor,
                            fontFamily = UnivsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF111111)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = UiMappings.formatRelativeTime(post.timestamp),
                                fontFamily = UnivsFontFamily,
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                            // ✅ 구분선 변경: '•' -> '|' 및 간격 조정
                            Text(
                                text = " | ",
                                color = Color(0xFFEEEEEE),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                            Text(
                                text = post.category,
                                fontFamily = UnivsFontFamily,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 제목 및 본문
                Text(
                    text = post.displayTitle,
                    fontFamily = UnivsFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 30.sp,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = post.content,
                    fontFamily = UnivsFontFamily,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF333333)
                )

                // 이미지 영역
                val urls = post.medias.mapNotNull { it.url?.takeIf { u -> u.isNotBlank() } }
                if (urls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(urls.size) { idx ->
                            val decoded = dataUrlToImageBitmapOrNull(urls[idx])
                            val cardModifier = Modifier.width(280.dp).height(200.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF8F9FA))
                            if (decoded != null) Image(bitmap = decoded, null, modifier = cardModifier, contentScale = ContentScale.Crop)
                            else AsyncImage(model = urls[idx], null, modifier = cardModifier, contentScale = ContentScale.Crop)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))

                // 좋아요/댓글 수
                Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleLike) {
                        Icon(
                            imageVector = if (post.likedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (post.likedByMe) Color(0xFFE53935) else Color.LightGray
                        )
                    }
                    Text("${post.likes}", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${post.commentCount}", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                HorizontalDivider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(20.dp))

                // 댓글 섹션
                Text("댓글", fontFamily = UnivsFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(12.dp))

                comments.forEach { c ->
                    ChatBubble(comment = c, postAuthorSchoolId = post.authorSchoolId)
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 2️⃣ 고정된 하단 입력창
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Column {
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = onCommentTextChange,
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("따뜻한 댓글을 남겨주세요", fontFamily = UnivsFontFamily, fontSize = 14.sp) },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF2F2F2),
                                unfocusedContainerColor = Color(0xFFF2F2F2),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSendComment() })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // ✅ 버튼 색상 변경: 학교 소속 브랜드 컬러 적용
                        IconButton(
                            onClick = onSendComment,
                            modifier = Modifier.background(myBrandColor, CircleShape).size(40.dp)
                        ) {
                            Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    comment: UiComment,
    postAuthorSchoolId: String?
) {
    val schoolColor = UiMappings.schoolColor(comment.authorSchoolId)
    val isSameSchool = comment.authorSchoolId == postAuthorSchoolId
    val isAnonymous = comment.authorNickname == "익명"
    val isRight = isSameSchool

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isRight) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isRight) {
            ProfileCircle(schoolColor, isAnonymous)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (isRight) {
                Text(
                    text = UiMappings.formatRelativeTime(comment.createdAtMillis),
                    fontSize = 9.sp,
                    fontFamily = UnivsFontFamily,
                    color = Color.LightGray,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            Surface(
                color = Color(0xFFF2F2F2),
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (isRight) 16.dp else 2.dp,
                    bottomEnd = if (isRight) 2.dp else 16.dp
                ),
                modifier = Modifier.widthIn(max = 220.dp)
            ) {
                Text(
                    text = comment.content,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 14.sp, fontFamily = UnivsFontFamily,
                    color = Color(0xFF222222), lineHeight = 18.sp
                )
            }

            if (!isRight) {
                Text(
                    text = UiMappings.formatRelativeTime(comment.createdAtMillis),
                    fontSize = 9.sp, fontFamily = UnivsFontFamily,
                    color = Color.LightGray, modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        if (isRight) {
            Spacer(modifier = Modifier.width(8.dp))
            ProfileCircle(schoolColor, isAnonymous)
        }
    }
}

@Composable
private fun ProfileCircle(color: Color, isAnonymous: Boolean) {
    val finalColor = if (isAnonymous) Color.LightGray else color
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(finalColor.copy(alpha = 0.15f))
            .border(1.5.dp, finalColor.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp), tint = finalColor)
    }
}