// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.objectinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.models.ImageCopyPanelContent
import com.meta.pixelandtexel.scanner.models.PanelContentType
import com.meta.pixelandtexel.scanner.models.TileContent
import com.meta.pixelandtexel.scanner.models.TilesPanelContent
import com.meta.pixelandtexel.scanner.viewmodels.CuratedObjectInfoViewModel
import com.meta.pixelandtexel.scanner.views.components.Panel
import com.meta.spatial.uiset.navigation.SpatialSideNavItem
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Info
import com.meta.spatial.uiset.theme.icons.regular.VrObject

@Composable
fun CuratedObjectInfoScreen(
    vm: CuratedObjectInfoViewModel = viewModel(),
    onResume: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null
) {
  val navController = rememberNavController()
  val route by vm.route

  LaunchedEffect(route) { navController.navigate(route) { launchSingleTop = true } }

  SpatialTheme {
    Panel {
      Row {
        Column {
          Spacer(Modifier.height(85.dp))
          for (i in 0 until vm.routes.size) {
            val r = vm.routes[i]
            val icon = if (i % 2 == 0) SpatialIcons.Regular.Info else SpatialIcons.Regular.VrObject

            SpatialSideNavItem(
                primaryLabel = r,
                icon = { Image(icon, "Navigate to $r") },
                onClick = { vm.navTo(r, i) },
                collapsed = true,
                selected = r == route)
            Spacer(Modifier.height(8.dp))
          }
        }
        Spacer(Modifier.width(20.dp).fillMaxHeight())
        NavHost(navController = navController, startDestination = route) {
          for (i in 0 until vm.routes.size) {
            val r = vm.routes[i]
            val content = vm.uiContent[i]

            composable(r) {
              when (content.layoutType) {
                PanelContentType.TILES ->
                    CuratedObjectTilesPanel(
                        content as TilesPanelContent,
                        { vm.onTileSelected(i, it) },
                        onResume,
                        onClose)

                PanelContentType.IMAGE_COPY ->
                    CuratedObjectImageCopyPanel(content as ImageCopyPanelContent, onResume, onClose)

                else -> throw RuntimeException("Unsupported panel content type")
              }
            }
          }
        }
      }
    }
  }
}

@Preview(widthDp = 708, heightDp = 644)
@Composable
private fun CuratedObjectInfoScreenPreviewTiles() {
  CuratedObjectInfoScreen(
      CuratedObjectInfoViewModel(
          listOf(
              TilesPanelContent(
                  "Refrigerator",
                  null,
                  listOf(
                      TileContent(
                          "How To:", "Troubleshoot Wifi Connection", R.drawable.fridge_light),
                      TileContent("How To:", "Connect Your Apps", R.drawable.tv_apps),
                      TileContent("How To:", "Prepare for Wall Mounting", R.drawable.tv_mount),
                      TileContent("How To:", "Connect a Sound System", R.drawable.tv_sound))))))
}

@Preview(widthDp = 708, heightDp = 644)
@Composable
private fun CuratedObjectInfoScreenPreviewImageCopy() {
  CuratedObjectInfoScreen(
      CuratedObjectInfoViewModel(
          listOf(
              ImageCopyPanelContent(
                  "Model: RF32CG5900SR/AA",
                  null,
                  R.drawable.fridge_hero,
                  "## 30 cu. ft. Mega Capacity\n" +
                      "\n" +
                      "Store more groceries with more room to stay organized.\n" +
                      "\n" +
                      "## Features\n" +
                      "\n" +
                      "- Share pictures, stream music and videos, access recipes, control your smart devices and Alexa all from the fridge.\n" +
                      "- Enjoy your favorite beverage with your choice of ice. Choose from cubed ice or Ice Bites from the Dual Auto Ice Maker in the freezer drawer, or choose from curved or crushed ice from the external dispenser.\n" +
                      "- A flat-front fridge design with recessed drawer handle blends beautifully into the kitchen."))))
}
