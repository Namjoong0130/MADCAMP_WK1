// app/src/main/java/com/example/madcamp_1/ui/screen/article/articleScreen.kt
package com.example.madcamp_1.ui.screen.article

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.madcamp_1.data.utils.AuthManager
import com.example.madcamp_1.ui.screen.dashboard.Post
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import com.example.madcamp_1.ui.util.UiMappings

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
    val mySchoolId = AuthManager.getSchoolId()
    val myBrandColor = UiMappings.schoolColor(mySchoolId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게시글", fontFamily = UnivsFontFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (post == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        val urls = remember(post.medias) {
            post.medias.mapNotNull { it.url?.takeIf { u -> u.isNotBlank() } }
        }

        val authorSchoolColor = UiMappings.schoolColor(post.authorSchoolId)
        val authorSchoolLabel = UiMappings.schoolLabel(post.authorSchoolId)
        val tagColor = UiMappings.tagColor(post.category)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // ✅ 작성자/학교뱃지/시간/태그
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.author.ifBlank { "익명" },
                            fontFamily = UnivsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // ✅ 학교 인증 뱃지(작성자 이름 옆)
                        Box(
                            modifier = Modifier
                                .background(authorSchoolColor.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = authorSchoolLabel,
                                fontFamily = UnivsFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = authorSchoolColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // ✅ 태그 칩(태그별 색)
                        Box(
                            modifier = Modifier
                                .background(tagColor.copy(alpha = 0.10f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = post.category,
                                fontFamily = UnivsFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = tagColor
                            )
                        }
                    }

                    Text(
                        text = UiMappings.formatRelativeTime(post.timestamp),
                        fontFamily = UnivsFontFamily,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = post.title,
                fontFamily = UnivsFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = post.content,
                fontFamily = UnivsFontFamily,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                color = Color(0xFF424242)
            )

            // ✅ 여러 장 이미지
            if (urls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(urls.size) { idx ->
                        val u: String = urls[idx]
                        val decoded = remember(u) { dataUrlToImageBitmapOrNull(u) }

                        val cardModifier = Modifier
                            .width(320.dp)
                            .heightIn(min = 180.dp, max = 360.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))

                        if (decoded != null) {
                            Image(
                                bitmap = decoded,
                                contentDescription = null,
                                modifier = cardModifier,
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            AsyncImage(
                                model = u,
                                contentDescription = null,
                                modifier = cardModifier,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))

            // ✅ 좋아요 토글(하트 토글 + 수) - 작성자 학교색으로 강조
            Row(
                modifier = Modifier.padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleLike) {
                    Icon(
                        imageVector = if (post.likedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = authorSchoolColor
                    )
                }
                Text(
                    text = "좋아요 ${post.likes}",
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF212121)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "댓글 ${post.commentCount}",
                    fontFamily = UnivsFontFamily,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(12.dp))

            // ✅ 댓글 목록(대화형)
            Text(
                text = "댓글",
                fontFamily = UnivsFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            comments.forEach { c ->
                ChatBubble(
                    comment = c,
                    postAuthorSchoolId = post.authorSchoolId
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ✅ 댓글 입력창: "내 학교색"으로 브랜드 일관성 유지
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onCommentTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("댓글을 입력하세요", fontFamily = UnivsFontFamily) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendComment() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = myBrandColor,
                        cursorColor = myBrandColor
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                FilledIconButton(
                    onClick = onSendComment,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = myBrandColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ChatBubble(
    comment: UiComment,
    postAuthorSchoolId: String?
) {
    // ✅ "작성자와 동일한 학교"면 우측 / 아니면 좌측
    val isSameSchoolAsAuthor = (comment.authorSchoolId != null && comment.authorSchoolId == postAuthorSchoolId)
    val align = if (isSameSchoolAsAuthor) Arrangement.End else Arrangement.Start
    val bubbleColor = UiMappings.schoolColor(comment.authorSchoolId)
    val badgeLabel = UiMappings.schoolLabel(comment.authorSchoolId)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = align
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor.copy(alpha = 0.14f))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // 닉네임 + 학교 뱃지
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorNickname ?: "익명",
                    fontFamily = UnivsFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(bubbleColor.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        fontFamily = UnivsFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = bubbleColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = comment.content,
                fontFamily = UnivsFontFamily,
                fontSize = 14.sp,
                color = Color(0xFF212121),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = UiMappings.formatRelativeTime(comment.createdAtMillis),
                fontFamily = UnivsFontFamily,
                fontSize = 10.sp,
                color = Color(0xFF616161)
            )
        }
    }
}

private fun dataUrlToImageBitmapOrNull(dataUrl: String): ImageBitmap? {
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
