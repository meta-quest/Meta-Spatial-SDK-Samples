// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.widget.ImageView
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible

// This is a custom system created to control general stuff of the app,
// like the loading of the first assets, the behaviour of the toolbar and the loading state of the
// AI chat panel
class GeneralSystem() : SystemBase() {

  private var initTime = System.currentTimeMillis()
  private var skyboxLoaded = false

  override fun execute() {

    // Logo and Home Panel are shown when app starts
    val appStarted = ImmersiveActivity.instance.get()?.appStarted
    if (appStarted == false) {
      val currentTime = System.currentTimeMillis()
      val deltaTime = (currentTime - initTime) / 1000f

      val headPose: Pose = getHeadPose()
      val logo = ImmersiveActivity.instance.get()?.logo

      // We wait for the tracking system to start, then app logo is shown for 5 seconds, followed by
      // home panel.
      if (headPose.t != Vector3(0f) && logo?.getComponent<Visible>()?.isVisible == false) {

        placeInFront(ImmersiveActivity.instance.get()?.logo, Vector3(0f, -0.1f, 0.9f))
        ImmersiveActivity.instance.get()?.logo?.setComponent(Visible(true))
        initTime = System.currentTimeMillis()

        // We load the skybox later, to improve app performance
      } else if (deltaTime >= 1 &&
          logo?.getComponent<Visible>()?.isVisible == true &&
          !skyboxLoaded) {
        skyboxLoaded = true
        ImmersiveActivity.instance.get()?.createSkybox(R.drawable.skybox1)
        // App is initialized after logo has been shown
      } else if (deltaTime >= 5 && skyboxLoaded) {
        ImmersiveActivity.instance.get()?.initApp()
      }
      // If the ambient audio in On, we update the position vector of the ambientSoundPlayer with
      // the Speaker position
    } else if (ImmersiveActivity.instance.get()?.speakerIsOn == true) {
      val pos = ImmersiveActivity.instance.get()?.speaker!!.getComponent<Transform>().transform.t
      ImmersiveActivity.instance.get()?.ambientSoundPlayer!!.setPosition(pos)
    }

    // Toolbar can be called anytime with B or Y button of controllers
    val controllers = Query.where { has(Controller.id) }
    for (entity in controllers.eval()) {
      val controller = entity.getComponent<Controller>()
      if ((controller.buttonState.inv() and controller.changedButtons and ButtonBits.ButtonB) !=
          0 ||
          (controller.buttonState.inv() and controller.changedButtons and ButtonBits.ButtonY) !=
              0) {
        placeInFront(ImmersiveActivity.instance.get()?.toolbarPanel)
      }
    }

    // Update loading button when we are waiting for AI response
    if (ImmersiveActivity.instance.get()?.waitingForAI == true) {
      val uniqueAssets = Query.where { has(UniqueAssetComponent.id) }
      for (entity in uniqueAssets.eval()) {
        if (entity.getComponent<UniqueAssetComponent>().type == AssetType.AI_PANEL) {
          systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)?.thenAccept {
              sceneObject ->
            updateLoading(sceneObject as PanelSceneObject)
          }
        }
      }
    }
  }

  fun updateLoading(panel: PanelSceneObject) {
    val loading: ImageView? = panel.rootView?.findViewById(R.id.loading)
    loading!!.rotation += 5f
  }
}
