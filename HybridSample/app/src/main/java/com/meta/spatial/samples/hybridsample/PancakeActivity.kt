/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.hybridsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

// default activity
class PancakeActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    this.setTheme(R.style.PanelAppThemeTransparent)
    setContentView(R.layout.ui_pancake)
    val button = findViewById<Button>(R.id.chosen)
    val activity = this
    button?.setOnClickListener {
      val immersiveIntent =
          Intent(activity, HybridSampleActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
      startActivity(immersiveIntent)
    }
  }
}
