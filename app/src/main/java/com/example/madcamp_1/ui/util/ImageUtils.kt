package com.example.madcamp_1.ui.util

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * "data:image/jpeg;base64,...." 같은 data URL을 ImageBitmap으로 변환합니다.
 * - 디코딩 실패/형식 불일치 시 null
 */
fun dataUrlToImageBitmapOrNull(dataUrl: String): ImageBitmap? {
    return try {
        if (!dataUrl.startsWith("data:", ignoreCase = true)) return null
        val base64Part = dataUrl.substringAfter("base64,", missingDelimiterValue = "")
        if (base64Part.isBlank()) return null

        val bytes = Base64.decode(base64Part, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        bitmap.asImageBitmap()
    } catch (_: Exception) {
        null
    }
}
