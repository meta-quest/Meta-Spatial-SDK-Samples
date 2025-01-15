package com.meta.levinriegner.mediaview.app.player.view.edit

sealed class CropState {
  data object NotSupported : CropState()
  data object Disabled : CropState()
  data object Enabled : CropState()
}
