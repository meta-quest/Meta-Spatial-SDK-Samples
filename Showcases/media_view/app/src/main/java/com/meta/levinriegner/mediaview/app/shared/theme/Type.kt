// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography =
    Typography(
        // H1
        titleLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 28.sp * 0.90,
            ),
        // H3
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 19.4.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
            ),
        // Roboto Text
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.4.sp,
                lineHeight = 16.sp,
            ),
    )
