// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens

@Composable
fun OnboardingControls(
    onPreviousButtonPressed: () -> Unit,
    onNextButtonPressed: () -> Unit,
    onFinishButtonPressed: (() -> Unit)?,
    currentStep: Int,
    totalSteps: Int,
) {
  Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(
        modifier =
            Modifier.border(width = 1.dp, color = AppColor.White30, shape = CircleShape)
                .size(Dimens.large),
        colors =
            IconButtonDefaults.iconButtonColors(
                contentColor = AppColor.White,
            ),
        onClick = onPreviousButtonPressed,
    ) {
      Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Go to previous step",
          modifier = Modifier.padding(Dimens.xSmall),
      )
    }

    Text(
        "$currentStep of $totalSteps",
        style = MaterialTheme.typography.bodySmall,
        color = AppColor.White30,
        modifier = Modifier.align(alignment = Alignment.CenterVertically),
    )

    if (onFinishButtonPressed != null) {
      IconButton(
          modifier =
              Modifier.border(width = 1.dp, color = AppColor.MetaBlu, shape = CircleShape)
                  .size(Dimens.large),
          colors =
              IconButtonDefaults.iconButtonColors(
                  contentColor = AppColor.White,
                  containerColor = AppColor.MetaBlu,
              ),
          onClick = onFinishButtonPressed,
      ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = "Finish",
            modifier = Modifier.padding(Dimens.xSmall),
        )
      }
    } else {
      IconButton(
          modifier =
              Modifier.border(width = 1.dp, color = AppColor.MetaBlu, shape = CircleShape)
                  .size(Dimens.large),
          colors =
              IconButtonDefaults.iconButtonColors(
                  contentColor = AppColor.White,
              ),
          onClick = onNextButtonPressed,
      ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Go to next step",
            modifier = Modifier.padding(Dimens.xSmall),
        )
      }
    }
  }
}
