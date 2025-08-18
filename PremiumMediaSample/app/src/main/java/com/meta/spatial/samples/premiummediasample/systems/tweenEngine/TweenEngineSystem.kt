/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Color4
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector4
import com.meta.spatial.samples.premiummediasample.immersive.TweenLightingPassthrough
import com.meta.spatial.samples.premiummediasample.immersive.TweenLightingPassthroughAccessor
import com.meta.spatial.samples.premiummediasample.systems.heroLighting.TweenFarPlane
import com.meta.spatial.samples.premiummediasample.systems.heroLighting.TweenFarPlaneAccessor
import dorkbox.tweenEngine.Tween
import dorkbox.tweenEngine.TweenEngine

class TweenEngineSystem : SystemBase() {
  private var lastTime = System.currentTimeMillis()

  val tweenEngine: TweenEngine

  init {
    val tweenBuilder = TweenEngine.create()
    tweenBuilder.setCombinedAttributesLimit(7)
    tweenBuilder.registerAccessor(TweenFloat::class.java, TweenFloatAccessor())
    tweenBuilder.registerAccessor(Vector3::class.java, Vector3Accessor())
    tweenBuilder.registerAccessor(Vector4::class.java, Vector4Accessor())
    tweenBuilder.registerAccessor(TweenTransform::class.java, TweenTransformAccessor())
    tweenBuilder.registerAccessor(TweenScale::class.java, TweenScaleAccessor())
    tweenBuilder.registerAccessor(TweenMaterial::class.java, TweenMaterialAccessor())
    tweenBuilder.registerAccessor(TweenSceneMaterial::class.java, TweenSceneMaterialAccessor())

    tweenBuilder.registerAccessor(TweenFarPlane::class.java, TweenFarPlaneAccessor())
    tweenBuilder.registerAccessor(TweenPanelLayerAlpha::class.java, TweenPanelLayerAlphaAccessor())

    tweenBuilder.registerAccessor(
        TweenLightingPassthrough::class.java,
        TweenLightingPassthroughAccessor(),
    )

    tweenEngine = tweenBuilder.build()
  }

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    val diff = (currentTime - lastTime).toDouble().toFloat() / 1000f
    lastTime = currentTime

    tweenEngine.update(diff)
  }

  override fun destroy() {
    tweenEngine.cancelAll()
    super.destroy()
  }
}

fun <T> Tween<T>.value(targetValue: Vector3): Tween<T> {
  return this.value(targetValue.x, targetValue.y, targetValue.z)
}

fun <T> Tween<T>.value(targetValue: Quaternion): Tween<T> {
  return this.value(targetValue.w, targetValue.x, targetValue.y, targetValue.z)
}

fun <T> Tween<T>.value(targetValue: Vector4): Tween<T> {
  return this.value(targetValue.w, targetValue.x, targetValue.y, targetValue.z)
}

fun <T> Tween<T>.value(targetValue: Pose): Tween<T> {
  return this.value(
      targetValue.t.x,
      targetValue.t.y,
      targetValue.t.z,
      targetValue.q.w,
      targetValue.q.x,
      targetValue.q.y,
      targetValue.q.z,
  )
}

fun <T> Tween<T>.value(color4: Color4): Tween<T> {
  return this.value(color4.red, color4.green, color4.blue, color4.alpha)
}
