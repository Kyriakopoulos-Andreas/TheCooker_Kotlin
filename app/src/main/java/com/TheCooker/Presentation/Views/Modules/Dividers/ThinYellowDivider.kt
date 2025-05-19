package com.TheCooker.Presentation.Views.Modules.Dividers

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("ResourceAsColor")
@Composable
fun ThinYellowDivider() {
    val DividerAlpha = 0.5f
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = DividerAlpha)),
        color = Color(0xFFFFC107),
        thickness = 1.dp
    )
}