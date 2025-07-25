// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.systems

import android.util.Log
import android.widget.TextView
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.TransformParent
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.TimeComponent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// This is a custom system created to update the clocks and timers of the experience
class UpdateTimeSystem : SystemBase() {

  private var lastTime = System.currentTimeMillis()

  override fun execute() {

    if (ImmersiveActivity.instance.get()?.appStarted == false) return

    val currentTime = System.currentTimeMillis()

    // if there is no current project, we don't update clock
    if (ImmersiveActivity.instance.get()?.currentProject == null) {
      lastTime = currentTime
    }

    // Clock and timers UI updated each 1 second
    val deltaTime = (currentTime - lastTime) / 1000f
    if (deltaTime >= 1) {
      lastTime = currentTime

      // getting all timers and clocks in the scene. We identify them by the TimeComponent
      val timers = Query.where { has(TimeComponent.id) }

      for (entity in timers.eval()) {

        val asset = entity.getComponent<TimeComponent>()
        // Check if it's a timer or a clock to update content
        when (asset.type) {
          AssetType.CLOCK -> {
            systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)?.thenAccept {
                sceneObject ->
              updateClockTime(sceneObject as PanelSceneObject)
            }
          }
          AssetType.TIMER -> {
            systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)?.thenAccept {
                sceneObject ->
              updateTimer(sceneObject as PanelSceneObject, entity, asset)
            }
          }
          else -> {
            Log.e("Focus", "Focus> wrong asset type with Time() component")
          }
        }
      }
    }
  }

  // Update clock panel content
  fun updateClockTime(panel: PanelSceneObject) {
    val currentTime: Date = Calendar.getInstance().time
    val daytime = SimpleDateFormat("a", Locale.getDefault())
    val time = SimpleDateFormat("hh mm", Locale.getDefault())
    val date = SimpleDateFormat("MMM dd", Locale.getDefault())

    panel.rootView?.findViewById<TextView>(R.id.daytime)?.text = daytime.format(currentTime)
    panel.rootView?.findViewById<TextView>(R.id.time)?.text = time.format(currentTime)
    panel.rootView?.findViewById<TextView>(R.id.date)?.text = date.format(currentTime)
  }

  // Update timer panel content
  fun updateTimer(panel: PanelSceneObject, entity: Entity, asset: TimeComponent) {
    val currentTime: Long = System.currentTimeMillis()

    val timeLeft = (asset.startTime + (asset.totalTime * 1000 * 60)) - currentTime

    if (!asset.complete && timeLeft > 0) {
      val minutes = ((timeLeft / (1000 * 60)) % 60)
      val seconds = (timeLeft / 1000).toInt() % 60

      panel.rootView?.findViewById<TextView>(R.id.totalTime)?.text =
          asset.totalTime.toString() + "'"
      panel.rootView?.findViewById<TextView>(R.id.minutes)?.text =
          if (minutes >= 10) minutes.toString() else "0" + minutes.toString()
      panel.rootView?.findViewById<TextView>(R.id.seconds)?.text =
          if (seconds >= 10) seconds.toString() else "0" + seconds.toString()
      // If the timer ended, timer sound should be played
    } else if (!asset.complete && timeLeft <= 0) {
      asset.complete = true
      asset.loop = 1
      entity.setComponent(asset)
      ImmersiveActivity.instance
          .get()
          ?.scene
          ?.playSound(ImmersiveActivity.instance.get()?.timerSound!!, entity, 1f)
      // Make the timer sound 3 times
    } else if (asset.complete &&
        ((asset.loop == 1 && (timeLeft / 1000) < -2) ||
            (asset.loop == 2 && (timeLeft / 1000) < -4))) {
      ImmersiveActivity.instance
          .get()
          ?.scene
          ?.playSound(ImmersiveActivity.instance.get()?.timerSound!!, entity, 1f)
      asset.loop += 1
      entity.setComponent(asset)
      // We delete the timer 6 seconds after it finishes
    } else if (asset.complete && (timeLeft / 1000) < -6) {
      ImmersiveActivity.instance
          .get()
          ?.deleteObject(entity.getComponent<TransformParent>().entity, false, true)
    }
  }
}
