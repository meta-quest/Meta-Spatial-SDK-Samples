// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.utils

import com.meta.spatial.runtime.PanelConfigOptions

/** Helper function to copy values from one PanelConfigOptions object to another */
fun PanelConfigOptions.copyTo(other: PanelConfigOptions) {
  other.layoutHeightInPx = this.layoutHeightInPx
  other.layoutWidthInPx = this.layoutWidthInPx
  other.layoutHeightInDp = this.layoutHeightInDp
  other.layoutWidthInDp = this.layoutWidthInDp
  other.layoutDpi = this.layoutDpi
  other.height = this.height
  other.width = this.width
  other.radiusForCylinderOrSphere = this.radiusForCylinderOrSphere
  other.pivotOffsetHeight = this.pivotOffsetHeight
  other.pivotOffsetWidth = this.pivotOffsetWidth
  other.fractionOfScreen = this.fractionOfScreen
  other.mips = this.mips
  other.samplerConfig = this.samplerConfig
  other.unlit = this.unlit
  other.includeGlass = this.includeGlass
  other.stereoMode = this.stereoMode
  other.alphaMode = this.alphaMode
  other.forceSceneTexture = this.forceSceneTexture
  other.clickButtons = this.clickButtons
  other.panelShader = this.panelShader
  other.effectShader = this.effectShader
  other.sceneMeshCreator = this.sceneMeshCreator
  other.layerConfig = this.layerConfig
  other.panelShapeType = this.panelShapeType
  other.enableTransparent = this.enableTransparent
  other.themeResourceId = this.themeResourceId
}
