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
import com.TheCooker.R

@SuppressLint("ResourceAsColor")
@Composable
fun BlackFatDivider() {
    val DividerAlpha = 0.5f // Αλλάξε την τιμή ανάλογα με τις προτιμήσεις σου
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = DividerAlpha)),
        color = Color(R.color.black), // Χρησιμοποιούμε background αντί για το color εδώ
        thickness = 4.dp
    )
}