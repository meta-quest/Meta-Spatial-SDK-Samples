// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens

@Composable
fun RoundedButton(title: String, onClick: () -> Unit) {
  OutlinedButton(
      onClick = onClick,
      modifier = Modifier.height(28.dp),
      colors =
          ButtonDefaults.outlinedButtonColors(
              contentColor = AppColor.White,
              containerColor = Color.Transparent,
          ),
      contentPadding =
          PaddingValues(
              horizontal = Dimens.large,
              vertical = 0.dp,
          ),
      border =
          BorderStroke(
              1.dp,
              AppColor.MetaBlu,
          ),
  ) {
    Text(
        title,
        color = AppColor.White,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 10.sp,
        modifier =
            Modifier.align(
                alignment = Alignment.CenterVertically,
            ),
    )
  }
}
