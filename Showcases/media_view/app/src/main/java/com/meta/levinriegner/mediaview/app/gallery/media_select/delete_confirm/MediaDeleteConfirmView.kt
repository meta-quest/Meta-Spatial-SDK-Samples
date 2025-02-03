// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select.delete_confirm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.immersive.ImmersiveActivity
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.SpatialActivityManager

@Composable
fun MediaDeleteConfirmView(
  modifier: Modifier = Modifier,
  onConfirmed: () -> Unit,
  onCanceled: () -> Unit,
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier
          .padding(24.dp)
  ) {
    Icon(
        painter = painterResource(R.drawable.icon_delete),
        contentDescription = "Delete Icon",
        tint = Color.White,
        modifier = Modifier.size(48.dp)
    )
    Spacer(modifier = Modifier.height(Dimens.small))
    Text(
        text = pluralStringResource(R.plurals.n_files_delete_confirmation, 1),
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(Dimens.xSmall))

    Text(
        text = stringResource(R.string.delete_confirm_rationale),
        color = Color.Gray,
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(Dimens.large))

    Button(
        onClick = { onConfirmed() },
        colors = ButtonDefaults.buttonColors(containerColor = AppColor.MetaBlu),
        modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = stringResource(R.string.delete_confirm_button), color = AppColor.White)
    }

    Spacer(modifier = Modifier.height(Dimens.xSmall))

    OutlinedButton(
        onClick = { onCanceled() },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColor.White),
        border = BorderStroke(1.dp, AppColor.White),
        modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = stringResource(R.string.cancel))
    }
  }
}
