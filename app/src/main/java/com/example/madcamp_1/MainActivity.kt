package com.example.madcamp_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.madcamp_1.ui.navigation.NavGraph
import com.example.madcamp_1.ui.theme.MadCamp_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MadCamp_1Theme {
                // 이제 NavGraph가 화면 전환을 담당합니다.
                NavGraph()
            }
        }
    }
}