// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R

/**
 * Standard GeoVoyage circular icon button.
 *
 * @param iconResId Icon to display in the circular icon button
 * @param contentDescription Custom content description for icon button
 * @param modifier the [Modifier] to be applied to this icon button
 * @param onClick Action to trigger when icon button is clicked
 */
@Composable
fun CircleButton(
    @DrawableRes iconResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
  FloatingActionButton(
      onClick = onClick,
      shape = CircleShape,
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
      contentColor = MaterialTheme.colorScheme.outline,
      elevation =
          FloatingActionButtonDefaults.elevation(
              defaultElevation = 35.dp,
              pressedElevation = 12.dp,
              focusedElevation = 12.dp,
              hoveredElevation = 12.dp,
          ),
      modifier = modifier.size(76.dp),
  ) {
    val painter = painterResource(id = iconResId)
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = Modifier.padding(10.dp),
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewGeoVoyageCircleButton() {
  CircleButton(
      iconResId = R.drawable.ic_settings,
      contentDescription = "Settings",
      modifier = Modifier.padding(16.dp)) {}
}
