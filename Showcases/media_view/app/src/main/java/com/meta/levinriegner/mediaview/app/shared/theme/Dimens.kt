// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.theme

import androidx.compose.ui.unit.dp

class Dimens {
  companion object {
    val xSmall = 8.dp
    val small = 12.dp
    val medium = 16.dp
    val large = 24.dp
    val xLarge = 32.dp
    val xxLarge = 48.dp
    val xxxLarge = 64.dp

    val radiusSmall = 5.dp
    val radiusMedium = 8.dp
    val radiusLarge = 16.dp

    // View-specific dimensions
    const val playerMenuButtonSize = 54
    const val playerMenuTotalWidth = 204
    const val playerMenuTotalHeight = 172 + playerMenuButtonSize + 16

    val galleryItemSize = 120.dp
  }
}
