/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.object3dsample

import android.animation.ValueAnimator
import android.net.Uri
import android.view.animation.OvershootInterpolator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import com.meta.spatial.physics.Physics
import com.meta.spatial.physics.PhysicsMaterial
import com.meta.spatial.physics.PhysicsState
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.toolkit.Animated
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.PanelConstants.DEFAULT_DP_PER_METER
import com.meta.spatial.toolkit.PlaybackState
import com.meta.spatial.toolkit.PlaybackType
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

const val PANEL_WIDTH = 2.048f
const val PANEL_HEIGHT = 1.254f
private const val DARK_GRAY = 0x1A000000
private const val LIGHT_GRAY = 0x0D272727

@Composable private fun getGray() = if (isSystemInDarkTheme()) DARK_GRAY else LIGHT_GRAY

@Composable
@Preview(
    widthDp = (DEFAULT_DP_PER_METER * PANEL_WIDTH).toInt(),
    heightDp = (DEFAULT_DP_PER_METER * PANEL_HEIGHT).toInt(),
)
fun ObjectLibraryPanelPreview() {
  val ent = Entity.nullEntity()
  ObjectLibraryPanel(ent, ent, ent, ent, ent, ent)
}

@Composable
fun ObjectLibraryPanel(
    robot: Entity,
    drone: Entity,
    plant: Entity,
    deskLamp: Entity,
    easyChair: Entity,
    sculpture: Entity,
) {
  val darkTheme = isSystemInDarkTheme()
  val colorScheme = if (darkTheme) darkSpatialColorScheme() else lightSpatialColorScheme()
  SpatialTheme(colorScheme = colorScheme) {
    ObjectLibraryScreen(robot, drone, plant, deskLamp, easyChair, sculpture)
  }
}

@Composable
fun ObjectLibraryScreen(
    robot: Entity,
    drone: Entity,
    plant: Entity,
    deskLamp: Entity,
    easyChair: Entity,
    sculpture: Entity,
) {
  Column(
      Modifier.fillMaxSize()
          .clip(SpatialTheme.shapes.large)
          .background(brush = LocalColorScheme.current.panel)
          .padding(73.dp), // Add padding to the entire column
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Column(Modifier.widthIn(max = 512.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      // Heading
      Text(
          text = stringResource(R.string.panel_title),
          textAlign = TextAlign.Center,
          style =
              SpatialTheme.typography.headline1Strong.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.size(24.dp))
      Text(
          text = stringResource(R.string.panel_description),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 8.dp),
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.size(24.dp))
      // First row of images
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        ImageItem(
            drawableRes = R.drawable.bot,
            modifier = Modifier.weight(1f),
            onClick = { setUpButton(robot, dimensions = Vector3(0.11f, 0.21f, 0.08f)) },
        )
        ImageItem(
            drawableRes = R.drawable.quad,
            modifier = Modifier.weight(1f),
            onClick = {
              setUpButton(drone, isAnimated = true, dimensions = Vector3(0.106f, 0.07f, 0.22f))
            },
        )
        ImageItem(
            drawableRes = R.drawable.plant,
            modifier = Modifier.weight(1f),
            onClick = { setUpButton(plant, dimensions = Vector3(0.09f, 0.09f, 0.09f)) },
        )
      }
      Spacer(Modifier.size(16.dp))
      // Second row of images
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        ImageItem(
            drawableRes = R.drawable.lamp,
            modifier = Modifier.weight(1f),
            onClick = { setUpButton(deskLamp, dimensions = Vector3(0.2f, 0.34f, 0.06f)) },
        )
        ImageItem(
            drawableRes = R.drawable.chair,
            modifier = Modifier.weight(1f),
            onClick = { setUpButton(easyChair, dimensions = Vector3(0.3f, 0.34f, 0.3f)) },
        )
        ImageItem(
            drawableRes = R.drawable.sculpture,
            modifier = Modifier.weight(1f),
            onClick = { setUpButton(sculpture, dimensions = Vector3(0.23f, 0.17f, 0.17f)) },
        )
      }
    }
  }
}

@Composable
fun ImageItem(drawableRes: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Box(
      modifier =
          modifier
              .aspectRatio(1f) // Maintain a 1:1 aspect ratio
              .clip(RoundedCornerShape(8.dp)) // Adjust shape as needed
              .background(color = Color(getGray())) // Use a color or a brush for the background
              .clickable(onClick = onClick)
  ) {
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale =
            ContentScale.Crop, // Use Crop to fill the space while maintaining aspect ratio
    )
  }
}

private fun setUpButton(
    entity: Entity? = null,
    collisionMesh: String = "box",
    isAnimated: Boolean = false,
    dimensions: Vector3 = Vector3(0.1f, 0.1f, 0.1f),
) {

  val scale: Vector3 = entity?.getComponent<Scale>()?.scale?.copy() ?: Vector3(1f, 1f, 1f)
  val glb = entity?.getComponent<Mesh>()?.mesh?.toString()

  val objModel =
      Entity.create(
          listOf(
              Mesh(
                  mesh = Uri.parse(glb),
                  defaultShaderOverride = SceneMaterial.PHYSICALLY_BASED_SHADER,
              ),
              Grabbable(type = GrabbableType.PIVOT_Y),
              Scale(scale),
              Physics(
                      shape = collisionMesh,
                      density = 0.1f,
                      state = PhysicsState.DYNAMIC,
                      dimensions = dimensions,
                  )
                  .applyMaterial(PhysicsMaterial.WOOD),
              Transform(Pose(Vector3(0f, 1.2f, 2.1f), Quaternion(0f, 180f, 0f))),
          )
      )

  scaleUp(objModel, scale)

  // add animation
  if (isAnimated) {
    objModel.setComponent(
        Animated(
            startTime = System.currentTimeMillis(),
            playbackState = PlaybackState.PLAYING,
            playbackType = PlaybackType.LOOP,
        )
    )
  }
}

private fun scaleUp(entity: Entity, scale: Vector3) {
  ValueAnimator.ofFloat(0f, 1f)
      .apply {
        duration = 1000
        interpolator = OvershootInterpolator(1f)
        addUpdateListener { animation ->
          val v = animation.animatedValue as Float
          entity.setComponent(Scale(scale.multiply(v)))
        }
      }
      .start()
}
