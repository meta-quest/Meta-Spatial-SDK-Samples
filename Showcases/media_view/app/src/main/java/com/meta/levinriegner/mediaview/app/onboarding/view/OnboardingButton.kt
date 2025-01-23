// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens

@Composable
fun OnboardingButton(
    onPressed: () -> Unit,
) {
  IconButton(
      modifier =
          Modifier.border(width = 1.dp, color = AppColor.White30, shape = CircleShape)
              .size(Dimens.xLarge),
      colors =
          IconButtonDefaults.iconButtonColors(
              contentColor = AppColor.White,
          ),
      onClick = { onPressed() }) {
        Icon(
            Icons.Filled.QuestionMark,
            contentDescription = "Open Onboarding",
            modifier = Modifier.padding(Dimens.xSmall))
      }
}
