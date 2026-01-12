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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    school: String, // 여기서 빈 문자열("")이 들어와야 처음에 빈칸으로 보입니다.
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
    var expanded by remember { mutableStateOf(false) }
    val schools = listOf("POSTECH", "KAIST")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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

        // --- 학교 선택 (빈칸으로 시작하는 리스트 박스) ---
        Text(
            text = "소속 학교를 선택해주세요",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = UnivsFontFamily,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = school, // school이 ""이면 빈칸으로 보임
                onValueChange = {},
                readOnly = true,
                // [수정] placeholder: 값이 비어있을 때만 나타나는 가이드 문구
                placeholder = {
                    Text("학교를 선택해주세요", fontFamily = UnivsFontFamily, color = Color.LightGray)
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                leadingIcon = {
                    // [수정] 학교가 선택되었을 때만 로고를 표시 (빈칸일 땐 안 보임)
                    if (school.isNotBlank()) {
                        Image(
                            painter = painterResource(id = if (school == "POSTECH") R.drawable.postech else R.drawable.kaist),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp) // 선택 후 로고 크기 확대
                        )
                    }
                },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                schools.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                // [수정] 리스트 박스 안의 사진 크기 대폭 확대
                                Image(
                                    painter = painterResource(id = if (selectionOption == "POSTECH") R.drawable.postech else R.drawable.kaist),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = selectionOption,
                                    fontFamily = UnivsFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                        },
                        onClick = {
                            onSchoolChange(selectionOption)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 이메일/아이디 필드 ---
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("이메일", fontFamily = UnivsFontFamily) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("아이디", fontFamily = UnivsFontFamily) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- 비밀번호 필드 (기본 폰트로 마스킹 깨짐 방지) ---
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("비밀번호", fontFamily = UnivsFontFamily) },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Default, // 보안 점(●) 표시용
                letterSpacing = 2.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 회원가입 버튼 ---
        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("회원가입", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center, // 가로 중앙 정렬
            verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬 (글자 높이 맞춤)
        ) {
            Text(
                text = "이미 계정이 있나요? ",
                fontFamily = UnivsFontFamily,
                fontSize = 14.sp,
                color = Color.Gray
            )
            TextButton(
                onClick = onBackClick,
                contentPadding = PaddingValues(0.dp) // 텍스트 간격 밀착을 위해 기본 여백 제거
            ) {
                Text(
                    text = "로그인으로 돌아가기",
                    fontFamily = UnivsFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, // 강조를 위해 굵게 설정 가능
                    color = MaterialTheme.colorScheme.primary // 브랜드 색상 적용
                )
            }
        }
    }
}