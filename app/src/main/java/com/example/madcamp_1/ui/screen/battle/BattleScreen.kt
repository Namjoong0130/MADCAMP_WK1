package com.example.madcamp_1.ui.screen.battle

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.R
import com.example.madcamp_1.data.model.PostResponse
import com.example.madcamp_1.ui.theme.UnivsFontFamily

private val KAIST_BLUE = Color(0xFF005EB8)
private val POSTECH_RED = Color(0xFFE0224E)

@Composable
fun BattleScreen(viewModel: BattleViewModel) {
    val totalLikes = (viewModel.kaistLikesTotal + viewModel.postechLikesTotal).coerceAtLeast(1L)
    val kaistWeight = viewModel.kaistLikesTotal.toFloat() / totalLikes
    val postechWeight = viewModel.postechLikesTotal.toFloat() / totalLikes

    val userSchoolColor = if (viewModel.isPostechUser) POSTECH_RED else KAIST_BLUE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ✅ 헤더: 좋아요 비율 바
        LikesBattleHeader(
            kaistWeight = kaistWeight,
            postechWeight = postechWeight,
            kaistLikes = viewModel.kaistLikesTotal,
            postechLikes = viewModel.postechLikesTotal
        )

        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = userSchoolColor)
            }
            return
        }

        viewModel.errorMessage?.let { msg ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(msg, color = Color.Gray, fontFamily = UnivsFontFamily)
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.refreshLikeBattle() },
                    colors = ButtonDefaults.buttonColors(containerColor = userSchoolColor)
                ) {
                    Text("다시 불러오기", color = Color.White, fontFamily = UnivsFontFamily)
                }
            }
            return
        }

        Spacer(Modifier.height(10.dp))

        // ✅ 마스코트 섹션 (기존 Battle 감성 유지)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MascotCard(
                name = "포닉스",
                imgRes = R.drawable.phonix,
                isWinning = postechWeight > kaistWeight,
                color = POSTECH_RED
            )

            Text(
                "VS",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFEEEEEE),
                fontFamily = UnivsFontFamily
            )

            MascotCard(
                name = "넙죽이",
                imgRes = R.drawable.nupjuk,
                isWinning = kaistWeight > postechWeight,
                color = KAIST_BLUE
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "최근 게시글(최대 200개) 기준 좋아요 합계로 집계됩니다.",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontFamily = UnivsFontFamily,
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(14.dp))

        // ✅ Top posts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopPostsCard(
                title = "POSTECH TOP",
                color = POSTECH_RED,
                posts = viewModel.postechTopPosts,
                modifier = Modifier.weight(1f)
            )
            TopPostsCard(
                title = "KAIST TOP",
                color = KAIST_BLUE,
                posts = viewModel.kaistTopPosts,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // ✅ 수동 새로고침 버튼(탭 UX)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = { viewModel.refreshLikeBattle() },
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
            ) {
                Text("새로고침", fontFamily = UnivsFontFamily)
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun LikesBattleHeader(
    kaistWeight: Float,
    postechWeight: Float,
    kaistLikes: Long,
    postechLikes: Long
) {
    val total = (kaistLikes + postechLikes).coerceAtLeast(1L)
    val kPct = (kaistLikes.toDouble() / total.toDouble() * 100.0)
    val pPct = (postechLikes.toDouble() / total.toDouble() * 100.0)

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("POSTECH", color = POSTECH_RED, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${"%,d".format(postechLikes)} likes", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
                Text(String.format("%.1f%%", pPct), color = Color.Gray, fontSize = 12.sp, fontFamily = UnivsFontFamily)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("KAIST", color = KAIST_BLUE, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${"%,d".format(kaistLikes)} likes", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
                Text(String.format("%.1f%%", kPct), color = Color.Gray, fontSize = 12.sp, fontFamily = UnivsFontFamily)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ 기존 BattleHeader의 핵심: weight로 바 분할
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .clip(RoundedCornerShape(999.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(postechWeight.coerceAtLeast(0.02f))
                    .background(POSTECH_RED)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(kaistWeight.coerceAtLeast(0.02f))
                    .background(KAIST_BLUE)
            )
        }
    }
}

@Composable
private fun TopPostsCard(
    title: String,
    color: Color,
    posts: List<PostResponse>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = color, fontWeight = FontWeight.Black, fontFamily = UnivsFontFamily)
            Spacer(Modifier.height(10.dp))

            if (posts.isEmpty()) {
                Text("표시할 게시글이 없습니다.", color = Color.Gray, fontSize = 12.sp, fontFamily = UnivsFontFamily)
                return@Column
            }

            posts.forEachIndexed { idx, p ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${idx + 1}. ${p.title}",
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = UnivsFontFamily,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "♥ ${p.likeCount}",
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontFamily = UnivsFontFamily,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MascotCard(name: String, imgRes: Int, isWinning: Boolean, color: Color) {
    val transition = rememberInfiniteTransition(label = "bounce")
    val bounce by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imgRes),
            contentDescription = null,
            modifier = Modifier
                .size(if (isWinning) 170.dp else 120.dp)
                .scale(if (isWinning) bounce else 1f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            name,
            fontWeight = FontWeight.ExtraBold,
            color = color,
            fontSize = 18.sp,
            fontFamily = UnivsFontFamily
        )
    }
}
