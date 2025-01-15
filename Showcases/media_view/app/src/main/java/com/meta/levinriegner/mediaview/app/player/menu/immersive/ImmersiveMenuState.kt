// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.menu.immersive

sealed class ImmersiveMenuState {
  data class Initial(val canEdit: Boolean) : ImmersiveMenuState()
  data class Editing(val saveLoading: Boolean) : ImmersiveMenuState()
}
