package com.ssafy.yeobaeg.presentation.common.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        role = role,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}