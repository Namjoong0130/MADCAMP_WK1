package com.example.madcamp_1.ui.screen.login

import androidx.compose.foundation.Image // ✅ 추가
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // ✅ 추가
import androidx.compose.ui.res.painterResource // ✅ 추가
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.R // ✅ R 리소스 임포트 확인 (패키지명에 맞게 수정 필요)
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    username: String,
    password: String,
    errorEvent: SharedFlow<String>,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorEvent) {
        errorEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    snackbarData = data
                )
            }
        }
    ) { paddingValues ->
        // ✅ Box를 사용하여 배경 이미지와 컨텐츠를 겹침
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ✅ 1. 배경 이미지 (가장 뒤에 배치)
            Image(
                painter = painterResource(id = R.drawable.poca), // poca.png 리소스 연결
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit, // 화면을 가득 채우도록 자름 (필요에 따라 Fit 등으로 변경)
                alpha = 0.15f // ✅ 투명도 설정 (0.0f ~ 1.0f 사이 값 조절, 낮을수록 투명)
            )

            // ✅ 2. 기존 로그인 컨텐츠 (배경 위에 배치)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp), // 내부 패딩 유지
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

                // 아이디 입력창
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("이메일 주소", fontFamily = UnivsFontFamily) },
                    placeholder = { Text("example@univ.ac.kr", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.8f), // 입력창 배경을 반투명 흰색으로 하여 가독성 확보
                        unfocusedContainerColor = Color.White.copy(alpha = 0.8f)
                    )
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
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.8f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.8f)
                    )
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
}