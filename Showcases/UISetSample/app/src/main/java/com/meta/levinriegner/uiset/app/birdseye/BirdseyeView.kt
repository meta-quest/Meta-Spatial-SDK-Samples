// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.birdseye

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
