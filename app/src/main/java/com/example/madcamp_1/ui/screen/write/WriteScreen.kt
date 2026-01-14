package com.example.madcamp_1.ui.screen.write

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState // 추가
import androidx.compose.animation.core.tween        // 추가
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // 추가
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.madcamp_1.data.utils.AuthManager
import com.example.madcamp_1.ui.theme.UnivsFontFamily

data class WriteTagConfig(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    viewModel: WriteViewModel,
    onBack: () -> Unit,
    onComplete: (String?, String, String, String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val schoolId = AuthManager.getSchoolId()
    val isPostech = schoolId.contains("postech", ignoreCase = true)
    val brandColor = if (isPostech) Color(0xFFE0224E) else Color(0xFF005EB8)

    val tagConfigs = listOf(
        WriteTagConfig("공지", Icons.Outlined.Campaign, Color(0xFF9C27B0)),
        WriteTagConfig("소통", Icons.Outlined.ChatBubbleOutline, Color(0xFF03A9F4)),
        WriteTagConfig("꿀팁", Icons.Outlined.Lightbulb, Color(0xFFFFB300)),
        WriteTagConfig("Q&A", Icons.Outlined.HelpOutline, Color(0xFF4CAF50))
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        viewModel.onImagesSelected(uris)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("글 쓰기", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = UnivsFontFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, contentDescription = null) }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "익명",
                            fontFamily = UnivsFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (viewModel.isAnonymous) brandColor else Color.Gray
                        )
                        Switch(
                            checked = viewModel.isAnonymous,
                            onCheckedChange = { viewModel.toggleAnonymous(it) },
                            modifier = Modifier.scale(0.8f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = brandColor,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFE0E0E0),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // ✅ 애니메이션이 적용된 태그 바
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                tagConfigs.forEach { config ->
                    val isSelected = viewModel.selectedTag == config.name

                    // 1. 배경색 애니메이션
                    val animatedContainerColor by animateColorAsState(
                        targetValue = if (isSelected) config.color else config.color.copy(alpha = 0.08f),
                        animationSpec = tween(durationMillis = 300),
                        label = "containerColor"
                    )

                    // 2. 콘텐츠(텍스트/아이콘)색 애니메이션
                    val animatedContentColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else config.color,
                        animationSpec = tween(durationMillis = 300),
                        label = "contentColor"
                    )

                    // 3. 테두리색 애니메이션
                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isSelected) Color.Transparent else config.color.copy(alpha = 0.3f),
                        animationSpec = tween(durationMillis = 300),
                        label = "borderColor"
                    )

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onTagSelect(config.name) },
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
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // 제목
            TextField(
                value = viewModel.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = { Text("제목을 입력하세요", color = Color.LightGray, fontFamily = UnivsFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = UnivsFontFamily
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = Color(0xFFF0F0F0)
            )

            // 본문
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                TextField(
                    value = viewModel.content,
                    onValueChange = viewModel::onContentChange,
                    placeholder = { Text("내용을 입력하세요. (최대 180자)", color = Color.LightGray, fontFamily = UnivsFontFamily) },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontFamily = UnivsFontFamily,
                        lineHeight = 24.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Text(
                    text = "${viewModel.content.length} / 180",
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                    color = if (viewModel.content.length >= 180) brandColor else Color.LightGray,
                    fontSize = 12.sp,
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 이미지 첨부 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "이미지 첨부",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontFamily = UnivsFontFamily
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = brandColor)
                    }

                    if (viewModel.selectedImageUris.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(viewModel.selectedImageUris) { idx, uri ->
                                Box {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(55.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Surface(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.TopEnd)
                                            .offset(x = 6.dp, y = (-6).dp)
                                            .clickable { viewModel.removeImageAt(idx) },
                                        color = Color.Gray,
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 게시 버튼
            Button(
                onClick = {
                    viewModel.uploadPostToServer(context) {
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = viewModel.title.isNotBlank() && viewModel.content.isNotBlank() && !viewModel.isUploading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandColor,
                    disabledContainerColor = Color(0xFFF0F0F0)
                )
            ) {
                if (viewModel.isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text(
                        "게시",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = UnivsFontFamily,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}