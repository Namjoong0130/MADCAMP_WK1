package com.example.madcamp_1.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    username: String,
    password: String,
    errorEvent: SharedFlow<String>, // 에러 수신
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // 에러 이벤트 감지 시 스낵바 노출
    LaunchedEffect(errorEvent) {
        errorEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = Color(0xFFD32F2F), // 경고용 빨간색
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    snackbarData = data
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "로그인",
                fontFamily = UnivsFontFamily,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 아이디 입력창 (이메일임을 명시)
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("이메일 주소", fontFamily = UnivsFontFamily) },
                placeholder = { Text("example@univ.ac.kr", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호 입력창
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("비밀번호", fontFamily = UnivsFontFamily) },
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Default,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("로그인하기", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("계정이 없으신가요? ", fontFamily = UnivsFontFamily, fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onRegisterClick) {
                    Text("회원가입", fontFamily = UnivsFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}