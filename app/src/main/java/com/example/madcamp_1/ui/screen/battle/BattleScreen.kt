package com.example.madcamp_1.ui.screen.battle

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.R
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BattleScreen(viewModel: BattleViewModel) {
    val totalTaps = (viewModel.kaistScore + viewModel.postechScore).coerceAtLeast(1L)
    val kaistWeight = viewModel.kaistScore.toFloat() / totalTaps
    val postechWeight = viewModel.postechScore.toFloat() / totalTaps

    val userSchoolColor = if (viewModel.isPostechUser) Color(0xFFE0224E) else Color(0xFF005EB8)
    val scope = rememberCoroutineScope()

    var btnScale by remember { mutableFloatStateOf(1f) }
    val animatedBtnScale by animateFloatAsState(targetValue = btnScale, label = "btnScale")
    var tapEffectTrigger by remember { mutableIntStateOf(0) }

    // ✅ 이스터에그 관련 상태
    var isLongPressing by remember { mutableStateOf(false) }
    var showEasterEggDialog by remember { mutableStateOf(false) }
    var longPressJob by remember { mutableStateOf<Job?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BattleHeader(kaistWeight, postechWeight, viewModel.kaistScore, viewModel.postechScore)

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 왼쪽: 포닉스 (포스텍 사용자가 누를 때 하트)
                    Box(contentAlignment = Alignment.Center) {
                        MascotCard(
                            name = "포닉스",
                            imgRes = R.drawable.phonix,
                            isWinning = postechWeight > kaistWeight,
                            color = Color(0xFFE0224E)
                        )
                        if (viewModel.isPostechUser) {
                            SingleHeartEmitter(trigger = tapEffectTrigger)
                        }
                    }

                    Text("VS", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFFEEEEEE), fontFamily = UnivsFontFamily)

                    // 오른쪽: 넙죽이 (카이스트 사용자가 누를 때 하트)
                    Box(contentAlignment = Alignment.Center) {
                        MascotCard(
                            name = "넙죽이",
                            imgRes = R.drawable.nupjuk,
                            isWinning = kaistWeight > postechWeight,
                            color = Color(0xFF005EB8)
                        )
                        if (!viewModel.isPostechUser) {
                            SingleHeartEmitter(trigger = tapEffectTrigger)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (viewModel.isPostechUser) "포닉스를 응원하세요!" else "넙죽이를 응원하세요!",
                    fontFamily = UnivsFontFamily,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                Box(contentAlignment = Alignment.Center) {
                    // 화려한 파티클과 줄어든 파동 이펙트
                    EnhancedSparkEmitter(trigger = tapEffectTrigger, color = userSchoolColor)
                    CompactRippleEffect(trigger = tapEffectTrigger, color = userSchoolColor)

                    Surface(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(animatedBtnScale)
                            .clip(CircleShape)
                            // ✅ 길게 누르기 감지를 위한 pointerInput 추가
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isLongPressing = true
                                        // 7초 타이머 시작
                                        longPressJob = scope.launch {
                                            delay(7000) // 7초 대기
                                            if (isLongPressing) {
                                                showEasterEggDialog = true // 7초 후 다이얼로그 표시
                                                isLongPressing = false
                                            }
                                        }
                                        // 버튼 눌림 애니메이션
                                        btnScale = 0.9f
                                        try {
                                            awaitRelease() // 손을 뗄 때까지 대기
                                        } finally {
                                            // 손을 떼거나 제스처가 취소되면 타이머 취소 및 상태 초기화
                                            longPressJob?.cancel()
                                            isLongPressing = false
                                            btnScale = 1f
                                        }
                                    },
                                    onTap = {
                                        // 짧은 탭 처리 (기존 로직 유지)
                                        viewModel.onTap()
                                        tapEffectTrigger++
                                    }
                                )
                            },
                        color = userSchoolColor,
                        shadowElevation = if (isLongPressing) 4.dp else 15.dp // 눌렀을 때 그림자 감소 효과
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("TAP!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // ✅ 이스터에그 다이얼로그
        if (showEasterEggDialog) {
            EasterEggDialog(onDismiss = { showEasterEggDialog = false })
        }
    }
}

// ================= ✅ 이스터에그 다이얼로그 컴포저블 =================
@Composable
fun EasterEggDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기", fontFamily = UnivsFontFamily, color = Color(0xFF005EB8))
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // ✅ np.jpg 이미지 표시 (res/drawable/np.jpg 필요)
                Image(
                    painter = painterResource(id = R.drawable.np),
                    contentDescription = "Easter Egg Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))
                // ✅ 이스터에그 문구 표시
                Text(
                    text = "경쟁을 넘어 공존으로,\n라이벌을 넘어 동료로.\n\n우리의 진정한 승리는\n'함께함'에 있습니다.",
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF424242)
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

// ================= ✅ 수정: 크기를 줄인 파동 이펙트 (CompactRipple) =================
@Composable
fun CompactRippleEffect(trigger: Int, color: Color) {
    val ripples = remember { mutableStateListOf<Long>() }
    LaunchedEffect(trigger) { if (trigger > 0) ripples.add(System.nanoTime()) }

    ripples.forEach { id ->
        key(id) {
            val duration = 600
            val transitionState = remember { MutableTransitionState(false) }
            LaunchedEffect(Unit) { transitionState.targetState = true; delay(duration.toLong()); ripples.remove(id) }
            val transition = updateTransition(transitionState, label = "ripple")

            // ✅ 기존 3.5f에서 2.0f로 축소 (적절한 파동 크기)
            val scale by transition.animateFloat(transitionSpec = { tween(duration, easing = LinearOutSlowInEasing) }, label = "s") { if (it) 2.0f else 1.0f }
            val alpha by transition.animateFloat(transitionSpec = { tween(duration) }, label = "a") { if (it) 0f else 0.6f }

            Box(modifier = Modifier.size(140.dp).scale(scale).alpha(alpha).background(color.copy(alpha = 0.3f), CircleShape))
        }
    }
}

// ================= ✅ 수정: 위로만 올라가며 페이드 아웃되는 하트 =================
@Composable
fun SingleHeartEmitter(trigger: Int) {
    val hearts = remember { mutableStateListOf<Long>() }
    LaunchedEffect(trigger) { if (trigger > 0) hearts.add(System.nanoTime()) }

    hearts.forEach { id ->
        key(id) {
            val startX = remember { Random.nextInt(-40, 40).dp }
            val duration = 1000
            val transitionState = remember { MutableTransitionState(false) }
            LaunchedEffect(Unit) { transitionState.targetState = true; delay(duration.toLong()); hearts.remove(id) }

            val transition = updateTransition(transitionState, label = "heart")
            // 위로 솟아오르는 이동 애니메이션
            val offsetY by transition.animateDp(transitionSpec = { tween(duration, easing = LinearOutSlowInEasing) }, label = "y") { if (it) (-180).dp else 0.dp }
            // ✅ 페이드 아웃 애니메이션
            val alpha by transition.animateFloat(transitionSpec = { tween(duration, easing = FastOutLinearInEasing) }, label = "a") { if (it) 0f else 1f }

            // ✅ scale 애니메이션 제거: 상하로 길어지는 현상 방지

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFE0224E),
                modifier = Modifier
                    .offset(x = startX, y = offsetY)
                    .alpha(alpha)
                    .size(35.dp) // 일정한 크기 유지
            )
        }
    }
}

// ================= ✅ 화려한 물방울(스파크) 이펙트 유지 =================
@Composable
fun EnhancedSparkEmitter(trigger: Int, color: Color) {
    val sparks = remember { mutableStateListOf<SparkData>() }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            repeat(12) { i -> sparks.add(SparkData(id = System.nanoTime() + i, angle = i * 30f)) }
        }
    }
    sparks.forEach { spark ->
        key(spark.id) {
            val distance = remember { Random.nextInt(180, 300).toFloat() }
            val duration = 500
            val transitionState = remember { MutableTransitionState(false) }
            LaunchedEffect(Unit) { transitionState.targetState = true; delay(duration.toLong()); sparks.remove(spark) }
            val transition = updateTransition(transitionState, label = "spark")

            val travel by transition.animateFloat(transitionSpec = { tween(duration, easing = LinearOutSlowInEasing) }, label = "t") { if (it) distance else 0f }
            val alpha by transition.animateFloat(transitionSpec = { tween(duration, easing = LinearEasing) }, label = "a") { if (it) 0f else 1f }

            Box(
                modifier = Modifier.graphicsLayer {
                    val rad = spark.angle * PI / 180.0
                    translationX = (travel * cos(rad)).toFloat()
                    translationY = (travel * sin(rad)).toFloat()
                    this.alpha = alpha
                }.size(10.dp).background(color, CircleShape)
            )
        }
    }
}

// ================= 기존 UI 컴포넌트 =================

@Composable
fun MascotCard(name: String, imgRes: Int, isWinning: Boolean, color: Color) {
    val transition = rememberInfiniteTransition(label = "bounce")
    val bounce by transition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "s"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = imgRes), contentDescription = null,
            modifier = Modifier.size(if (isWinning) 170.dp else 120.dp).scale(if (isWinning) bounce else 1f))
        Spacer(modifier = Modifier.height(12.dp))
        Text(name, fontWeight = FontWeight.ExtraBold, color = color, fontSize = 18.sp, fontFamily = UnivsFontFamily)
    }
}

@Composable
fun BattleHeader(kaistWeight: Float, postechWeight: Float, kScore: Long, pScore: Long) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("POSTECH", color = Color(0xFFE0224E), fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${String.format("%, d", pScore)}회", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("KAIST", color = Color(0xFF005EB8), fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${String.format("%, d", kScore)}회", fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().height(30.dp).clip(RoundedCornerShape(15.dp))) {
            Box(modifier = Modifier.fillMaxHeight().weight(postechWeight.coerceAtLeast(0.02f)).background(Color(0xFFE0224E)))
            Box(modifier = Modifier.fillMaxHeight().weight(kaistWeight.coerceAtLeast(0.02f)).background(Color(0xFF005EB8)))
        }
    }
}

data class SparkData(val id: Long, val angle: Float)