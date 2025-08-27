// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.views.android

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup

/**
 * An Android ViewGroup implementing the [ISurfaceProvider] pattern which adds a Surface to itself,
 * and can be added to a panel view and used to preview the camera feed.
 */
class CameraPreview : ViewGroup, ISurfaceProvider {
  companion object {
    private const val TAG = "Camera"
  }

  private val surfaceView = SurfaceView(context)

  private var _surface: Surface? = null
  override val surface: Surface?
    get() = _surface

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  init {
    surfaceView.holder.addCallback(
        object : SurfaceHolder.Callback {
          override fun surfaceCreated(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceCreated")
            _surface = holder.surface
          }

          override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.w(TAG, "surfaceChanged: $format, ${width}x$height")
          }

          override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.w(TAG, "surfaceDestroyed")
            _surface?.release()
            _surface = null
          }
        }
    )

    addView(surfaceView)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
    setMeasuredDimension(width, height)

    measureChild(surfaceView, widthMeasureSpec, heightMeasureSpec)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    surfaceView.layout(left, top, right, bottom)
  }
}
