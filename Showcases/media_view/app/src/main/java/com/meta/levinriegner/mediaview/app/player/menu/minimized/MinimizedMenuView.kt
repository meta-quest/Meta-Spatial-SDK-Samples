// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.menu.minimized

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import timber.log.Timber

@Composable
fun MinimizedMenuView(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit,
) {
  val isMenuVisible = remember { mutableStateOf(false) }
  val confirmingDelete = remember { mutableStateOf(false) }
  return Column(
      modifier = modifier,
  ) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.size(Dimens.playerMenuButtonSize.dp)
                .aspectRatio(1f)
                .background(
                    Brush.verticalGradient(listOf(AppColor.GradientStart, AppColor.GradientEnd)),
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = AppColor.MetaBlu,
                    shape = RoundedCornerShape((Dimens.playerMenuButtonSize / 2).dp),
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                  isMenuVisible.value = !isMenuVisible.value
                },
    ) {
      Icon(
          painter = painterResource(id = R.drawable.icon_menu_dots_horizontal),
          contentDescription = "Menu",
          tint = AppColor.White,
      )
    }
    Spacer(modifier = Modifier.size(Dimens.small))
    MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))) {
      DropdownMenu(
          expanded = isMenuVisible.value,
          onDismissRequest = { isMenuVisible.value = false },
          modifier =
              Modifier.shadow(2.dp)
                  .border(1.dp, AppColor.MetaBlu, RoundedCornerShape(16.dp))
                  .fillMaxWidth()
                  .background(
                      Brush.verticalGradient(listOf(AppColor.GradientStart, AppColor.GradientEnd))
                  ),
      ) {
        DropdownMenuItem(
            leadingIcon = {
              (Icon(
                  painter = painterResource(id = R.drawable.icon_immersive_view),
                  contentDescription = null,
                  modifier = Modifier.size(30.dp),
              ))
            },
            text = { Text(fontSize = 17.sp, text = "Immersive View") },
            onClick = {
              isMenuVisible.value = false
              onMaximize()
            },
        )
        HorizontalDivider(color = AppColor.White15, thickness = 1.dp)
        DropdownMenuItem(
            leadingIcon = {
              (Icon(
                  imageVector =
                      if (!confirmingDelete.value) Icons.Outlined.Delete
                      else Icons.Outlined.AutoDelete,
                  contentDescription = null,
                  modifier = Modifier.size(30.dp),
              ))
            },
            text = {
              Text(
                  fontSize = 17.sp,
                  text = if (!confirmingDelete.value) "Delete" else "Confirm Delete?",
              )
            },
            onClick = {
              if (confirmingDelete.value) {
                onDelete()
              } else {
                Timber.i("Confirming delete")
              }
              confirmingDelete.value = !confirmingDelete.value
            },
        )
        HorizontalDivider(color = AppColor.White15, thickness = 1.dp)
        DropdownMenuItem(
            leadingIcon = {
              (Icon(
                  painter = painterResource(id = R.drawable.icon_close),
                  contentDescription = null,
                  modifier = Modifier.size(30.dp),
              ))
            },
            text = { Text(fontSize = 17.sp, text = "Close") },
            onClick = {
              isMenuVisible.value = false
              onClose()
            },
        )
      }
    }
  }
}
