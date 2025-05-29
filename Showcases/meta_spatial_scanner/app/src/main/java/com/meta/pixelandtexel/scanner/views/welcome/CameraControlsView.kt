// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.views.components.Panel
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.SpatialTheme
import kotlinx.coroutines.delay

@Composable
fun CameraControlsView(onContinue: (() -> Unit)) {
  var visible by remember { mutableStateOf(true) }

  // toggle visibility every 2 seconds
  LaunchedEffect(Unit) {
    while (true) {
      delay(2000L)
      visible = !visible
    }
  }

  Panel(outerPadding = false) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(modifier = Modifier.weight(1f).background(Color(0x88000000)).fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()) {
              AnimatedVisibility(
                  visible = visible,
                  enter = fadeIn(tween(durationMillis = 800)),
                  exit = fadeOut(tween(durationMillis = 800)),
              ) {
                Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
                  Image(
                      painter = painterResource(R.drawable.hand_swipe),
                      contentDescription = "",
                      modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 10.dp))
                  Image(
                      painter = painterResource(R.drawable.arrow_over_3x),
                      contentDescription = "",
                      modifier =
                          Modifier.fillMaxSize().padding(top = 25.dp, bottom = 100.dp, end = 10.dp))
                }
              }
            }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()) {
              AnimatedVisibility(
                  visible = !visible,
                  enter = fadeIn(tween(durationMillis = 800)),
                  exit = fadeOut(tween(durationMillis = 800)),
              ) {
                Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
                  Image(
                      painter = painterResource(R.drawable.hand_open),
                      contentDescription = "",
                      modifier = Modifier.fillMaxSize().padding(vertical = 10.dp))
                }
              }
            }
      }
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.weight(0.95f).fillMaxWidth().padding(20.dp)) {
            Text(
                stringResource(R.string.camera_controls_title),
                style = SpatialTheme.typography.headline3Strong,
                color = Color.White)
            Text(
                stringResource(R.string.camera_controls_1),
                style = SpatialTheme.typography.body2,
                color = Color.White)
            Text(
                stringResource(R.string.camera_controls_2),
                style = SpatialTheme.typography.body2,
                color = Color.White)
            PrimaryButton(
                stringResource(R.string.btn_got_it), onClick = onContinue, expanded = true)
          }
    }
  }
}

@Preview(widthDp = 368, heightDp = 404)
@Composable
private fun CameraControlsViewPreview() {
  CameraControlsView {}
}
