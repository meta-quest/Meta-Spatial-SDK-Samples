// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.pixelandtexel.scanner.objectdetection.viewmodels.ObjectLabelViewModel
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme

/**
 * A simple Composable that renders the label or category of a detected object on a panel, which is
 * positioned under the outline of the object, wrapped in a button composable to listen for clicks.
 *
 * @param vm [ObjectLabelViewModel] view model containing the label string to display.
 * @param onClick Callback for if the user selects this composable.
 */
@Composable
fun ObjectLabelScreen(vm: ObjectLabelViewModel = viewModel(), onClick: (() -> Unit)? = null) {
  val name by vm.name

  SpatialTheme {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Button(
              onClick = { onClick?.invoke() },
              colors =
                  ButtonColors(
                      containerColor = Color.Transparent,
                      contentColor = Color.Transparent,
                      disabledContainerColor = Color.Transparent,
                      disabledContentColor = Color.Transparent),
              shape = CutCornerShape(0.dp),
              contentPadding = PaddingValues(0.dp),
              modifier = Modifier.fillMaxSize()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.background(
                                color = SpatialColor.b70, shape = SpatialTheme.shapes.large)
                            .clip(SpatialTheme.shapes.large)
                            .padding(horizontal = 20.dp)
                            .height(40.dp)
                            .wrapContentWidth()) {
                      Text(
                          name,
                          color = SpatialColor.white100,
                          style = SpatialTheme.typography.body1)
                    }
              }
        }
  }
}

@Preview(widthDp = 516, heightDp = 414)
@Composable
private fun ObjectLabelScreenPreview() {
  ObjectLabelScreen(ObjectLabelViewModel("Name"))
}
