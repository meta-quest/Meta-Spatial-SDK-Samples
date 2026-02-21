package com.meta.pixelandtexel.scanner.utils.mytheme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp


object AppTextStyles {
    val Title = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
    )
    val Subtitle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    )


    val Body = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )
    val Url = TextStyle(
        fontSize = 14.sp,
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    )
}