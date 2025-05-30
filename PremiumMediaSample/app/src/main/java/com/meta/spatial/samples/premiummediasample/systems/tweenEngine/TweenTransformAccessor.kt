// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import android.util.Log
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Transform
import dorkbox.tweenEngine.TweenAccessor
import kotlin.math.sqrt

class TweenTransformAccessor : TweenAccessor<TweenTransform> {
  override fun getValues(target: TweenTransform, tweenType: Int, returnValues: FloatArray): Int {
    val component = target.entity.getComponent<Transform>()
    return when (tweenType) {
      TweenTransform.POSITION_XYZ -> {
        returnValues[0] = component.transform.t.x
        returnValues[1] = component.transform.t.y
        returnValues[2] = component.transform.t.z
        3 // Return number of values
      }
      TweenTransform.ROTATION_WXYZ -> {
        returnValues[0] = component.transform.q.w
        returnValues[1] = component.transform.q.x
        returnValues[2] = component.transform.q.y
        returnValues[3] = component.transform.q.z
        4 // Return number of values
      }
      TweenTransform.ROTATION_XYZ -> {
        val euler = component.transform.q.toEuler()
        returnValues[0] = euler.x
        returnValues[1] = euler.y
        returnValues[2] = euler.z
        3 // Return number of values
      }
      TweenTransform.POSE_XYZ_XYZ -> {
        returnValues[0] = component.transform.t.x
        returnValues[1] = component.transform.t.y
        returnValues[2] = component.transform.t.z

        val euler = component.transform.q.toEuler()
        returnValues[3] = euler.x
        returnValues[4] = euler.y
        returnValues[5] = euler.z
        6 // Return number of values
      }
      TweenTransform.POSE_XYZ_WXYZ -> {
        returnValues[0] = component.transform.t.x
        returnValues[1] = component.transform.t.y
        returnValues[2] = component.transform.t.z

        val euler = component.transform.q.toEuler()
        returnValues[3] = component.transform.q.w
        returnValues[4] = component.transform.q.x
        returnValues[5] = component.transform.q.y
        returnValues[6] = component.transform.q.z
        7 // Return number of values
      }
      else -> 0
    }
  }

  override fun setValues(target: TweenTransform, tweenType: Int, newValues: FloatArray) {
    val component = target.entity.getComponent<Transform>()
    when (tweenType) {
      TweenTransform.POSITION_XYZ -> {
        component.transform.t.x = newValues[0]
        component.transform.t.y = newValues[1]
        component.transform.t.z = newValues[2]
      }
      TweenTransform.ROTATION_WXYZ -> {
        component.transform.q = Quaternion(newValues[0], newValues[1], newValues[2], newValues[3])
      }
      TweenTransform.ROTATION_XYZ -> {
        component.transform.q = Quaternion(newValues[0], newValues[1], newValues[2])
      }
      TweenTransform.POSE_XYZ_XYZ -> {
        component.transform.t.x = newValues[0]
        component.transform.t.y = newValues[1]
        component.transform.t.z = newValues[2]
        if (!isPoseValid(component.transform.t)) {
          Log.e("TweenEngine", "About to set invalid pose ${component.transform.t}")
        }

        component.transform.q = Quaternion(newValues[3], newValues[4], newValues[5])
        if (!isOrientationValid(component.transform.q)) {
          Log.e("TweenEngine", "About to set invalid orientation ${component.transform.q}")
        }
        // component.transform.q.normalize()
      }
      TweenTransform.POSE_XYZ_WXYZ -> {
        component.transform.t.x = newValues[0]
        component.transform.t.y = newValues[1]
        component.transform.t.z = newValues[2]
        if (!isPoseValid(component.transform.t)) {
          Log.e("TweenEngine", "About to set invalid pose ${component.transform.t}")
        }

        component.transform.q =
            Quaternion(newValues[3], newValues[4], newValues[5], newValues[6]).normalize()
        if (!isOrientationValid(component.transform.q)) {
          Log.e("TweenEngine", "About to set invalid orientation ${component.transform.q}")
        }
      }
    }
    target.entity.setComponent(component)
  }

  fun isOrientationValid(orientation: Quaternion): Boolean {
    // Check if each component is finite
    if (!orientation.x.isFinite() ||
        !orientation.y.isFinite() ||
        !orientation.z.isFinite() ||
        !orientation.w.isFinite()) {
      return false
    }

    // Check if the quaternion is normalized (magnitude close to 1)
    val magnitude =
        sqrt(
            orientation.x * orientation.x +
                orientation.y * orientation.y +
                orientation.z * orientation.z +
                orientation.w * orientation.w)
    return magnitude > 0.999 && magnitude < 1.001
  }

  fun isPoseValid(pos: Vector3): Boolean {
    return pos.x.isFinite() && pos.y.isFinite() && pos.z.isFinite()
  }
}
