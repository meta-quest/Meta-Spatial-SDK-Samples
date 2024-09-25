// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

class AppColor {
  companion object {
    val White = Color(0xFFDBE4EB)
    val White60 = Color(0x99DBE4EB)
    val White30 = Color(0x4DDBE4EB)
    val White15 = Color(0x26DBE4EB)
    val ButtonSelect = Color(0x20344ebd)
    val Black = Color(0xFF000000)
    val GradientStart = Color(0xFF0D1622)
    val GradientEnd = Color(0xFF142B4F)

    val GradientInEnvironmentStart = Color(0xB8484848)

    val MetaBlu = Color(0xFF1C65C1)

    val BackgroundSweep =
        Brush.verticalGradient(listOf(AppColor.GradientStart, AppColor.GradientEnd))
  }
}
