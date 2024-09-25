// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    text: String,
    iconResId: Int? = null,
    onClick: () -> Unit,
) {
  OutlinedButton(
      onClick = onClick,
      modifier = modifier,
      colors =
          ButtonDefaults.buttonColors(
              contentColor = AppColor.White,
              containerColor = Color.Transparent,
          ),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.Left,
    ) {
      if (iconResId != null) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = "Button Icon",
            modifier = Modifier.size(80.dp))

        Spacer(modifier = Modifier.size(20.dp))
      }
      Text(
          text = text,
          // style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Left,
      )
    }
  }
}
