package com.example.madcamp_1.ui.screen.write

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt // 카메라 아이콘
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    viewModel: WriteViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val tags = listOf("소통", "꿀팁", "Q&A", "공지")

    // 갤러리(Photo Picker) 런처 설정
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // [1] 상단 헤더: 취소, 제목, 게시 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "취소")
            }
            Text(
                text = "글 쓰기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text("게시", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // [2] 태그 선택 영역
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tags) { tag ->
                FilterChip(
                    selected = viewModel.selectedTag == tag,
                    onClick = { viewModel.onTagSelect(tag) },
                    label = { Text(tag) }
                )
            }
        }

        // [3] 제목 입력 필드 (Material 3 컬러 오류 수정 버전)
        TextField(
            value = viewModel.title,
            onValueChange = viewModel::onTitleChange,
            placeholder = { Text("제목") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFFC62828),
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        // [4] 본문 입력 필드 (높이 가득 채우기)
        TextField(
            value = viewModel.content,
            onValueChange = viewModel::onContentChange,
            placeholder = { Text("내용을 입력하세요.") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // [5] 하단 도구바: 카메라 아이콘 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 카메라 아이콘 버튼을 눌러 갤러리 호출
            IconButton(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt, // 플러스에서 카메라로 변경됨
                    contentDescription = "이미지 추가",
                    tint = Color.Gray
                )
            }

            // 선택된 이미지 미리보기
            if (viewModel.selectedImageUri != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Box {
                    AsyncImage(
                        model = viewModel.selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}