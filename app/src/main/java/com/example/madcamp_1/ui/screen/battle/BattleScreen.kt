package com.example.madcamp_1.ui.screen.battle

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ✅ Unresolved reference 해결
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // ✅ Unresolved reference 해결
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents // ✅ Unresolved reference 해결 (트로피)
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // ✅ Unresolved reference 해결
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog // ✅ Unresolved reference 해결
import com.example.madcamp_1.R
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val KAIST_BLUE = Color(0xFF005EB8)
private val POSTECH_RED = Color(0xFFE0224E)

@Composable
fun BattleScreen(viewModel: BattleViewModel) {
    val totalTaps = (viewModel.kaistScore + viewModel.postechScore).coerceAtLeast(1L)
    val kaistWeight = viewModel.kaistScore.toFloat() / totalTaps
    val postechWeight = viewModel.postechScore.toFloat() / totalTaps

    val userSchoolColor = if (viewModel.isPostechUser) POSTECH_RED else KAIST_BLUE
    val scope = rememberCoroutineScope()

    var btnScale by remember { mutableFloatStateOf(1f) }
    val animatedBtnScale by animateFloatAsState(targetValue = btnScale, label = "btnScale")
    var tapEffectTrigger by remember { mutableIntStateOf(0) }
    var showEasterEgg by remember { mutableStateOf(false) }
    var showPrizeDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            BattleHeader(kaistWeight, postechWeight, viewModel.kaistScore, viewModel.postechScore)

            Spacer(modifier = Modifier.weight(1.5f))

            Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        MascotCard("포닉스", R.drawable.phonix, postechWeight > kaistWeight, POSTECH_RED)
                        if (viewModel.isPostechUser) SingleHeartEmitter(tapEffectTrigger)
                    }
                    Text("VS", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFFF5F5F5), fontFamily = UnivsFontFamily)
                    Box(contentAlignment = Alignment.Center) {
                        MascotCard("넙죽이", R.drawable.nupjuk, kaistWeight > postechWeight, KAIST_BLUE)
                        if (!viewModel.isPostechUser) SingleHeartEmitter(tapEffectTrigger)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 상품 확인 버튼 (트로피 아이콘)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    onClick = { showPrizeDialog = true },
                    color = Color(0xFFF8F9FA),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    modifier = Modifier.height(44.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("이벤트 상품 확인", fontFamily = UnivsFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF444444))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // TAP 버튼
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp), contentAlignment = Alignment.Center) {
                EnhancedSparkEmitter(tapEffectTrigger, userSchoolColor)
                CompactRippleEffect(tapEffectTrigger, userSchoolColor)

                Surface(
                    modifier = Modifier.size(130.dp).scale(animatedBtnScale).clip(CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    val job = scope.launch { delay(7000); showEasterEgg = true }
                                    btnScale = 0.9f
                                    try { awaitRelease() } finally { job.cancel(); btnScale = 1f }
                                },
                                onTap = { viewModel.onTap(); tapEffectTrigger++ }
                            )
                        },
                    color = userSchoolColor, shadowElevation = 10.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("TAP!", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // ✅ 한 화면에 사진과 글이 모두 나타나는 다이얼로그
        if (showPrizeDialog) {
            CombinedPrizeDialog(
                userColor = userSchoolColor,
                onDismiss = { showPrizeDialog = false }
            )
        }

        if (showEasterEgg) EasterEggDialog(onDismiss = { showEasterEgg = false })
    }
}

/**
 * 사진과 설명이 통합된 커스텀 다이얼로그
 */
@Composable
fun CombinedPrizeDialog(userColor: Color, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 상단 아이콘 및 제목
                Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFB300), modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(8.dp))
                Text("응원전 배틀 이벤트", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Black, fontSize = 22.sp)

                Spacer(Modifier.height(20.dp))

                // 2. 스타벅스 이미지 카드
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.starbucks),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(20.dp))

                // 3. 이벤트 설명 (왼쪽 정렬)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "승리 학교를 위한 특별한 선물!",
                        fontFamily = UnivsFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = userColor
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "지금 진행 중인 포스텍 vs 카이스트 배틀에서 승리한 학교 학생 중 활발히 참여해주신 50분을 추첨하여 [스타벅스 아메리카노]를 드립니다.",
                        fontFamily = UnivsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF444444)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

                    Text(
                        text = "• 참여 방법: TAP 버튼을 많이 누를수록 당첨 확률 UP!\n" +
                                "• 결과 발표: 배틀 종료 후 공지사항 게시\n" +
                                "• 경품 발송: 가입된 연락처로 기프티콘 전송",
                        fontFamily = UnivsFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(24.dp))

                // 4. 확인 버튼
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = userColor)
                ) {
                    Text("열심히 탭할게요!", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
// (이하 애니메이션/헤더/마스코트 카드는 이전의 완벽한 버전과 동일함)
@Composable
fun SingleHeartEmitter(trigger: Int) {
    val hearts = remember { mutableStateListOf<Long>() }
    LaunchedEffect(trigger) { if (trigger > 0) hearts.add(System.nanoTime()) }
    hearts.forEach { id ->
        key(id) {
            val startX = remember { Random.nextInt(-35, 35).dp }
            val transitionState = remember { MutableTransitionState(false) }
            LaunchedEffect(Unit) { transitionState.targetState = true; delay(1000); hearts.remove(id) }
            val transition = updateTransition(transitionState, label = "heart")
            val offsetY by transition.animateDp(transitionSpec = { tween(1000, easing = LinearOutSlowInEasing) }, label = "y") { if (it) (-200).dp else 0.dp }
            val alpha by transition.animateFloat(transitionSpec = { tween(1000) }, label = "a") { if (it) 0f else 1f }
            Icon(Icons.Default.Favorite, null, tint = POSTECH_RED, modifier = Modifier.offset(x = startX, y = offsetY).alpha(alpha).size(32.dp))
        }
    }
}

@Composable
fun EnhancedSparkEmitter(trigger: Int, color: Color) {
    val sparks = remember { mutableStateListOf<SparkData>() }
    LaunchedEffect(trigger) { if (trigger > 0) repeat(12) { sparks.add(SparkData(System.nanoTime() + it, it * 30f)) } }
    sparks.forEach { spark ->
        key(spark.id) {
            val transitionState = remember { MutableTransitionState(false) }
            LaunchedEffect(Unit) { transitionState.targetState = true; delay(500); sparks.remove(spark) }
            val travel by updateTransition(transitionState, "spark").animateFloat(transitionSpec = { tween(500, easing = LinearOutSlowInEasing) }, "t") { if (it) 260f else 0f }
            Box(Modifier.graphicsLayer {
                val rad = spark.angle * PI / 180.0
                translationX = (travel * cos(rad)).toFloat()
                translationY = (travel * sin(rad)).toFloat()
                alpha = 1f - (travel/260f)
            }.size(8.dp).background(color, CircleShape))
        }
    }
}

@Composable
fun CompactRippleEffect(trigger: Int, color: Color) {
    val ripples = remember { mutableStateListOf<Long>() }
    LaunchedEffect(trigger) { if (trigger > 0) ripples.add(System.nanoTime()) }
    ripples.forEach { id ->
        key(id) {
            val transitionState = remember { MutableTransitionState(false) }
            LaunchedEffect(Unit) { transitionState.targetState = true; delay(600); ripples.remove(id) }
            val scale by updateTransition(transitionState, "ripple").animateFloat(transitionSpec = { tween(600, easing = LinearOutSlowInEasing) }, "s") { if (it) 2.2f else 1.0f }
            Box(Modifier.size(130.dp).scale(scale).alpha(0.3f).background(color.copy(alpha = 0.2f), CircleShape))
        }
    }
}

@Composable
fun BattleHeader(kaistWeight: Float, postechWeight: Float, kScore: Long, pScore: Long) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("POSTECH", color = POSTECH_RED, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${"%,d".format(pScore)} taps", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("KAIST", color = KAIST_BLUE, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${"%,d".format(kScore)} taps", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp))) {
            Box(Modifier.fillMaxHeight().weight(postechWeight.coerceAtLeast(0.02f)).background(POSTECH_RED))
            Box(Modifier.fillMaxHeight().weight(kaistWeight.coerceAtLeast(0.02f)).background(KAIST_BLUE))
        }
    }
}

@Composable
fun MascotCard(name: String, imgRes: Int, isWinning: Boolean, color: Color) {
    val bounce by rememberInfiniteTransition().animateFloat(0.96f, 1.04f, infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(imgRes), contentDescription = null, modifier = Modifier.size(if (isWinning) 150.dp else 115.dp).scale(if (isWinning) bounce else 1f))
        Text(name, fontWeight = FontWeight.ExtraBold, color = color, fontSize = 16.sp, fontFamily = UnivsFontFamily)
    }
}

@Composable
fun EasterEggDialog(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = { TextButton(onClick = onDismiss) { Text("확인", color = KAIST_BLUE, fontWeight = FontWeight.Bold) } }, text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.np), null, Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.height(16.dp))
            Text("경쟁을 넘어 공존으로,\n라이벌을 넘어 동료로.\n\n우리의 진정한 승리는\n'함께함'에 있습니다.", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
        }
    }, containerColor = Color.White, shape = RoundedCornerShape(24.dp))
}

data class SparkData(val id: Long, val angle: Float)