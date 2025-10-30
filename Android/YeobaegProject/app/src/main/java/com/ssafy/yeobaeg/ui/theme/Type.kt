package com.ssafy.yeobaeg.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.R

// Set of Material typography styles to start with

val jansilFont = FontFamily(
    Font(
        R.font.the_jamsil_thin, FontWeight.Thin
    ),
    Font(R.font.the_jamsil_bold, FontWeight.Bold),
    Font(R.font.the_jamsil_light, FontWeight.Light),
    Font(R.font.the_jamsil_medium, FontWeight.Medium),
    Font(R.font.the_jamsil_regular, FontWeight.Normal),
    Font(R.font.the_jamsil_extrabold, FontWeight.ExtraBold)

)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily =jansilFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)