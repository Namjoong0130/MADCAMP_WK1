package com.example.madcamp_1.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle // 추가
import androidx.compose.ui.text.font.FontFamily // 추가
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.ui.theme.UnivsFontFamily

@Composable
fun LoginScreen(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "로그인", fontFamily = UnivsFontFamily, fontSize = 34.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // 아이디 입력창 (커스텀 폰트 유지)
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("아이디") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 비밀번호 입력창 (입력 텍스트만 기본 폰트로 설정)
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("비밀번호") }, // 레이블은 UnivsFont가 적용됩니다.
            visualTransformation = PasswordVisualTransformation(),
            // [핵심] textStyle을 사용하여 입력 영역만 기본 폰트로 강제 지정
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Default,
                letterSpacing = 2.sp // 비밀번호 점 사이 간격을 넓히면 더 깔끔합니다.
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 로그인 버튼
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그인하기", fontFamily = UnivsFontFamily)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically // 텍스트와 버튼의 높이 정렬
        ) {
            Text(
                text = "계정이 없으신가요? ",
                fontSize = 14.sp,
                // 필요 시 fontFamily = UnivsFontFamily 추가
            )
            TextButton(
                onClick = onRegisterClick,
                contentPadding = PaddingValues(0.dp) // 버튼 내부 기본 여백 제거로 텍스트 밀착
            ) {
                Text(
                    text = "회원가입",
                    fontFamily = UnivsFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}