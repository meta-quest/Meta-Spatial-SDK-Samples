/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.panelLayerAlpha

import com.meta.spatial.core.EntityContext
import com.meta.spatial.core.Query
import com.meta.spatial.core.SpatialSDKExperimentalAPI
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.SystemDependencies
import com.meta.spatial.core.SystemDependencyConfig
import com.meta.spatial.core.Vector4
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import com.meta.spatial.samples.premiummediasample.systems.tweenEngine.TweenEngineSystem
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.MeshCreationSystem
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.SpatialActivityManager

class PanelLayerAlphaSystem() : SystemBase() {

  private var lastUpdateVersion = 0UL

  @OptIn(SpatialSDKExperimentalAPI::class)
  override fun execute() {
    // Using changedSince api allows system to detect changes made by the TweenEngineSystem the
    // frame they happen.
    for (entity in Query.where { changedSince(PanelLayerAlpha.id, lastUpdateVersion) }.eval()) {
      SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { immersiveActivity ->
        immersiveActivity.systemManager
            .findSystem<SceneObjectSystem>()
            .getSceneObject(entity)
            ?.thenAccept { sceneObject ->
              val panelSceneObject = sceneObject as? PanelSceneObject
              val sceneObjectLayer = panelSceneObject?.getLayer()
              if (sceneObjectLayer != null) {
                val plaComp = entity.getComponent<PanelLayerAlpha>()
                // Assuming color scale bias is default other than alpha
                sceneObjectLayer.setColorScaleBias(
                    Vector4(1.0f, 1.0f, 1.0f, plaComp.layerAlpha),
                    Vector4(0.0f),
                )
              }
            }
      }
    }
    lastUpdateVersion = EntityContext.getDataModel()!!.getLastUpdateVersion()
  }

  override fun getDependencies(): SystemDependencies {
    return SystemDependencies(
        mustRunBefore =
            mutableSetOf(
                SystemDependencyConfig(MeshCreationSystem::class),
                SystemDependencyConfig(TweenEngineSystem::class),
            ),
    )
  }
}
