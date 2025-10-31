package com.ssafy.yeobaeg.presentation.profile.cafe

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.CafeInfo
import com.ssafy.yeobaeg.domain.model.UserInfo
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun CafeOwnerProfileScreen(
    viewModel: CafeOwnerProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.errorMessage != null -> {
                ErrorContent(
                    message = uiState.errorMessage!!,
                    onRetry = viewModel::refresh,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        CafeOwnerProfileContent(
            userInfo = uiState.userInfo,
            cafeInfo = uiState.cafeInfo,
            onEditClick = viewModel::navigateToEditProfile
        )
    }
}

@Composable
fun CafeOwnerProfileContent(
    userInfo: UserInfo,
    cafeInfo: CafeInfo,
    onEditClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(25.dp)
    ) {
        // 프로필 부분
        ProfileSection(
            userInfo = userInfo,
            onEditClick = onEditClick
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 매장 정보 부분
        StoreInfoSection(cafeInfo = cafeInfo)

        Spacer(modifier = Modifier.height(32.dp))

        // 설정 메뉴 부분
        MenuListSection()

        Spacer(modifier = Modifier.weight(1f))

        // 로그아웃 버튼
        LogoutButton()
    }
}

@Composable
fun ProfileSection(
    userInfo: UserInfo,
    onEditClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        if (userInfo.profileImageUrl != null) {
            AsyncImage(
                model = userInfo.profileImageUrl,
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "프로필 이미지",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Name and Email
        Column {
            Text(
                text = userInfo.nickname,
                fontSize = 22.sp,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Bold,
                color = YeobaegBlack
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = userInfo.email,
                fontSize = 14.sp,
                color = YeobaegTextGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Edit Profile Button
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "프로필 편집",
                    fontSize = 13.sp,
                    color = YeobaegBlack
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StoreInfoSection(cafeInfo: CafeInfo) {
    Column {
        // Title
        Text(
            text = "매장 정보",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Store Details
        StoreDetailItem(
            title = cafeInfo.cafeName,
            subtitle = cafeInfo.address,
            icon = R.drawable.ic_storeedit
        )

        Spacer(modifier = Modifier.height(16.dp))

        StoreDetailItem(
            title = "운영시간",
            subtitle = cafeInfo.businessHours,
            icon = R.drawable.ic_time
        )

        Spacer(modifier = Modifier.height(16.dp))

        StoreDetailItem(
            title = "리뷰 ${cafeInfo.ratingStats.totalReviews}개",
            subtitle = "평균 평점: ${
                String.format(
                    "%.1f",
                    cafeInfo.ratingStats.averageRating
                )
            }",  // 실제 데이터 사용
            icon = R.drawable.ic_star
        )
    }
}

@Composable
fun MenuListSection() {
    Column {
        MenuListItem(
            title = "좋아요 한 목록",
            badge = "12",
            icon = R.drawable.ic_heart
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        MenuListItem(
            title = "매장 정보 수정",
            badge = null,
            icon = R.drawable.ic_storeedit
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        MenuListItem(
            title = "리뷰 관리",
            badge = "5",
            icon = R.drawable.ic_star
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        MenuListItem(
            title = "알림 설정",
            badge = null,
            icon = R.drawable.ic_time
        )
    }
}

@Composable
fun LogoutButton() {
    OutlinedButton(
        onClick = { /* Handle logout */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFFF6B6B)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0E0))
    ) {
        Text(
            text = "로그아웃",
            color = Color(0xFFFF6B6B),
            fontSize = 16.sp
        )
    }
}

@Composable
fun StoreDetailItem(
    title: String,
    subtitle: String,
    icon: Int
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "매장 수정",
                modifier = Modifier.size(20.dp),
                tint = YeobaegBrown
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = YeobaegBlack
            )
        }
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = YeobaegTextGray
        )
    }
}

@Composable
fun MenuListItem(
    title: String,
    badge: String?,
    icon: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "설정 아이콘",
            modifier = Modifier.size(20.dp),
            tint = YeobaegDarkGray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        if (badge != null) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 13.sp,
                    color = Color(0xFF754106),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Red,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onRetry) {
            Text("다시 시도")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CafeOwnerProfileScreenPreview() {
    CafeOwnerProfileScreen()
}


