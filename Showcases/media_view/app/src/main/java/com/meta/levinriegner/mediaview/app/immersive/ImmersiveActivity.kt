// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive

import android.net.Uri
import android.os.Bundle
import com.meta.levinriegner.mediaview.BuildConfig
import com.meta.levinriegner.mediaview.app.immersive.component.LookAtHead
import com.meta.levinriegner.mediaview.app.immersive.compose.ComponentAppSystemActivity
import com.meta.levinriegner.mediaview.app.immersive.entity.EnvironmentEntities
import com.meta.levinriegner.mediaview.app.immersive.entity.PanelTransformations
import com.meta.levinriegner.mediaview.app.immersive.system.LookAtHeadSystem
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ImmersiveActivity : ComponentAppSystemActivity(), PanelDelegate {

  companion object {
    const val MAX_OPEN_MEDIA = 3
  }

  // Dependencies
  private val panelManager: PanelManager by lazy {
    PanelManager(
        PanelTransformations(EnvironmentEntities(), systemManager),
        scene,
        spatialContext,
    )
  }

    // State
    private var _openMedia =
        MutableStateFlow<Map<Long, MediaModel>>(emptyMap()) // Uses MediaModel.id as key
    val openMedia = _openMedia.asStateFlow()
    private var uploadPanelEntityId: Long? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun registerFeatures(): List<SpatialFeature> {
        val features = mutableListOf<SpatialFeature>(VRFeature(this))
        if (BuildConfig.DEBUG) {
            features.add(CastInputForwardFeature(this))
        }
        return features
    }

  override fun registerPanels(): List<PanelRegistration> {
    return panelManager.providePanelRegistrations()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Disable Locomotion
    systemManager.unregisterSystem<LocomotionSystem>()
    // Register elements
    registerComponents()
    registerSystems()
    // Inflate scene
    loadGLXF()
  }

    override fun onSceneReady() {
        super.onSceneReady()
        // Position the user when they launch into the app
        scene.setViewOrigin(0.0f, 0.0f, -1.0f, 0.0f)
        // Enable better panel rendering (default)
        scene.enableHolePunching(true)
        // Set Mixed Reality passthrough mode
        scene.enablePassthrough(true)
    }

  private fun loadGLXF() {
    activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse(GLXFConstants.URI_STRING),
          rootEntity = Entity.create(),
          keyName = GLXFConstants.COMPOSITION_NAME,
      )
        if(BuildConfig.DEBUG) {
            panelManager.debugPrintNodes()
        }
    }
  }

    private fun registerComponents() {
        componentManager.registerComponent<LookAtHead>(LookAtHead.Companion)
    }

    private fun registerSystems() {
        systemManager.registerSystem(LookAtHeadSystem())
    }

  // region PanelDelegate
  override fun openMediaPanel(mediaModel: MediaModel) {
    Timber.i("Opening media with id: ${mediaModel.id}")
    if (_openMedia.value.containsKey(mediaModel.id)) {
      Timber.w("Media panel is already open")
      return
    }
    if (_openMedia.value.size >= MAX_OPEN_MEDIA) {
      Timber.w("Max media panels open")
      return
    }

    // Register Panel
    registerPanel(panelManager.providePlayerPanelRegistration(mediaModel))
    registerPanel(panelManager.providePlayerMenuRegistration(mediaModel))
    registerPanel(panelManager.provideImmersiveMenuRegistration(mediaModel))

    // Create Entity
    val playerEntity = panelManager.createPlayerEntity(mediaModel)
    panelManager.createPlayerMenuEntity(mediaModel, playerEntity)

    _openMedia.value = _openMedia.value.toMutableMap().apply { put(mediaModel.id, mediaModel) }
  }

  override fun closeMediaPanel(mediaModel: MediaModel) {
    Timber.i("Closing media with id ${mediaModel.id}")
    panelManager.closeMediaPanel(mediaModel)

    _openMedia.value = _openMedia.value.toMutableMap().apply { remove(mediaModel.id) }
  }

  override fun closeAllMedia() {
    Timber.i("Closing all media panels")
    panelManager.closeAllMediaPanels(_openMedia.value.values.toList())
    _openMedia.value = emptyMap()
  }

  override fun maximizeMedia(mediaModel: MediaModel) {
    Timber.i("Maximizing media with id ${mediaModel.id}")
    registerPanel(panelManager.provideImmersiveMenuRegistration(mediaModel))
    panelManager.maximizePlayerPanel(mediaModel)
  }

  override fun minimizeMedia(mediaModel: MediaModel, close: Boolean) {
    Timber.i("Minimizing media with id ${mediaModel.id}")
    panelManager.minimizePlayerPanel(mediaModel)
    if (close) {
      closeMediaPanel(mediaModel)
    }
  }

  override fun openUploadPanel() {
    Timber.i("Opening upload panel")
    if (uploadPanelEntityId != null) {
      Timber.w("Upload panel is already open")
      return
    }

    // Register Panel
    registerPanel(panelManager.provideUploadPanelRegistration())

    // Create Entity
    val ent = panelManager.createUploadEntity()
    uploadPanelEntityId = ent.id
  }

    override fun closeUploadPanel() {
        Timber.i("Closing upload panel")
        uploadPanelEntityId?.let {
            panelManager.destroyEntity(it)
            uploadPanelEntityId = null
        } ?: Timber.w("Upload panel is not open")
    }

    override fun togglePrivacyPolicy(show: Boolean) {
        Timber.i("Toggling privacy policy. Show: $show")
        panelManager.togglePrivacyPolicy(show)
    }

    override fun toggleGallery(show: Boolean) {
        Timber.i("Toggling gallery. Show: $show")
        panelManager.toggleGallery(show)
    }

    override fun toggleOnboarding(show: Boolean) {
        Timber.i("Toggling Onboarding. Show: $show")
        panelManager.toggleOnboarding(show)
    }

    override fun toggleWhatsNew(show: Boolean) {
        Timber.i("Toggling Whats New. Show: $show")
        panelManager.toggleWhatsNew(show)
    }
    // endregion

}
