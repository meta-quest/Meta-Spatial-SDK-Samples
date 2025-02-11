// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select.delete_confirm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.data.gallery.model.MediaToDeleteModel

@Composable
fun MediaDeleteConfirmView(
  modifier: Modifier = Modifier,
  mediaToDelete: List<MediaToDeleteModel>,
  onConfirmed: () -> Unit,
  onCanceled: () -> Unit,
) {
  val isSingleFile = mediaToDelete.size == 1

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier.fillMaxSize()
          .padding(48.dp),
  ) {
    Icon(
        painter = painterResource(R.drawable.icon_delete),
        contentDescription = "Delete Icon",
        tint = Color.White,
        modifier = Modifier.size(72.dp),
    )
    Spacer(modifier = Modifier.height(Dimens.medium))
    Text(
        text = if (isSingleFile)
          stringResource(R.string.single_file_delete_confirmation, mediaToDelete.first().name)
        else
          stringResource(R.string.n_files_delete_confirmation, mediaToDelete.size),
        style = MaterialTheme.typography.titleLarge.copy(
            color = AppColor.White,
        ),
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(Dimens.medium))

    Text(
        text = pluralStringResource(R.plurals.n_files_delete_confirm_rationale, mediaToDelete.size),
        style = MaterialTheme.typography.titleMedium.copy(
            color = AppColor.White60,
        ),
        textAlign = TextAlign.Center,

        )

    Spacer(modifier = Modifier.height(Dimens.large))

    Button(
        onClick = { onConfirmed() },
        colors = ButtonDefaults.buttonColors(containerColor = AppColor.MetaBlu),
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
      Text(
          text = if (isSingleFile)
            stringResource(R.string.single_file_delete_confirmation_button)
          else
            stringResource(R.string.n_files_delete_confirmation_button, mediaToDelete.size),
          style = MaterialTheme.typography.titleMedium.copy(color = AppColor.White),
      )
    }

    Spacer(modifier = Modifier.height(Dimens.medium))

    OutlinedButton(
        onClick = { onCanceled() },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColor.White),
        border = BorderStroke(1.dp, AppColor.White),
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
      Text(text = stringResource(R.string.cancel), style = MaterialTheme.typography.titleMedium)
    }
  }
}
