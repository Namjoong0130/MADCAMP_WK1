package com.example.madcamp_1.ui.screen.write

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.MediaCreateRequest
import com.example.madcamp_1.data.model.PostCreateRequest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class WriteViewModel : ViewModel() {
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var selectedTag by mutableStateOf("소통")
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var isUploading by mutableStateOf(false)

    fun onTitleChange(newText: String) { title = newText }
    fun onContentChange(newText: String) { content = newText }
    fun onTagSelect(tag: String) { selectedTag = tag }
    fun onImageSelected(uri: Uri?) { selectedImageUri = uri }

    fun clearFields() {
        title = ""; content = ""; selectedTag = "소통"; selectedImageUri = null
    }

    // [서버 전송 핵심 함수]
    fun uploadPostToServer(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isUploading = true
            try {
                val base64Image = getBase64Image(context)
                val mediaIds = mutableListOf<String>()

                // 1. 이미지가 있다면 먼저 Media API에 등록하고 ID를 받아옴
                if (base64Image != null) {
                    val mediaResponse = RetrofitClient.apiService.uploadMedia(
                        MediaCreateRequest(url = base64Image)
                    )
                    mediaIds.add(mediaResponse.id)
                }

                // 2. 게시글 작성 API 호출 (받아온 Media ID 포함)
                RetrofitClient.apiService.createPost(
                    PostCreateRequest(
                        title = title,
                        content = content,
                        mediaIds = mediaIds
                        // tagIds는 서버의 Tag 테이블 ID와 매칭 필요 (현재는 생략)
                    )
                )
                clearFields()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isUploading = false
            }
        }
    }

    fun getBase64Image(context: Context): String? { // private 제거!
        val uri = selectedImageUri ?: return null
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}