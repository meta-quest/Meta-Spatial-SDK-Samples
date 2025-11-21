/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meta.spatial.samples.mruksample.anchor_mesh.MrukAnchorMeshSampleActivity
import com.meta.spatial.samples.mruksample.keyboard_tracker.KeyboardTrackerSampleActivity
import com.meta.spatial.samples.mruksample.qr_code_scanner.QrCodeScannerSampleActivity
import com.meta.spatial.samples.mruksample.raycast.RaycastSampleActivity

/** Basic activity that shows a menu to choose between the different sample activities. */
class MrukSampleStartMenuActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      StartMenuLayout(
          onAnchorMeshClick = { startSampleActivity(MrukAnchorMeshSampleActivity::class.java) },
          onRaycastClick = { startSampleActivity(RaycastSampleActivity::class.java) },
          onKeyboardTrackerClick = {
            startSampleActivity(KeyboardTrackerSampleActivity::class.java)
          },
          onQrCodeScannerClick = { startSampleActivity(QrCodeScannerSampleActivity::class.java) },
      )
    }
  }

  private fun <T> startSampleActivity(activityClass: Class<T>) {
    val immersiveIntent =
        Intent(this, activityClass).apply {
          action = Intent.ACTION_MAIN
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    startActivity(immersiveIntent)

    finish()
  }
}
