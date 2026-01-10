package com.example.madcamp_1.ui.screen.infoselect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.R

val BgGray = Color(0xFFF8F9FA)

// [수정] scale과 Dp 대신 Bias(비율)를 사용합니다.
data class ImageTransform(
    val verticalBias: Float = 0f // -1.0(최상단) ~ 1.0(최하단)
)

@Composable
fun SelectScreen(
    categories: List<String>,
    onCategoryClick: (String) -> Unit
) {
    val categoryImages = mapOf(
        "E-sports" to R.drawable.esports,
        "AI" to R.drawable.ai,
        "축구" to R.drawable.soccer,
        "야구" to R.drawable.baseball,
        "과학퀴즈" to R.drawable.science,
        "농구" to R.drawable.basketball,
        "해킹" to R.drawable.hacking
    )

    // [설정] 여기서 수치만 조절하세요.
    // -1f에 가까울수록 사진 윗부분이 보이고, 1f에 가까울수록 아랫부분이 보입니다.
    // 절대 여백이 생기지 않습니다.
    val transforms = mapOf(
        "축구" to ImageTransform(verticalBias = -0.4f),
        "E-sports" to ImageTransform(verticalBias = -1f),
        "농구" to ImageTransform(verticalBias = 0.0f),
        "해킹" to ImageTransform(verticalBias = 0.2f),
        "야구" to ImageTransform(verticalBias = 0.25f),
        "AI" to ImageTransform(verticalBias = -0.35f),
        "과학퀴즈" to ImageTransform(verticalBias = 0.1f) //음수가 위, 양수가 밑
    )

    Scaffold(
        containerColor = BgGray,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { category ->
                val transform = transforms[category] ?: ImageTransform()

                CategoryWideCard(
                    category = category,
                    imageResId = categoryImages[category] ?: 0,
                    verticalBias = transform.verticalBias,
                    onClick = onCategoryClick,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun CategoryWideCard(
    category: String,
    imageResId: Int,
    verticalBias: Float,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(category) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                // ContentScale.Crop은 원본 비율을 유지하며 카드를 꽉 채웁니다. (여백 방지 핵심)
                contentScale = ContentScale.Crop,
                // [핵심 문법] BiasAlignment를 사용하여 원본 사진의 노출 지점을 정교하게 선택합니다.
                alignment = BiasAlignment(horizontalBias = 0f, verticalBias = verticalBias),
                modifier = Modifier.fillMaxSize()
            )

            // 그라데이션 오버레이
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(0.2f), Color.Black.copy(0.6f))
                        )
                    )
            )

            Text(
                text = category,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 24.dp)
            )
        }
    }
}