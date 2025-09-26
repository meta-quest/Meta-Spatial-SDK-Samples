// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.managers

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneAudioPlayer
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Transform
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.UniqueAssetComponent
import com.meta.theelectricfactory.focus.viewmodels.FocusViewModel

class AudioManager {

  val immA: ImmersiveActivity?
    get() = ImmersiveActivity.getInstance()

  // Sounds
  lateinit var ambientSound: SceneAudioAsset
  lateinit var createSound: SceneAudioAsset
  lateinit var deleteSound: SceneAudioAsset
  lateinit var timerSound: SceneAudioAsset
  lateinit var ambientSoundPlayer: SceneAudioPlayer

  var audioIsOn = false

  companion object {
    val instance: AudioManager by lazy { AudioManager() }
  }

  fun switchAudio() {
    if (audioIsOn) {
      stopAmbientSound()
    } else {
      playAmbientSound()
    }
  }

  // Play ambient audio and save its state in database
  fun playAmbientSound() {
    audioIsOn = true

    // Playing spatial sound in loop
    ambientSoundPlayer.play(immA!!.speaker.getComponent<Transform>().transform.t, 0.2f, true)

    // Change texture to speaker state image
    immA
        ?.speakerState
        ?.setComponent(
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.speaker_on
              alphaMode = 1
            }
        )
    // Project audio state updated in database
    immA
        ?.DB
        ?.updateUniqueAsset(
            immA!!.speaker.getComponent<UniqueAssetComponent>().uuid,
            state = audioIsOn,
        )
    // Change texture to audio button state image in toolbar
    FocusViewModel.instance.setSpeakerIsOn(true)
  }

  // Stop ambient audio and save its state in database
  fun stopAmbientSound() {
    audioIsOn = false
    ambientSoundPlayer.stop()

    // Change texture to speaker state image
    immA
        ?.speakerState
        ?.setComponent(
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.speaker_off
              alphaMode = 1
            }
        )
    // Project audio state updated in database
    immA
        ?.DB
        ?.updateUniqueAsset(
            immA!!.speaker.getComponent<UniqueAssetComponent>().uuid,
            state = audioIsOn,
        )
    // Change texture to audio button state image in toolbar
    FocusViewModel.instance.setSpeakerIsOn(false)
  }

  // Play sound when a tool has been created
  fun playCreationSound(position: Vector3) {
    immA?.scene?.playSound(createSound, position, 1f)
  }

  fun playTimerSound(entity: Entity) {
    immA?.scene?.playSound(timerSound, entity, 1f)
  }

  fun playDeleteSound(position: Vector3 = Vector3(0f)) {
    immA?.scene?.playSound(deleteSound, position, 1f)
  }
}
