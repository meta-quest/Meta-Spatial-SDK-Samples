// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

object DisplayUtils {
  // Setting this value now, but in 0.4.0 this won't be necessary
  const val defaultDpi = 320

  /**
   * Utility function for calculating panel pixel values from dp and density
   * https://developer.android.com/training/multiscreen/screendensities
   */
  fun dpToPx(dp: Int): Int {
    return ((dp * defaultDpi).toFloat() / 160f).toInt()
  }

  @Composable fun Int.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

  @Composable fun Dp.toPx(): Float = with(LocalDensity.current) { this@toPx.toPx() }
}
