/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.panels

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.samples.premiummediasample.panels.controlsPanel.ControlsPanelConstants
import com.meta.spatial.samples.premiummediasample.ui.theme.Inter18

// Extension methods

/**
 * An extension modifier for creating a circular shadow with a gradient halo effect around a
 * composable. This function draws a radial gradient that simulates a blurred shadow effect
 * extending outwards from the composable's center.
 *
 * **Usage:** This modifier can be applied to any composable to add a gradient-based halo effect
 * around its border. The halo fades out gradually, providing a visually appealing shadow effect.
 * The inner part of the gradient remains transparent, creating an outlined effect.
 *
 * @param color The base color of the gradient halo. The opacity of this color will be adjusted
 *   across different points in the gradient to simulate the shadow fading effect.
 * @param haloBorderWidth The width of the halo shadow effect. This defines how far the gradient
 *   extends from the inner circle to the outer edge.
 *
 * **Note:** Unlike some clipping approaches, this function allows the halo to be drawn outside the
 * composable's bounds since the inner circle is drawn as transparent and not clipped. This makes it
 * suitable for creating halo effects that extend beyond the composable's size.
 *
 * @return A [Modifier] with the circular shadow gradient applied.
 */
fun Modifier.drawOutlineCircularShadowGradient(
    color: Color,
    haloBorderWidth: Dp,
    haloSize: Dp? = null,
    haloOpacity: Float = 0.15f,
    cutHoleMiddle: Boolean = false,
) =
    this.drawBehind {
      if (haloOpacity <= 0f) return@drawBehind
      if (((haloSize?.toPx() ?: 0f) + haloBorderWidth.toPx()) <= 0) return@drawBehind
      val realSizePx = haloSize?.toPx() ?: size.minDimension
      val haloBorderWidthPx = haloBorderWidth.toPx()
      val innerRadius = realSizePx / 2
      val totalRadius = innerRadius + haloBorderWidthPx
      val innerColor = color.copy(haloOpacity)
      val innerColorTransparent = if (cutHoleMiddle) color.copy(0f) else innerColor
      val percentRadius = innerRadius / totalRadius

      // Create a radial gradient with color stops
      val gradientBrush =
          Brush.radialGradient(
              0.0f to innerColorTransparent, // Starting at the center with 0.0 alpha
              percentRadius to innerColorTransparent, // At the edge of the inner circle
              percentRadius to innerColor, // At the edge of the inner circle
              1f to color.copy(alpha = 0f), // At the outer edge with 0 alpha
              center = center,
              radius = totalRadius,
              tileMode = androidx.compose.ui.graphics.TileMode.Clamp,
          )
      drawCircle(brush = gradientBrush, radius = totalRadius, center = center)
    }

fun Modifier.dropShadow(
    shape: Shape,
    color: Color = Color.Black.copy(0.25f),
    blur: Dp = 4.dp,
    offsetY: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.dp,
) =
    this.drawBehind {
      val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
      val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

      val paint = Paint()
      paint.color = color

      if (blur.toPx() > 0) {
        paint.asFrameworkPaint().apply {
          maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }
      }

      drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
      }
    }

@Composable
fun FadeIcon(
    iconBG: Painter,
    iconFade: Painter,
    fadeOpacity: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
  Box() {
    // Icons stacked
    Icon(modifier = modifier, painter = iconBG, tint = color, contentDescription = "IconBG")
    Icon(
        modifier = modifier,
        painter = iconFade,
        tint = color.copy(alpha = fadeOpacity),
        contentDescription = "IconFade",
    )
  }
}

fun formatTime(seconds: Float): String {
  val minutes = (seconds / 60).toInt()
  val remainingSeconds = (seconds % 60).toInt()
  return String.format("%01d:%02d", minutes, remainingSeconds)
}

@Composable
fun HoverIconButton(
    modifier: Modifier = Modifier,
    iconPainter: Painter,
    onClick: () -> Unit,
    iconTint: Color = Color.White,
    haloBorderWidth: Dp = 18.dp,
    haloSize: Dp = 5.dp,
    haloOpacity: Float = 0.2f,
    glowInside: Boolean = true,
) {
  HoverButton(modifier, onClick, iconTint, haloBorderWidth, haloSize, haloOpacity, glowInside) {
    Icon(painter = iconPainter, tint = iconTint, contentDescription = "Rewind 10 Seconds")
  }
}

@Composable
fun HoverButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconTint: Color = Color.White,
    haloBorderWidth: Dp = 18.dp,
    haloSize: Dp = 5.dp,
    haloOpacity: Float = 0.2f,
    glowInside: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
) {
  var isHovered by remember { mutableStateOf(ControlsPanelConstants.controlsDebugHover) }

  // Animate halo width and opacity on hover
  val haloSizeTween by animateDpAsState(targetValue = if (isHovered) haloSize else 0.dp)
  val haloWidthTween by animateDpAsState(targetValue = if (isHovered) haloBorderWidth else 0.dp)
  val haloOpacityTween by animateFloatAsState(targetValue = if (isHovered) haloOpacity else 0f)

  IconButton(
      modifier =
          modifier
              .drawOutlineCircularShadowGradient(
                  color = iconTint,
                  haloSize = haloSizeTween,
                  haloBorderWidth = haloWidthTween,
                  haloOpacity = haloOpacityTween,
                  cutHoleMiddle = !glowInside,
              )
              .pointerInput(Unit) {
                awaitPointerEventScope {
                  while (true) {
                    val event = awaitPointerEvent()
                    when (event.type) {
                      PointerEventType.Enter -> isHovered = true
                      PointerEventType.Exit -> isHovered = false
                      else -> Unit
                    }
                  }
                }
              },
      onClick = onClick,
  ) {
    icon?.invoke()
  }
}

@Composable
fun MetaButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ControlsPanelConstants.controlsBlueColor,
    cornerRadius: Dp = 24.dp,
    blurSize: Dp = 10.dp,
    paddingVertical: Dp = 0.dp,
    paddingHorizontal: Dp = 14.dp,
    textSizeModifier: Float = .667f,
    shadowOpacity: Float = 0.5f,
    onClick: () -> Unit = {},
) {
  var isHovered by remember { mutableStateOf(ControlsPanelConstants.controlsDebugHover) }

  // Animate halo width and opacity on hover
  val shadowBlurTween by animateDpAsState(targetValue = if (isHovered) blurSize else 0.dp)
  val shadowOpacityTween by animateFloatAsState(targetValue = if (isHovered) shadowOpacity else 0f)

  val buttonShape = RoundedCornerShape(cornerRadius)

  Button(
      modifier =
          modifier
              .run {
                // Detect hover state
                pointerInput(Unit) {
                  awaitPointerEventScope {
                    while (true) {
                      val event = awaitPointerEvent()
                      when (event.type) {
                        PointerEventType.Enter -> isHovered = true
                        PointerEventType.Exit -> isHovered = false
                        else -> Unit
                      }
                    }
                  }
                }
              }
              .run {
                // Draw shadow if hovered
                if (shadowOpacity > 0f && shadowBlurTween > 0.dp) {
                  dropShadow(
                      shape = buttonShape,
                      blur = shadowBlurTween,
                      offsetX = 0.dp,
                      offsetY = 0.dp,
                      color = color.copy(shadowOpacityTween),
                  )
                } else this
              },
      shape = buttonShape,
      onClick = onClick,
      elevation = null,
      colors = ButtonDefaults.buttonColors(backgroundColor = color),
  ) {
    Text(
        text,
        modifier = Modifier.padding(paddingHorizontal, paddingVertical),
        style =
            TextStyle(
                fontFamily = Inter18,
                fontSize = (14f * textSizeModifier).sp,
                fontWeight = FontWeight.Normal,
                lineHeight = (20f * textSizeModifier).sp,
            ),
        color = Color.White,
    )
  }
}
