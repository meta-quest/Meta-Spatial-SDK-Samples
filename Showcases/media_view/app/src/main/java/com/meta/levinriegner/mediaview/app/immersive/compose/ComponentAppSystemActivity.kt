// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.compose

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.meta.spatial.toolkit.AppSystemActivity

open class ComponentAppSystemActivity : AppSystemActivity() {
  private val lifecycleOwner = ComposeActivityLifecycleOwner()

  protected fun attachLifecycleToRootView(rootView: View?) {
    if (rootView != null) {
      rootView.setViewTreeLifecycleOwner(lifecycleOwner)
      rootView.setViewTreeViewModelStoreOwner(lifecycleOwner)
      rootView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleOwner.onCreate()
  }

  override fun onDestroy() {
    super.onDestroy()
    lifecycleOwner.onDestroy()
  }

  override fun onPause() {
    super.onPause()
    lifecycleOwner.onPause()
  }

  override fun onResume() {
    super.onResume()
    lifecycleOwner.onResume()
  }

  override fun onSpatialShutdown() {
    lifecycleOwner.onSpatialShutdown()
    super.onSpatialShutdown()
  }
}

class ComposeActivityLifecycleOwner : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
  private val store = ViewModelStore()
  private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

  private val savedStateRegistryController = SavedStateRegistryController.create(this)

  fun onCreate() {
    savedStateRegistryController.performRestore(null)
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
  }

  fun onResume() {
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  fun onPause() {
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  }

  fun onDestroy() {
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    store.clear()
  }

  fun onSpatialShutdown() {
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    store.clear()
  }

  override val lifecycle: Lifecycle
    get() = lifecycleRegistry

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  override val viewModelStore: ViewModelStore
    get() = store
}
