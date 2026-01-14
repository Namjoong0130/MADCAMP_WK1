package com.example.madcamp_1.ui.screen.article

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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

        val urls = remember(post.medias) {
            post.medias.mapNotNull { it.url?.takeIf { u -> u.isNotBlank() } }
        }

        // ✅ 디자인 설정을 위한 변수 (대시보드와 동일한 로직)
        val isAnonymousPost = post.isAnonymousPost
        val authorSchoolColor = UiMappings.schoolColor(post.authorSchoolId)
        val authorColor = if (isAnonymousPost) Color.Gray else authorSchoolColor
        val bgAlpha = if (isAnonymousPost) 0.08f else 0.12f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // 작성자 영역
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8F9FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    // ✅ 대시보드 스타일의 작성자 이름 (배경 포함)
                    Surface(
                        color = authorColor.copy(alpha = bgAlpha),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = post.displayAuthor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 0.dp),
                            fontFamily = UnivsFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = authorColor
                        )
                    }



                    Text(
                        text = "${UiMappings.formatRelativeTime(post.timestamp)} | ${post.category}",
                        fontFamily = UnivsFontFamily,
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 제목
            Text(
                text = post.displayTitle,
                fontFamily = UnivsFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 32.sp,
                color = Color(0xFF111111)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 본문
            Text(
                text = post.content,
                fontFamily = UnivsFontFamily,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                color = Color(0xFF333333)
            )

            // 이미지 영역 (동일)
            if (urls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(urls.size) { idx ->
                        val u: String = urls[idx]
                        val decoded = remember(u) { dataUrlToImageBitmapOrNull(u) }
                        val cardModifier = Modifier
                            .width(300.dp)
                            .heightIn(min = 180.dp, max = 360.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF8F9FA))

                        if (decoded != null) {
                            Image(
                                bitmap = decoded,
                                contentDescription = null,
                                modifier = cardModifier,
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = u,
                                contentDescription = null,
                                modifier = cardModifier,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF5F5F5))

            // 좋아요/댓글 수 영역
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleLike) {
                    Icon(
                        imageVector = if (post.likedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (post.likedByMe) Color(0xFFE53935) else Color.LightGray
                    )
                }
                Text(
                    text = "${post.likes}",
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF424242)
                )

                Spacer(modifier = Modifier.width(20.dp))

                Icon(
                    Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${post.commentCount}",
                    fontFamily = UnivsFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
            }

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(24.dp))

            // 댓글 영역
            Text(
                text = "댓글",
                fontFamily = UnivsFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF111111)
            )
            Spacer(modifier = Modifier.height(16.dp))

            comments.forEach { c ->
                ChatBubble(
                    comment = c,
                    postAuthorSchoolId = post.authorSchoolId
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 댓글 입력창
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onCommentTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("따뜻한 댓글을 남겨주세요", fontFamily = UnivsFontFamily, fontSize = 14.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = authorColor,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendComment() })
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onSendComment,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = authorColor),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
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
    val isAnonymousComment = comment.authorNickname == "익명"
    val isSameSchoolAsPostAuthor = (comment.authorSchoolId != null && comment.authorSchoolId == postAuthorSchoolId)

    // ✅ 대시보드 스타일의 닉네임 배경 처리
    val schoolColor = UiMappings.schoolColor(comment.authorSchoolId)
    val nameBgColor = if (isAnonymousComment) Color.Gray else schoolColor
    val nameBgAlpha = if (isAnonymousComment) 0.08f else 0.15f

    val align = if (isSameSchoolAsPostAuthor) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = align
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8F9FA)) // 말풍선 배경은 연한 회색으로 통일 (가독성)
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // ✅ 닉네임 영역 (대시보드와 동일한 Surface 스타일)
            Surface(
                color = nameBgColor.copy(alpha = nameBgAlpha),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = comment.authorNickname ?: "익명",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontFamily = UnivsFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = nameBgColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.content,
                fontFamily = UnivsFontFamily,
                fontSize = 14.sp,
                color = Color(0xFF424242),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = UiMappings.formatRelativeTime(comment.createdAtMillis),
                fontFamily = UnivsFontFamily,
                fontSize = 10.sp,
                color = Color.LightGray
            )
        }
    }
}