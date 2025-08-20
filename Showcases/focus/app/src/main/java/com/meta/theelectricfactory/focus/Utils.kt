// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkGrabbable
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.ButtonDownEventArgs
import com.meta.spatial.runtime.ControllerButton
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent

var temporalID: Int = 0

// Creates temporal ids to identify entities during runtime
fun getDisposableID(): Int {
  temporalID += 1
  return temporalID
}

// Creates ids to identify objects in projects. This is not temporal
fun getNewUUID(): Int {
  val savedData =
      ImmersiveActivity.instance
          .get()!!
          .getSharedPreferences(
              "com.meta.theelectricfactory.focus.MySavedData",
              Activity.MODE_PRIVATE,
          )
  val uuid = savedData.getInt("currentUUID", -1)

  with(savedData.edit()) {
    putInt("currentUUID", uuid + 1)
    apply()
  }

  Log.i("Focus", "Focus> NEW UUID GENERATED: ${uuid + 1}")
  return (uuid + 1)
}

// This function adds an input listener to an entity, to detect when has been selected and show the
// delete button on it
fun addDeleteButton(entity: Entity, panel: PanelSceneObject? = null) {
  // If entity has a Panel, we need to detect the input in the panel
  if (panel != null) {
    panel.addInputListener(
        object : InputListener {
          override fun onInput(
              receiver: SceneObject,
              hitInfo: HitInfo,
              sourceOfInput: Entity,
              changed: Int,
              clicked: Int,
              downTime: Long,
          ): Boolean {
            // Check if panel has been clicked/tapped with button A, X, triggers o squeezers
            if (
                (changed and ButtonBits.ButtonA) != 0 ||
                    (changed and ButtonBits.ButtonX) != 0 ||
                    (changed and ButtonBits.ButtonTriggerR) != 0 ||
                    (changed and ButtonBits.ButtonTriggerL) != 0 ||
                    (changed and ButtonBits.ButtonSqueezeR) != 0 ||
                    (changed and ButtonBits.ButtonSqueezeL) != 0
            ) {

              if (
                  (clicked and ButtonBits.ButtonA) != 0 ||
                      (clicked and ButtonBits.ButtonX) != 0 ||
                      (clicked and ButtonBits.ButtonTriggerR) != 0 ||
                      (clicked and ButtonBits.ButtonTriggerL) != 0 ||
                      (clicked and ButtonBits.ButtonSqueezeR) != 0 ||
                      (clicked and ButtonBits.ButtonSqueezeL) != 0
              ) {

                ImmersiveActivity.instance.get()?.selectElement(entity)
                return true
              }
            }
            return false
          }
        }
    )
    // If entity has a Mesh component, object is selected with a registerEventListener
  } else {
    addOnSelectListener(
        entity,
        fun() {
          ImmersiveActivity.instance.get()?.selectElement(entity)
        },
    )
  }
}

// Function to detect when an entity has been selected or grabbed
fun addOnSelectListener(entity: Entity, onClick: () -> (Unit)) {

  // We treat the speaker differently from other objects, preventing it from being selected when the
  // user grabs it to avoid the audio stopping.
  if (entity == ImmersiveActivity.instance.get()?.speaker) {
    entity.registerEventListener<ButtonDownEventArgs>(ButtonDownEventArgs.EVENT_NAME) {
        ent,
        eventArgs ->
      if (
          eventArgs.button == ControllerButton.A ||
              eventArgs.button == ControllerButton.X ||
              eventArgs.button == ControllerButton.RightTrigger ||
              eventArgs.button == ControllerButton.LeftTrigger
      ) {
        onClick()
      }
    }
    // Check if entity has been selected or grabbed with button A, X, triggers, or grips
  } else {
    entity.registerEventListener<ButtonDownEventArgs>(ButtonDownEventArgs.EVENT_NAME) {
        ent,
        eventArgs ->
      if (
          eventArgs.button == ControllerButton.A ||
              eventArgs.button == ControllerButton.X ||
              eventArgs.button == ControllerButton.RightTrigger ||
              eventArgs.button == ControllerButton.LeftTrigger ||
              eventArgs.button == ControllerButton.RightSqueeze ||
              eventArgs.button == ControllerButton.LeftSqueeze
      ) {
        onClick()
      }
    }
  }
}

// Function to get the correct size for some tools.
fun getAssetSize(type: AssetType, index: Int): Float {
  var sizeArray = arrayOf(0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f) // Default size

  when (type) {
    AssetType.BOARD -> {
      sizeArray = boardSizeArray
    }
    AssetType.SHAPE_2D -> {
      sizeArray = shape2DSizeArray
    }
    AssetType.SHAPE_3D -> {
      sizeArray = shape3DSizeArray
    }
    else -> {
      //
    }
  }
  return sizeArray[index]
}

// Function to get the correct height for the delete button for some tools.
fun getDeleteButtonHeight(type: AssetType, index: Int): Float {
  var heightArray = arrayOf(0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f) // Default height

  when (type) {
    AssetType.ARROW -> {
      heightArray = arrowHeightArray
    }
    AssetType.BOARD -> {
      heightArray = boardHeightArray
    }
    else -> {
      //
    }
  }
  return heightArray[index]
}

// Function to get all children of an entity
fun getChildren(parent: Entity): MutableList<Entity> {
  var children: MutableList<Entity> = mutableListOf()

  val allChildren = Query.where { has(TransformParent.id) }
  for (child in allChildren.eval()) {
    val _parent = child.getComponent<TransformParent>().entity
    if (parent == _parent) {
      children.add(child)
    }
  }
  return children
}

// Function to get the pose of the user's head
fun getHeadPose(): Pose {
  val head =
      Query.where { has(AvatarAttachment.id) }
          .eval()
          .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
          .first()
  return head.getComponent<Transform>().transform
}

// Function to place an entity in front of the user, at the same height and facing it
fun placeInFront(entity: Entity?, offset: Vector3 = Vector3(0f), bigPanel: Boolean = false) {
  val headPose: Pose = getHeadPose()

  val isToolbar = entity!! == ImmersiveActivity.instance.get()?.toolbarPanel

  // We treat toolbar and big panels differently from other objects.
  val height: Float = if (isToolbar) 0.35f else 0.1f
  var distanceFromUser: Float = if (bigPanel) 0.9f else 0.7f

  // Having the users position, we place the entity in front of it, at a particular distance and
  // height
  var newPos = headPose.t + headPose.q * Vector3.Forward * distanceFromUser
  newPos.y = headPose.t.y - height

  // If there is an offset vector, we place the object at the vector position (using user's position
  // as reference)
  if (offset != Vector3(0f)) {
    newPos =
        headPose.t + headPose.q * Vector3.Right * offset.x + headPose.q * Vector3.Forward * offset.z

    newPos.y = headPose.t.y + offset.y
  }

  // Add rotation to look in same vector direction as user
  var newRot = Quaternion.lookRotation(newPos - headPose.t)
  val billboardOrientationEuler: Vector3 =
      entity.tryGetComponent<IsdkGrabbable>()?.billboardOrientation ?: Vector3(0f, 0f, 0f)

  newRot *=
      Quaternion(
          billboardOrientationEuler.x,
          billboardOrientationEuler.y,
          billboardOrientationEuler.z,
      )

  entity.setComponent(Transform(Pose(newPos, newRot)))
}

// Convert dp to px
fun dpToPx(dp: Int): Int {
  val displayMetrics: DisplayMetrics =
      ImmersiveActivity.instance.get()!!.spatialContext.resources.displayMetrics
  return (dp * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun pxToDp(px: Float): Float {
  return px / ImmersiveActivity.instance.get()!!.spatialContext.resources.displayMetrics.density
}

// Function to detect when an EditText has been updated and perform an action
fun addEditTextListeners(
    editText: EditText?,
    onComplete: () -> (Unit),
    updateWithoutEnter: Boolean = false,
) {

  // Detect Enter keyboard (Done) to perform action
  if (!updateWithoutEnter) {
    var enterTime: Long = 0
    val waitingInterval: Long = (0.5f * 1000).toLong() // 0.5 seconds

    editText?.setOnEditorActionListener { v, actionId, event ->
      if (
          actionId == EditorInfo.IME_ACTION_DONE ||
              event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
      ) {

        // To avoid multiple events to trigger the same action
        if (System.currentTimeMillis() - enterTime >= waitingInterval) {
          enterTime = System.currentTimeMillis()
          onComplete()
          return@setOnEditorActionListener true
        }
      }
      false
    }
    // In cases that virtual keyboard doesn't have an Enter (Done) button, like in multiline texts,
    // we wait 1 second after user finished writing to perform the action
  } else {
    var lastTextChangeTime: Long = 0
    val typingInterval: Long = 1 * 1000
    val handler = Handler(Looper.getMainLooper())

    editText?.addTextChangedListener(
        object : TextWatcher {
          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

          // update lastTime if user is still writing
          override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            lastTextChangeTime = System.currentTimeMillis()
          }

          override fun afterTextChanged(s: Editable?) {
            handler.postDelayed(
                {
                  // Wait to perform action
                  if (System.currentTimeMillis() - lastTextChangeTime >= typingInterval) {
                    onComplete()
                  }
                },
                typingInterval,
            )
          }
        }
    )
  }
}
