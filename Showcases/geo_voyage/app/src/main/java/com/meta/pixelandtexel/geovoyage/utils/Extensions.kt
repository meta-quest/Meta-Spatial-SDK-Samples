// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.utils

import com.meta.spatial.runtime.PanelConfigOptions

/** Helper function to copy values from one PanelConfigOptions object to another */
fun PanelConfigOptions.copyTo(other: PanelConfigOptions) {
  other.alphaMode = this.alphaMode
  other.clickButtons = this.clickButtons
  other.effectShader = this.effectShader
  other.enableLayer = this.enableLayer
  other.enableTransparent = this.enableTransparent
  other.forceSceneTexture = this.forceSceneTexture
  other.fractionOfScreen = this.fractionOfScreen
  other.height = this.height
  other.includeGlass = this.includeGlass
  other.layerConfig = this.layerConfig
  other.layoutDpi = this.layoutDpi
  other.layoutHeightInDp = this.layoutHeightInDp
  other.layoutHeightInPx = this.layoutHeightInPx
  other.layoutWidthInDp = this.layoutWidthInDp
  other.layoutWidthInPx = this.layoutWidthInPx
  other.mips = this.mips
  other.panelShader = this.panelShader
  other.panelShapeType = this.panelShapeType
  other.pivotOffsetHeight = this.pivotOffsetHeight
  other.pivotOffsetWidth = this.pivotOffsetWidth
  other.radiusForCylinderOrSphere = this.radiusForCylinderOrSphere
  other.samplerConfig = this.samplerConfig
  other.sceneMeshCreator = this.sceneMeshCreator
  other.stereoMode = this.stereoMode
  other.themeResourceId = this.themeResourceId
  other.unlit = this.unlit
  other.width = this.width
}
