package com.example.madcamp_1.ui.screen.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    confirmPassword: String,
    isAgreed: Boolean,
    errorEvent: SharedFlow<String>,
    onSchoolChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAgreementChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }
    val schools = listOf("POSTECH", "KAIST")

    // ✅ 학교별 브랜드 컬러 정의 (로그인 화면과 동일한 로직)
    val isPostech = school.contains("POSTECH", ignoreCase = true)
    val isKaist = school.contains("KAIST", ignoreCase = true)

    val brandColor = when {
        isPostech -> Color(0xFFE0224E)
        isKaist -> Color(0xFF005EB8)
        else -> MaterialTheme.colorScheme.primary // 선택 전 기본 색상
    }

    // 약관 다이얼로그 상태
    var showTermsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(errorEvent) {
        errorEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    // ✅ Box를 사용해 배경 이미지와 컨텐츠를 겹침
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 배경 이미지 (poca.png)
        Image(
            painter = painterResource(id = R.drawable.poca),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            alpha = 0.15f // 투명도 설정
        )

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
            },
            containerColor = Color.Transparent // ✅ 이미지가 비치도록 투명하게 설정
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "회원가입",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = UnivsFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (school.isNotBlank()) brandColor else Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 학교 선택
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
                            if (school.isNotBlank()) {
                                Image(painter = painterResource(id = if (isPostech) R.drawable.postech else R.drawable.kaist), contentDescription = null, modifier = Modifier.size(24.dp))
                            }
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = brandColor,
                            focusedLabelColor = brandColor
                        )
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        schools.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(painter = painterResource(id = if (selectionOption == "POSTECH") R.drawable.postech else R.drawable.kaist), contentDescription = null, modifier = Modifier.size(30.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(selectionOption, fontFamily = UnivsFontFamily)
                                    }
                                },
                                onClick = { onSchoolChange(selectionOption); expanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("이메일", fontFamily = UnivsFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brandColor, focusedLabelColor = brandColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("아이디", fontFamily = UnivsFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brandColor, focusedLabelColor = brandColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("비밀번호 (6자 이상)", fontFamily = UnivsFontFamily) },
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Default,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brandColor, focusedLabelColor = brandColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 확인
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("비밀번호 확인", fontFamily = UnivsFontFamily) },
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Default,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brandColor, focusedLabelColor = brandColor)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 이용약관 동의
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAgreed,
                        onCheckedChange = onAgreementChange,
                        colors = CheckboxDefaults.colors(checkedColor = brandColor) // ✅ 체크박스 색상
                    )
                    Text(
                        text = "이용약관 및 개인정보 동의",
                        fontFamily = UnivsFontFamily,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { showTermsDialog = true },
                        textDecoration = TextDecoration.Underline,
                        color = brandColor // ✅ 텍스트 색상
                    )
                    Text(text = " (필수)", fontFamily = UnivsFontFamily, fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandColor) // ✅ 버튼 색상
                ) {
                    Text("회원가입", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("이미 계정이 있나요? ", fontFamily = UnivsFontFamily, fontSize = 14.sp, color = Color.Gray)
                    TextButton(onClick = onBackClick) {
                        Text("로그인", fontFamily = UnivsFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = brandColor)
                    }
                }
            }
        }

        // 약관 다이얼로그
        if (showTermsDialog) {
            TermsDialog(brandColor = brandColor, onDismiss = { showTermsDialog = false })
        }
    }
}

@Composable
fun TermsDialog(brandColor: Color, onDismiss: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("이용약관", "개인정보방침")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.7f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 10.dp), contentAlignment = Alignment.Center) {
                    Text("서비스 정책 안내", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = brandColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = brandColor // ✅ 탭 밑줄 색상
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontFamily = UnivsFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f).padding(20.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        text = if (selectedTab == 0) getServiceTerms() else getPrivacyPolicy(),
                        fontFamily = UnivsFontFamily, fontSize = 13.sp, lineHeight = 20.sp, color = Color(0xFF444444)
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(20.dp).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandColor) // ✅ 확인 버튼 색상
                ) {
                    Text("확인", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ... getServiceTerms() 및 getPrivacyPolicy() 함수는 이전과 동일

private fun getServiceTerms() = """
    [제 1 장 총칙]
    제 1 조 (목적)
    본 약관은 MADCAMP 1팀(이하 '회사')이 운영하는 대학 커뮤니티 플랫폼의 이용과 관련하여 회사와 회원 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.
    
    제 2 조 (용어의 정의)
    1. '서비스'라 함은 대학생 간 정보 공유 및 소통을 위해 제공되는 모바일 어플리케이션을 의미합니다.
    2. '회원'이라 함은 학교 인증을 거쳐 가입한 이용자를 말합니다.
    
    [제 2 장 서비스 이용]
    제 3 조 (이용계약 체결)
    회원이 되고자 하는 자는 본 약관에 동의하고 소속 대학 이메일을 통해 본인 인증을 완료해야 합니다.
    
    제 4 조 (게시물 관리)
    타인의 명예를 훼손하거나 불법 정보를 공유하는 경우, 사전 통보 없이 게시물이 삭제될 수 있으며 이용 정지 처분을 받을 수 있습니다.
""".trimIndent()

private fun getPrivacyPolicy() = """
    [개인정보 처리방침]
    
    1. 수집하는 개인정보 항목
    회사는 회원가입 시 아래 정보를 필수적으로 수집합니다.
    - 소속 대학, 이메일 주소, 아이디(닉네임), 비밀번호
    
    2. 개인정보 수집 목적
    - 대학별 커뮤니티 권한 부여
    - 회원 식별 및 가입 의사 확인
    - 부정 이용 방지 및 안정적인 서비스 운영
    
    3. 개인정보의 보유 기간
    회원 탈퇴 시 수집된 개인정보는 지체 없이 파기하는 것을 원칙으로 합니다.
    
    4. 동의 거부권
    귀하는 개인정보 수집에 동의하지 않을 권리가 있으나, 거부 시 회원가입 및 서비스 이용이 불가능합니다.
""".trimIndent()