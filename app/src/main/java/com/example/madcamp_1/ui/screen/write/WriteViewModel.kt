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
    var isAnonymous by mutableStateOf(true) // ✅ 익명 여부 상태 (기본값 true)
    var isUploading by mutableStateOf(false)

    fun onTitleChange(newText: String) { title = newText }
    fun onContentChange(newText: String) {
        if (newText.length <= 180) content = newText
    }
    fun onTagSelect(tag: String) { selectedTag = tag }
    fun onImageSelected(uri: Uri?) { selectedImageUri = uri }
    fun toggleAnonymous(value: Boolean) { isAnonymous = value } // ✅ 토글 함수

    fun clearFields() {
        title = ""; content = ""; selectedTag = "공지"; selectedImageUri = null; isAnonymous = true
    }

    fun uploadPostToServer(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isUploading = true
            try {
                val base64Image = getBase64Image(context)
                val mediaIds = mutableListOf<String>()

                if (base64Image != null) {
                    val mediaResponse = RetrofitClient.apiService.uploadMedia(
                        MediaCreateRequest(url = base64Image)
                    )
                    mediaIds.add(mediaResponse.id)
                }

                RetrofitClient.apiService.createPost(
                    PostCreateRequest(
                        title = title,
                        content = content,
                        mediaIds = mediaIds
                        // 서버 API 명세에 따라 isAnonymous 값 전달 로직 추가 가능
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

    fun getBase64Image(context: Context): String? {
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