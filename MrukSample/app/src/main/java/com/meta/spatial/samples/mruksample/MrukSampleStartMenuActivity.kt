/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

/** Basic activity that shows a menu to choose between the different sample activities. */
class MrukSampleStartMenuActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.ui_mruk_start_menu)

    val anchorMeshButton = findViewById<Button>(R.id.button_sample_anchor_mesh)
    anchorMeshButton?.setOnClickListener {
      startSampleActivity(MrukAnchorMeshSampleActivity::class.java)
    }

    val raycastButton = findViewById<Button>(R.id.button_sample_raycast)
    raycastButton?.setOnClickListener { startSampleActivity(RaycastSampleActivity::class.java) }
  }

  private fun <T> startSampleActivity(c: Class<T>) {
    val activity = this
    val immersiveIntent =
        Intent(activity, c).apply {
          action = Intent.ACTION_MAIN
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    startActivity(immersiveIntent)

    finish()
  }
}
