package com.ssafy.yeobaeg.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 로고
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "여백 로고",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 환영 메시지
            Text(
                text = "다시 만나서 반가워요!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 이메일 입력 필드
            YeobaegTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = "이메일",
                placeholder = "이메일을 입력하세요",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 비밀번호 입력 필드
            YeobaegTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = "비밀번호",
                placeholder = "비밀번호를 입력하세요",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 로그인 버튼
            YeobaegButton(
                text = "로그인",
                onClick = viewModel::login,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 테스트용 구분선
            Text(
                text = "━━━━━━ 테스트 로그인 ━━━━━━",
                color = YeobaegTextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 테스트 버튼 3개
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 일반 사용자 버튼
                YeobaegButton(
                    text = "일반",
                    onClick = {
                        viewModel.loginWithTestUser(
                            email = "user1@example.com",
                            password = "Password123!"
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 작가 버튼
                YeobaegButton(
                    text = "작가",
                    onClick = {
                        viewModel.loginWithTestUser(
                            email = "user2@example.com",
                            password = "Password123!"
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 카페 사장 버튼
                YeobaegButton(
                    text = "사장",
                    onClick = {
                        viewModel.loginWithTestUser(
                            email = "user3@example.com",
                            password = "Password123!"
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 회원가입 링크
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = YeobaegTextGray)) {
                    append("계정이 없으신가요? ")
                }
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "SIGNUP",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = YeobaegDarkBrown,
                                fontWeight = FontWeight.Bold
                            )
                        ),
                        linkInteractionListener = {
                            viewModel.navigateSignUp()
                        }
                    )
                ) {
                    append("회원가입")
                }
            }

            Text(
                text = annotatedString,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // 로딩 인디케이터
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YeobaegDarkBrown)
            }
        }

        // 에러 다이얼로그
        if (uiState.error != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("오류") },
                text = { Text(uiState.error ?: "") },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("확인")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    YeobaegProjectTheme {
        LoginScreenContent()
    }
}

@Composable
private fun LoginScreenContent() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // 로고
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "여백 로고",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 환영 메시지
        Text(
            text = "다시 만나서 반가워요!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 이메일 입력 필드
        YeobaegTextField(
            value = email,
            onValueChange = { email = it },
            label = "이메일",
            placeholder = "이메일을 입력하세요",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 비밀번호 입력 필드
        YeobaegTextField(
            value = password,
            onValueChange = { password = it },
            label = "비밀번호",
            placeholder = "비밀번호를 입력하세요",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 로그인 버튼
        YeobaegButton(
            text = "로그인",
            onClick = { }
        )

        Spacer(modifier = Modifier.weight(1f))

        // 회원가입 링크
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = YeobaegTextGray)) {
                append("계정이 없으신가요? ")
            }
            withLink(
                LinkAnnotation.Clickable(
                    tag = "SIGNUP",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = YeobaegDarkBrown,
                            fontWeight = FontWeight.Bold
                        )
                    ),
                    linkInteractionListener = { }
                )
            ) {
                append("회원가입")
            }
        }

        Text(
            text = annotatedString,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

