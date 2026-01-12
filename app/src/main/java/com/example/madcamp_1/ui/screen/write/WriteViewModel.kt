package com.example.madcamp_1.ui.screen.write

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import java.io.ByteArrayOutputStream

class WriteViewModel : ViewModel() {
    // 1. UI 상태 관리
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var selectedTag by mutableStateOf("소통")
    var selectedImageUri by mutableStateOf<Uri?>(null)

    // 2. 입력 핸들러
    fun onTitleChange(newText: String) { title = newText }
    fun onContentChange(newText: String) { content = newText }
    fun onTagSelect(tag: String) { selectedTag = tag }
    fun onImageSelected(uri: Uri?) { selectedImageUri = uri }

    // 3. 필드 초기화
    fun clearFields() {
        title = ""
        content = ""
        selectedTag = "소통"
        selectedImageUri = null
    }

    /**
     * [이미지 변환 로직]
     * Uri 형태의 이미지를 서버 저장용 Base64 문자열로 변환합니다.
     */
    fun getBase64Image(context: Context): String? {
        val uri = selectedImageUri ?: return null

        return try {
            // ContentResolver를 통해 이미지 스트림 열기
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()

            // [추가 수정 사항] 서버 부하를 줄이기 위해 JPEG 70% 품질로 압축
            // 원본이 너무 크면 전송 중 오류가 발생할 수 있습니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()

            // Coil 라이브러리가 바로 인식할 수 있도록 "data:image/..." 프리픽스 추가
            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}