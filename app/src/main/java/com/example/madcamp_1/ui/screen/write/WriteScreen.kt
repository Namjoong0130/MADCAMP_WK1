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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.madcamp_1.ui.theme.UnivsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    viewModel: WriteViewModel,
    onBack: () -> Unit,
    onComplete: (String?, String, String, String) -> Unit // 이미지(Base64), 제목, 내용, 태그 전달
) {
    val context = LocalContext.current
    val tags = listOf("소통", "꿀팁", "Q&A", "공지")
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.onImageSelected(it)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // [1] 상단 헤더
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.Close, contentDescription = null) }
            Text("글 쓰기", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = UnivsFontFamily)
            Button(
                onClick = {
                    val base64 = viewModel.getBase64Image(context)
                    onComplete(base64, viewModel.title, viewModel.content, viewModel.selectedTag)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) { Text("게시", color = Color.White, fontFamily = UnivsFontFamily) }
        }

        // [2] 태그 선택
        LazyRow(modifier = Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tags) { tag ->
                FilterChip(
                    selected = viewModel.selectedTag == tag,
                    onClick = { viewModel.onTagSelect(tag) },
                    label = { Text(tag, fontFamily = UnivsFontFamily) }
                )
            }
        }

        // [3] 제목 및 본문
        TextField(
            value = viewModel.title, onValueChange = viewModel::onTitleChange,
            placeholder = { Text("제목", fontFamily = UnivsFontFamily) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
        )
        TextField(
            value = viewModel.content, onValueChange = viewModel::onContentChange,
            placeholder = { Text("내용을 입력하세요.", fontFamily = UnivsFontFamily) },
            modifier = Modifier.fillMaxWidth().weight(1f),
            textStyle = LocalTextStyle.current.copy(fontFamily = UnivsFontFamily),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )

        // [4] 하단 이미지 추가 바
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.size(48.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            ) { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray) }

            if (viewModel.selectedImageUri != null) {
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = viewModel.selectedImageUri, contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}