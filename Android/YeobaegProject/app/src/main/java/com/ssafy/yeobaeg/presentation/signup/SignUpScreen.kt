package com.ssafy.yeobaeg.presentation.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.ArtType
import com.ssafy.yeobaeg.domain.model.Gender
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 프로그래스 바
        LinearProgressIndicator(
            progress = { uiState.currentStep.toFloat() / uiState.totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = YeobaegDarkBrown,
            trackColor = Color.White,
        )

        when (uiState.currentStep) {
            1 -> Step1EmailPassword(
                email = uiState.email,
                password = uiState.password,
                passwordConfirm = uiState.passwordConfirm,
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,
                onPasswordConfirmChange = viewModel::updatePasswordConfirm,
                onNextClick = viewModel::nextStep,
                onBackClick = onBackClick,
                canProceed = viewModel.canProceedFromEmailPasswordStep()
            )
            2 -> Step2RoleSelection(
                selectedRole = uiState.userRole,
                onRoleSelect = viewModel::selectUserRole,
                onNextClick = viewModel::nextStep,
                onBackClick = viewModel::previousStep,
                canProceed = viewModel.canProceedFromRoleSelectionStep()
            )
            3 -> Step3GenderAndAge(
                birthDate = uiState.birthDate,
                selectedGender = uiState.gender,
                onBirthDateChange = viewModel::updateBirthDate,
                onGenderSelect = viewModel::selectGender,
                onNextClick = viewModel::nextStep,
                onBackClick = viewModel::previousStep,
                canProceed = viewModel.canProceedFromGenderAndAgeStep()
            )
            4 -> Step4Nickname(
                nickname = uiState.nickname,
                userRole = uiState.userRole,
                onNicknameChange = viewModel::updateNickname,
                onNextClick = {
                    if (uiState.userRole == UserRole.ARTIST) {
                        viewModel.nextStep()
                    } else {
                        onSignUpSuccess()
                    }
                },
                onBackClick = viewModel::previousStep,
                canProceed = viewModel.canProceedFromNicknameStep()
            )
            5 -> Step5ArtType(
                selectedArtType = uiState.artType,
                onArtTypeSelect = viewModel::selectArtType,
                onNextClick = onSignUpSuccess,
                onBackClick = viewModel::previousStep,
                canProceed = viewModel.canProceedFromArtTypeStep()
            )
        }
    }
}

@Composable
private fun Step1EmailPassword(
    email: String,
    password: String,
    passwordConfirm: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    canProceed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "회원가입",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "이메일",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("coffee@gmail.com", color = YeobaegTextGray.copy(0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = YeobaegDarkBrown,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "비밀번호",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("coffee", color = YeobaegTextGray.copy(0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = YeobaegDarkBrown,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "비밀번호 확인",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            placeholder = { Text("비밀번호를 다시 입력하세요", color = YeobaegTextGray.copy(0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = YeobaegDarkBrown,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YeobaegDarkBrown,
                disabledContainerColor = YeobaegTextGray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun Step2RoleSelection(
    selectedRole: UserRole?,
    onRoleSelect: (UserRole) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    canProceed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "어떤 역할로 시작하시겠어요?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "역할에 따라 제공되는 서비스가 달라집니다.",
            fontSize = 14.sp,
            color = YeobaegTextGray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 일반 사용자
        RoleSelectionItem(
            role = UserRole.GENERAL,
            description = "카페를 탐색하고 작품을 작품을 감상할 수 있어요.",
            iconBackgroundColor = Color(0xFFB8E6D5),
            iconColor = Color(0xFF2E7D5E),
            isSelected = selectedRole == UserRole.GENERAL,
            onSelect = { onRoleSelect(UserRole.GENERAL) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 신진 작가
        RoleSelectionItem(
            role = UserRole.ARTIST,
            description = "작품을 전시하고 카페에 전시할 수 있어요",
            iconBackgroundColor = Color(0xFFE5D4F7),
            iconColor = Color(0xFF8B5FBF),
            isSelected = selectedRole == UserRole.ARTIST,
            onSelect = { onRoleSelect(UserRole.ARTIST) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 카페 사장
        RoleSelectionItem(
            role = UserRole.CAFE_OWNER,
            description = "전시 공간을 제공하고 관리할 수 있어요.",
            iconBackgroundColor = Color(0xFFD4E8F7),
            iconColor = Color(0xFF4A90C9),
            isSelected = selectedRole == UserRole.CAFE_OWNER,
            onSelect = { onRoleSelect(UserRole.CAFE_OWNER) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YeobaegDarkBrown,
                disabledContainerColor = YeobaegTextGray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RoleSelectionItem(
    role: UserRole,
    description: String,
    iconBackgroundColor: Color,
    iconColor: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material.icons.Icons.Default.Person.let { icon ->
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 텍스트
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = role.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = YeobaegTextGray,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 라디오 버튼
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isSelected) YeobaegDarkBrown else Color.LightGray,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .background(if (isSelected) YeobaegDarkBrown else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}

@Composable
private fun Step3GenderAndAge(
    birthDate: String,
    selectedGender: Gender?,
    onBirthDateChange: (String) -> Unit,
    onGenderSelect: (Gender) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    canProceed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "성별과 나이를 입력해 주세요",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "생년월일",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = birthDate,
            onValueChange = onBirthDateChange,
            placeholder = { Text("2025.01.01", color = YeobaegTextGray.copy(0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = YeobaegDarkBrown,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "성별",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenderButton(
                gender = Gender.MALE,
                isSelected = selectedGender == Gender.MALE,
                onSelect = { onGenderSelect(Gender.MALE) },
                modifier = Modifier.weight(1f)
            )
            GenderButton(
                gender = Gender.FEMALE,
                isSelected = selectedGender == Gender.FEMALE,
                onSelect = { onGenderSelect(Gender.FEMALE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YeobaegDarkBrown,
                disabledContainerColor = YeobaegTextGray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun GenderButton(
    gender: Gender,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) YeobaegDarkBrown.copy(alpha = 0.1f) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) YeobaegDarkBrown else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = androidx.compose.ui.Modifier.size(40.dp),
                tint = if (isSelected) YeobaegDarkBrown else YeobaegTextGray
            )
            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
            Text(
                text = gender.displayName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) YeobaegDarkBrown else Color.Black
            )
        }
    }
}

@Composable
private fun Step4Nickname(
    nickname: String,
    userRole: UserRole?,
    onNicknameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    canProceed: Boolean
) {
    val label = when (userRole) {
        UserRole.ARTIST -> "작가명"
        UserRole.CAFE_OWNER -> "카페명"
        else -> "닉네임"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "${label}을 입력해 주세요",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            placeholder = { Text("커피왕자", color = YeobaegTextGray.copy(0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = YeobaegDarkBrown,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YeobaegDarkBrown,
                disabledContainerColor = YeobaegTextGray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun Step5ArtType(
    selectedArtType: ArtType?,
    onArtTypeSelect: (ArtType) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    canProceed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "주로 어떤 작품을 그리시나요?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "카페 매장에 도움이 돼요.",
            fontSize = 14.sp,
            color = YeobaegTextGray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArtTypeCard(
                artType = ArtType.LANDSCAPE,
                isSelected = selectedArtType == ArtType.LANDSCAPE,
                onSelect = { onArtTypeSelect(ArtType.LANDSCAPE) },
                modifier = Modifier.weight(1f)
            )
            ArtTypeCard(
                artType = ArtType.PORTRAIT,
                isSelected = selectedArtType == ArtType.PORTRAIT,
                onSelect = { onArtTypeSelect(ArtType.PORTRAIT) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArtTypeCard(
                artType = ArtType.STILL_LIFE,
                isSelected = selectedArtType == ArtType.STILL_LIFE,
                onSelect = { onArtTypeSelect(ArtType.STILL_LIFE) },
                modifier = Modifier.weight(1f)
            )
            ArtTypeCard(
                artType = ArtType.HISTORY_CREATIVE,
                isSelected = selectedArtType == ArtType.HISTORY_CREATIVE,
                onSelect = { onArtTypeSelect(ArtType.HISTORY_CREATIVE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YeobaegDarkBrown,
                disabledContainerColor = YeobaegTextGray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ArtTypeCard(
    artType: ArtType,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) YeobaegDarkBrown.copy(alpha = 0.1f) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) YeobaegDarkBrown else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = artType.displayName,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) YeobaegDarkBrown else Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep1Preview() {
    YeobaegProjectTheme {
        Step1EmailPassword(
            email = "",
            password = "",
            passwordConfirm = "",
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordConfirmChange = {},
            onNextClick = {},
            onBackClick = {},
            canProceed = false
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep2Preview() {
    YeobaegProjectTheme {
        Step2RoleSelection(
            selectedRole = UserRole.CAFE_OWNER,
            onRoleSelect = {},
            onNextClick = {},
            onBackClick = {},
            canProceed = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep3Preview() {
    YeobaegProjectTheme {
        Step3GenderAndAge(
            birthDate = "2025.01.01",
            selectedGender = Gender.MALE,
            onBirthDateChange = {},
            onGenderSelect = {},
            onNextClick = {},
            onBackClick = {},
            canProceed = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep4ArtistPreview() {
    YeobaegProjectTheme {
        Step4Nickname(
            nickname = "커피왕자",
            userRole = UserRole.ARTIST,
            onNicknameChange = {},
            onNextClick = {},
            onBackClick = {},
            canProceed = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep4CafeOwnerPreview() {
    YeobaegProjectTheme {
        Step4Nickname(
            nickname = "커피왕자",
            userRole = UserRole.CAFE_OWNER,
            onNicknameChange = {},
            onNextClick = {},
            onBackClick = {},
            canProceed = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep5Preview() {
    YeobaegProjectTheme {
        Step5ArtType(
            selectedArtType = ArtType.LANDSCAPE,
            onArtTypeSelect = {},
            onNextClick = {},
            onBackClick = {},
            canProceed = true
        )
    }
}

