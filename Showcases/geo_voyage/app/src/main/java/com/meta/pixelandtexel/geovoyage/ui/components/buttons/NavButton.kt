// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTypography

class NavButtonState(
    val text: String,
    val route: String,
    @DrawableRes val iconResId: Int,
)

@Composable
fun NavButton(state: NavButtonState, selected: Boolean = false, onClick: () -> Unit) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.size(130.dp, 130.dp).clickable { onClick() }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier =
                Modifier.width(120.dp)
                    .height(70.dp)
                    .background(
                        if (selected) Color(0xFFBDEAF4) else Color.Transparent,
                        RoundedCornerShape(24.dp))) {
              Icon(
                  painter = painterResource(id = state.iconResId),
                  contentDescription = null,
                  modifier = Modifier.size(45.dp),
                  tint = MaterialTheme.colorScheme.onSurface)
            }
        Text(
            text = state.text,
            style = GeoVoyageTypography.titleLarge,
            modifier = Modifier.padding(top = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
}

@Preview(showBackground = true)
@Composable
fun PreviewUnselectedGeoVoyageNavButton() {
  val navButtonState =
      NavButtonState(
          text = "GeoVoyage",
          route = "Route",
          iconResId = R.drawable.ic_explore,
      )
  NavButton(navButtonState) {}
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectedGeoVoyageNavButton() {
  val navButtonState =
      NavButtonState(text = "GeoVoyage", route = "Route", iconResId = R.drawable.ic_explore)
  NavButton(navButtonState) {}
}
