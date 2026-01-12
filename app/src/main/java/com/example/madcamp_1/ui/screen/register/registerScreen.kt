package com.example.madcamp_1.ui.screen.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madcamp_1.R
import com.example.madcamp_1.ui.theme.UnivsFontFamily
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    school: String,
    email: String,
    username: String,
    password: String,
    errorEvent: SharedFlow<String>,
    onSchoolChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }
    val schools = listOf("POSTECH", "KAIST")

    LaunchedEffect(errorEvent) {
        errorEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = Color(0xFFD32F2F), // 요청하신 경고용 빨간색
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "회원가입",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = UnivsFontFamily,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 학교 선택 (이미지 기능 복구) ---
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = school,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("소속 학교", fontFamily = UnivsFontFamily) },
                    leadingIcon = {
                        // 선택된 학교에 따라 이미지 표시
                        if (school.isNotBlank()) {
                            Image(
                                painter = painterResource(id = if (school == "POSTECH") R.drawable.postech else R.drawable.kaist),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    schools.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = if (selectionOption == "POSTECH") R.drawable.postech else R.drawable.kaist),
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(selectionOption, fontFamily = UnivsFontFamily)
                                }
                            },
                            onClick = {
                                onSchoolChange(selectionOption)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 이메일 입력
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("이메일", fontFamily = UnivsFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 아이디 입력
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("아이디", fontFamily = UnivsFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호 입력
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("비밀번호 (6자 이상)", fontFamily = UnivsFontFamily) },
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Default, letterSpacing = 2.sp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 회원가입 버튼 (여기서 모든 검증 트리거) ---
            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("회원가입", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 로그인 화면 이동
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("이미 계정이 있나요? ", fontFamily = UnivsFontFamily, fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onBackClick) {
                    Text("로그인으로 돌아가기", fontFamily = UnivsFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}