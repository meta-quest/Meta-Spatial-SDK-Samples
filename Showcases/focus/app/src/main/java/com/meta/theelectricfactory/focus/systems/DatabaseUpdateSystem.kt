// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.systems

import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Transform
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.UniqueAssetComponent

// Custom system created to update the poses of the objects that had been moved in the database
class DatabaseUpdateSystem : SystemBase() {

    private var lastTime = System.currentTimeMillis()

    override fun execute() {

        if (ImmersiveActivity.instance.get()?.appStarted == false) return

        val currentTime = System.currentTimeMillis()

        // if there is no current project, we don't update database
        if (ImmersiveActivity.instance.get()?.currentProject == null) {
            lastTime = currentTime
        }

        // Check if objects are being moved and save new position
        // We do this each 0.2 seconds to improve performance
        val deltaTime = (currentTime - lastTime) / 1000f
        if (deltaTime > 0.2) {
            lastTime = currentTime

            // Update pose of tool assets
            val tools = Query.where { has(ToolComponent.id) }
            for (entity in tools.eval()) {
                val asset = entity.getComponent<ToolComponent>()
                val pose = entity.getComponent<Transform>().transform
                val isGrabbed = entity.getComponent<Grabbable>().isGrabbed

                if (isGrabbed) {
                    if (asset.type != AssetType.TIMER)
                        ImmersiveActivity.instance.get()?.DB?.updateAssetPose(asset.uuid, asset.type, pose)
                }
            }

            // Update pose of unique assets
            val uniqueAssets = Query.where { has(UniqueAssetComponent.id) }
            for (entity in uniqueAssets.eval()) {
                val uniqueAsset = entity.getComponent<UniqueAssetComponent>()
                val pose = entity.getComponent<Transform>().transform
                val isGrabbed = entity.getComponent<Grabbable>().isGrabbed

                if (isGrabbed)
                    ImmersiveActivity.instance.get()?.DB?.updateUniqueAsset(uniqueAsset.uuid, pose)
            }
        }
    }
}
