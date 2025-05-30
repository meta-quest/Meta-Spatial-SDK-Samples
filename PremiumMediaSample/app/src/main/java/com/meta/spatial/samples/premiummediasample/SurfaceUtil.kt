// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample

import android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION
import android.opengl.EGL14.EGL_OPENGL_ES2_BIT
import android.opengl.GLES20
import android.view.Surface
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGL10.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

object SurfaceUtil {
  val config =
      intArrayOf(
          EGL_RENDERABLE_TYPE,
          EGL_OPENGL_ES2_BIT,
          EGL_RED_SIZE,
          8,
          EGL_GREEN_SIZE,
          8,
          EGL_BLUE_SIZE,
          8,
          EGL_ALPHA_SIZE,
          8,
          EGL_DEPTH_SIZE,
          0,
          EGL_STENCIL_SIZE,
          0,
          EGL_NONE)

  private fun chooseEglConfig(egl: EGL10, eglDisplay: EGLDisplay): EGLConfig {
    val configsCount = intArrayOf(0)
    val configs = arrayOfNulls<EGLConfig>(1)
    egl.eglChooseConfig(eglDisplay, config, configs, 1, configsCount)
    return configs[0]!!
  }

  // http://stackoverflow.com/a/21564236/2681195
  fun paintBlack(surface: Surface) {
    val egl = EGLContext.getEGL() as EGL10
    val eglDisplay = egl.eglGetDisplay(EGL_DEFAULT_DISPLAY)
    egl.eglInitialize(eglDisplay, intArrayOf(0, 0)) // getting OpenGL ES 2
    val eglConfig = chooseEglConfig(egl, eglDisplay)
    val eglContext =
        egl.eglCreateContext(
            eglDisplay,
            eglConfig,
            EGL_NO_CONTEXT,
            intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE))
    val eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, surface, null)

    egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
    GLES20.glClearColor(0f, 0f, 0f, 1f)
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    egl.eglSwapBuffers(eglDisplay, eglSurface)

    egl.eglDestroyContext(eglDisplay, eglContext)
    egl.eglDestroySurface(eglDisplay, eglSurface)
  }
}
