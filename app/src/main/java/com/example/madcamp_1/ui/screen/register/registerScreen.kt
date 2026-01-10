package com.example.madcamp_1.ui.screen.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    school: String,
    email: String,
    username: String,
    password: String,
    onSchoolChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("회원가입", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // --- 학교 선택 (RadioButton) ---
        Text("소속 학교를 선택해주세요", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            SchoolOption("POSTECH", school == "POSTECH") { onSchoolChange("POSTECH") }
            Spacer(modifier = Modifier.width(16.dp))
            SchoolOption("KAIST", school == "KAIST") { onSchoolChange("KAIST") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 입력 필드들 ---
        OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("이메일") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = username, onValueChange = onUsernameChange, label = { Text("아이디") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 하단 버튼들 ---
        Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
            Text("회원가입 완료")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackClick) {
            Text("이미 계정이 있나요? 로그인으로 돌아가기")
        }
    }
}

@Composable
fun SchoolOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.selectable(selected = isSelected, onClick = onClick, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null) // 클릭 처리는 Row에서 함
        Text(text = text, modifier = Modifier.padding(start = 4.dp))
    }
}