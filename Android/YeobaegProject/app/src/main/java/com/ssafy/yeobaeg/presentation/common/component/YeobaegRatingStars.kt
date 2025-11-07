package com.ssafy.yeobaeg.presentation.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

/**
 * 별점을 표시하는 공통 컴포넌트
 *
 * @param rating 0.0 ~ 5.0 사이의 별점 (Int 또는 Double 모두 지원)
 * @param starSize 별 크기 (dp)
 * @param tint 별 색상 (기본값: 노란색)
 *
 * 사용 예시:
 * ```
 * YeobaegRatingStars(rating = 4.8)  // ★★★★⯨
 * YeobaegRatingStars(rating = 5)    // ★★★★★
 * YeobaegRatingStars(rating = 3.2)  // ★★★☆☆
 * ```
 */
@Composable
fun YeobaegRatingStars(
    rating: Number,
    modifier: Modifier = Modifier,
    starSize: Int = 20,
    tint: Color = Color(0xFFFFC107)
) {
    val ratingDouble = rating.toDouble().coerceIn(0.0, 5.0)
    val fullStars = ratingDouble.toInt()
    val hasHalfStar = ratingDouble - fullStars >= 0.5
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 채워진 별
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(starSize.dp)
            )
        }

        // 반별 (0.5 이상일 때)
        if (hasHalfStar) {
            Box(modifier = Modifier.size(starSize.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            clipRect(right = size.width * 0.5f) {
                                this@drawWithContent.drawContent()
                            }
                        }
                )
            }
        }

        // 빈 별
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(starSize.dp)
            )
        }
    }
}
