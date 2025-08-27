// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens

@Composable
fun GalleryMenuView(
    openCount: Int,
    canOpenMore: Boolean,
    onCloseAll: () -> Unit,
) {
  if (openCount == 0) return Box(Modifier)
  Box(
      modifier =
          Modifier.size(600.dp, 100.dp)
              .padding(16.dp)
              .clip(RoundedCornerShape(40.dp))
              .border(1.dp, AppColor.MetaBlu, RoundedCornerShape(40.dp))
              .background(
                  Brush.verticalGradient(listOf(AppColor.GradientStart, AppColor.GradientEnd))
              ),
      contentAlignment = Alignment.Center,
  ) {
    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
          text =
              if (canOpenMore)
                  pluralStringResource(
                      id = R.plurals.n_files_open,
                      openCount,
                      openCount,
                  )
              else stringResource(id = R.string.max_files_open, openCount),
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Left,
          modifier = Modifier.offset(0.dp, 5.dp),
      )
      Spacer(modifier = Modifier.padding(Dimens.medium))

      OutlinedButton(
          colors =
              ButtonDefaults.buttonColors(
                  contentColor = AppColor.White,
                  containerColor = Color.Transparent,
              ),
          onClick = { onCloseAll() },
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(painter = painterResource(id = R.drawable.icon_close), contentDescription = "Close")
          Spacer(modifier = Modifier.size(Dimens.xSmall))
          Text(stringResource(id = R.string.files_close_all_button))
        }
      }
    }
  }
}
