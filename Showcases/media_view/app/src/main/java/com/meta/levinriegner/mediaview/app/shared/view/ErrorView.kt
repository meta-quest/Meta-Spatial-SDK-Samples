// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.default_error_title),
    description: String = stringResource(id = R.string.default_error_description),
    actionButtonText: String = stringResource(id = R.string.refresh),
    onActionButtonPressed: () -> Unit,
) {
  Column(
      modifier = modifier.padding(Dimens.medium),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = title, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(Dimens.small))
    Text(
        text = description,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(modifier = Modifier.height(Dimens.large))
    Button(onClick = onActionButtonPressed) { Text(text = actionButtonText) }
  }
}
