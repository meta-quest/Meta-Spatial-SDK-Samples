// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialContext
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.Scene
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity

// Enum to save sticky notes possible colors
enum class StickyColor {
  Yellow,
  Green,
  Pink,
  Orange,
  Blue,
  Purple
}

// Class to create a Sticky Note
class StickyNote(
    scene: Scene,
    ctx: SpatialContext,
    var uuid: Int = -1,
    var message: String = "",
    var color: StickyColor,
    var pose: Pose = Pose()
) {

  init {

    var id = getDisposableID()
    val _width = 0.13f
    val _height = 0.13f
    val _widthInPx = 200
    val _dpi = 61

    ImmersiveActivity.instance
        .get()
        ?.registerPanel(
            PanelRegistration(id) {
              layoutResourceId = R.layout.sticky_layout
              config {
                themeResourceId = R.style.Theme_Focus_Transparent
                width = _width
                height = _height
                layoutWidthInPx = _widthInPx
                layoutHeightInPx = (_widthInPx * (_height / _width)).toInt()
                layoutDpi = _dpi
                includeGlass = false
                //                    enableLayer = true
                //                    enableTransparent = true
              }
              panel {
                // Set the selected color to the sticky note
                var bodyColor = R.color.stickyGreenBody
                var topColor = R.color.stickyGreenTop

                when (color) {
                  StickyColor.Yellow -> {
                    bodyColor = R.color.stickyYellowBody
                    topColor = R.color.stickyYellowTop
                  }
                  StickyColor.Green -> {
                    bodyColor = R.color.stickyGreenBody
                    topColor = R.color.stickyGreenTop
                  }
                  StickyColor.Pink -> {
                    bodyColor = R.color.stickyPinkBody
                    topColor = R.color.stickyPinkTop
                  }
                  StickyColor.Orange -> {
                    bodyColor = R.color.stickyOrangeBody
                    topColor = R.color.stickyOrangeTop
                  }
                  StickyColor.Blue -> {
                    bodyColor = R.color.stickyBlueBody
                    topColor = R.color.stickyBlueTop
                  }
                  StickyColor.Purple -> {
                    bodyColor = R.color.stickyPurpleBody
                    topColor = R.color.stickyPurpleTop
                  }
                }

                // Set old or new data to sticky
                val textView = rootView?.findViewById<EditText>(R.id.stickyInputText)
                textView?.setText(message)
                rootView?.findViewById<ImageView>(R.id.stickyBody)?.backgroundTintList =
                    ContextCompat.getColorStateList(ctx, bodyColor)
                rootView?.findViewById<ImageView>(R.id.stickyTopBar)?.backgroundTintList =
                    ContextCompat.getColorStateList(ctx, topColor)

                // We add a listener to show delete button when entity is selected
                addDeleteButton(this.entity!!, this)

                // Wait until the textview is layout to measure the amount of lines and change the
                // text size
                textView
                    ?.viewTreeObserver
                    ?.addOnGlobalLayoutListener(
                        object : ViewTreeObserver.OnGlobalLayoutListener {
                          override fun onGlobalLayout() {
                            // Remove the listener to prevent repeated calls
                            textView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            textView.textSize =
                                pxToDp(
                                    ImmersiveActivity.instance
                                        .get()
                                        ?.resources!!
                                        .getDimension(
                                            if (textView.lineCount <= 7) R.dimen.sticky_text_size
                                            else R.dimen.sticky_text_smaller_size))
                            textView.requestLayout()
                          }
                        })

                // We add an EditText listener to identify when text has been edited and we update
                // it in database
                fun onComplete() {
                  ImmersiveActivity.instance
                      .get()
                      ?.DB
                      ?.updateStickyMessage(uuid, textView?.text.toString())
                }
                addEditTextListeners(textView, ::onComplete, true)
              }
            })

    // Create a grabbable entity with a Panel
    val sticky: Entity =
        Entity.createPanelEntity(id, Transform(pose), Grabbable(true, GrabbableType.PIVOT_Y))

    // If Sticky is new, we save it in database as well
    if (uuid == -1) {
      placeInFront(sticky)
      uuid = getNewUUID()
      ImmersiveActivity.instance
          .get()
          ?.DB
          ?.createSticky(
              uuid,
              ImmersiveActivity.instance.get()?.currentProject?.uuid,
              color,
              message,
              sticky.getComponent<Transform>().transform)
      ImmersiveActivity.instance
          .get()
          ?.playCreationSound(sticky.getComponent<Transform>().transform.t)
    }

    // We add it a ToolComponent to be able to identify it and get the type and uuid of the entity
    sticky.setComponent(ToolComponent(uuid, AssetType.STICKY_NOTE, Vector3(0f, 0.1f, 0f)))
    // We add an AttachableComponent to the object to be able to "stick" it to the boards
    sticky.setComponent(AttachableComponent())
  }
}
