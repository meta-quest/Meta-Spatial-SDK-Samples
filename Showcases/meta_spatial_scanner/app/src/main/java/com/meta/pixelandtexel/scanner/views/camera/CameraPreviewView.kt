// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.camera

import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier,
    onSurfaceAvailable: (Surface) -> Unit,
    onSurfaceDestroyed: (Surface) -> Unit
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val context = LocalContext.current

  val surfaceView = remember { SurfaceView(context) }

  val surfaceHolderCallback = remember {
    object : SurfaceHolder.Callback {
      private var currentSurface: Surface? = null

      override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d("CameraPreviewView", "surfaceCreated")
        holder.surface?.let { surface ->
          currentSurface = surface
          onSurfaceAvailable(surface)
        }
      }

      override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.w("CameraPreviewView", "surfaceChanged: $format, ${width}x$height")
      }

      override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.w("CameraPreviewView", "surfaceDestroyed")
        currentSurface?.let { surface -> onSurfaceDestroyed(surface) }
        currentSurface = null
      }
    }
  }

  // add the SurfaceView holder callback using lifecycle awareness

  DisposableEffect(key1 = lifecycleOwner, key2 = surfaceView) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        Log.w("CameraPreviewView", "lifecycle event on_resume")
        surfaceView.holder.addCallback(surfaceHolderCallback)
      } else if (event == Lifecycle.Event.ON_PAUSE) {
        Log.w("CameraPreviewView", "lifecycle event on_pause")
        surfaceView.holder.removeCallback(surfaceHolderCallback)
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)

    // add the callback immediately in case we are already resumed
    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
      Log.w("CameraPreviewView", "lifecycle state is at least resumed; adding")
      surfaceView.holder.addCallback(surfaceHolderCallback)
    }

    object : DisposableEffectResult {
      override fun dispose() {
        Log.w("CameraPreviewView", "disposing effect")
        // clean up when the composable leaves the composition
        surfaceView.holder.removeCallback(surfaceHolderCallback)
        lifecycleOwner.lifecycle.removeObserver(observer)
      }
    }
  }

  AndroidView(factory = { surfaceView }, modifier = modifier)
}
