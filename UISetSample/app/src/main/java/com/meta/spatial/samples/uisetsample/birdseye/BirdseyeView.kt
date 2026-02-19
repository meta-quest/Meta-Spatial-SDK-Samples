/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BirdseyeView() {

  Row {
    BirdseyeTypography()
    BirdseyeIconLibrary()
    BirdseyeButtons()
    BirdseyeTextTileButton()
    BirdseyeButtonShelf()
    BirdseyeSlider()
    BirdseyeControl()
    BirdseyeDropdown()
    BirdseyeTooltip()
    BirdseyeSearchBar()
    BirdseyeTextField()
    BirdseyeDialog()
  }
}

@Preview(
    widthDp = 1000,
    heightDp = 2000,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeViewPreview() {
  BirdseyeView()
}
