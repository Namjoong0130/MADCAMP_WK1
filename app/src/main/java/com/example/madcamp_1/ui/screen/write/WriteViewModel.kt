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
import com.example.madcamp_1.data.utils.AuthManager
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class WriteViewModel : ViewModel() {
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var selectedTag by mutableStateOf("공지")
    var selectedImageUris by mutableStateOf<List<Uri>>(emptyList())
    var isAnonymous by mutableStateOf(true)
    var isUploading by mutableStateOf(false)

    fun onTitleChange(newText: String) { title = newText }
    fun onContentChange(newText: String) {
        if (newText.length <= 180) content = newText
    }
    fun onTagSelect(tag: String) { selectedTag = tag }
    fun onImagesSelected(uris: List<Uri>) { selectedImageUris = uris }
    fun removeImageAt(index: Int) {
        selectedImageUris = selectedImageUris.toMutableList().also { list ->
            if (index in list.indices) list.removeAt(index)
        }
    }
    fun toggleAnonymous(value: Boolean) { isAnonymous = value }

    fun clearFields() {
        title = ""
        content = ""
        selectedTag = "공지"
        selectedImageUris = emptyList()
        isAnonymous = true
    }

    fun uploadPostToServer(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isUploading = true
            try {
                val mediaIds = mutableListOf<String>()
                for (uri in selectedImageUris) {
                    val base64 = toBase64Jpeg(context, uri) ?: continue
                    val mediaRes = RetrofitClient.apiService.uploadMedia(MediaCreateRequest(url = base64))
                    mediaIds.add(mediaRes.id)
                }

                val finalTitle = if (isAnonymous) "[익명] $title" else title
                val finalNickname = if (isAnonymous) "익명" else AuthManager.getNickname()

                RetrofitClient.apiService.createPost(
                    PostCreateRequest(
                        title = finalTitle, // 👈 제목에 정보를 숨겨서 보냄
                        content = content,
                        visibility = "PUBLIC", // 👈 에러 방지 및 대시보드 노출을 위해 PUBLIC 고정
                        tagIds = listOf(selectedTag),
                        mediaIds = mediaIds,
                        authorNickname = finalNickname
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

    private fun toBase64Jpeg(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri).use { input ->
                if (input == null) return null
                val bitmap = BitmapFactory.decodeStream(input) ?: return null
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val byteArray = outputStream.toByteArray()
                "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}