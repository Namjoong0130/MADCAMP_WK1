package com.example.madcamp_1.ui.screen.write

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class WriteViewModel : ViewModel() {
    // UI 상태 관리
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var selectedTag by mutableStateOf("소통")
    var selectedImageUri by mutableStateOf<Uri?>(null)

    fun onTitleChange(newText: String) { title = newText }
    fun onContentChange(newText: String) { content = newText }
    fun onTagSelect(tag: String) { selectedTag = tag }
    fun onImageSelected(uri: Uri?) { selectedImageUri = uri }

    // 게시판 데이터로 넘기기 위한 초기화 로직 (선택 사항)
    fun clearFields() {
        title = ""
        content = ""
        selectedTag = "소통"
        selectedImageUri = null
    }
}