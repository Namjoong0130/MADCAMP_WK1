package com.example.madcamp_1.ui.screen.battle

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.madcamp_1.R
import com.example.madcamp_1.data.model.TapperDto
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
    var showLeaderboard by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // [1] 상단 바
            BattleHeader(kaistWeight, postechWeight, viewModel.kaistScore, viewModel.postechScore)

            // ✅ 캐릭터를 하단으로 밀기 위한 여백
            Spacer(modifier = Modifier.weight(1.5f))

            // [2] 마스코트 섹션
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

            // [3] 리더보드 버튼 (중앙)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    onClick = { showLeaderboard = true },
                    color = Color(0xFFF8F9FA),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("리더보드 확인", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // [4] TAP 버튼
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
                                onTap = {
                                    viewModel.onTap()
                                    tapEffectTrigger++
                                }
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

        // ✅ 리더보드 다이얼로그 (중앙 팝업, Top 5)
        if (showLeaderboard) {
            Dialog(onDismissRequest = { showLeaderboard = false }) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("리더보드 (TOP 5)", fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = UnivsFontFamily)
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            TapperLeaderboardList("POSTECH", POSTECH_RED, viewModel.postechTopTappers, Modifier.weight(1f))
                            TapperLeaderboardList("KAIST", KAIST_BLUE, viewModel.kaistTopTappers, Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { showLeaderboard = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("닫기", color = Color.Gray, fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
                        }
                    }
                }
            }
        }

        if (showEasterEgg) EasterEggDialog(onDismiss = { showEasterEgg = false })
    }
}

@Composable
private fun TapperLeaderboardList(title: String, color: Color, tappers: List<TapperDto>, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(title, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, fontFamily = UnivsFontFamily)
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tappers.isEmpty()) {
                Text("탭 없음", color = Color.LightGray, fontSize = 12.sp, fontFamily = UnivsFontFamily)
            } else {
                tappers.take(5).forEachIndexed { idx, tapper ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("${idx + 1}. ${tapper.nickname}", fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f), fontFamily = UnivsFontFamily)
                        Text("${tapper.count}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                    }
                }
            }
        }
    }
}

// --- 하트/스파크/헤더 애니메이션 로직은 이전과 동일하게 유지 ---
@Composable
fun SingleHeartEmitter(trigger: Int) {
    val hearts = remember { mutableStateListOf<Long>() }
    LaunchedEffect(trigger) { if (trigger > 0) hearts.add(System.nanoTime()) }
    hearts.forEach { id ->
        key(id) {
            val startX = remember { Random.nextInt(-30, 30).dp }
            val transitionState = remember { MutableTransitionState(false) }
            LaunchedEffect(Unit) { transitionState.targetState = true; delay(1000); hearts.remove(id) }
            val transition = updateTransition(transitionState, label = "heart")
            val offsetY by transition.animateDp(transitionSpec = { tween(1000, easing = LinearOutSlowInEasing) }, label = "y") { if (it) (-180).dp else 0.dp }
            val alpha by transition.animateFloat(transitionSpec = { tween(1000, easing = LinearEasing) }, label = "a") { if (it) 0f else 1f }
            Icon(Icons.Default.Favorite, null, tint = POSTECH_RED, modifier = Modifier.offset(x = startX, y = offsetY).alpha(alpha).size(30.dp))
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
            val travel by updateTransition(transitionState, "spark").animateFloat(transitionSpec = { tween(500, easing = LinearOutSlowInEasing) }, "t") { if (it) 250f else 0f }
            Box(Modifier.graphicsLayer { val rad = spark.angle * PI / 180.0; translationX = (travel * cos(rad)).toFloat(); translationY = (travel * sin(rad)).toFloat(); alpha = if(travel > 0) 1f - (travel/250f) else 1f }.size(8.dp).background(color, CircleShape))
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
            val transition = updateTransition(transitionState, label = "ripple")
            val scale by transition.animateFloat(transitionSpec = { tween(600, easing = LinearOutSlowInEasing) }, label = "s") { if (it) 2.2f else 1.0f }
            val alpha by transition.animateFloat(transitionSpec = { tween(600) }, label = "a") { if (it) 0f else 0.4f }
            Box(Modifier.size(130.dp).scale(scale).alpha(alpha).background(color.copy(alpha = 0.3f), CircleShape))
        }
    }
}

@Composable
fun BattleHeader(kaistWeight: Float, postechWeight: Float, kScore: Long, pScore: Long) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("POSTECH", color = POSTECH_RED, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${"%,d".format(pScore)}", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("KAIST", color = KAIST_BLUE, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${"%,d".format(kScore)}", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
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
    AlertDialog(onDismissRequest = onDismiss, confirmButton = { TextButton(onClick = onDismiss) { Text("확인", color = KAIST_BLUE) } }, text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.np), null, Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.height(16.dp))
            Text("경쟁을 넘어 공존으로,\n라이벌을 넘어 동료로.\n\n우리의 진정한 승리는\n'함께함'에 있습니다.", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
        }
    }, containerColor = Color.White, shape = RoundedCornerShape(24.dp))
}

data class SparkData(val id: Long, val angle: Float)