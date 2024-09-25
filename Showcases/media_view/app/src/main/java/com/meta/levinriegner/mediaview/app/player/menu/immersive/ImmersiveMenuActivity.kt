// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.menu.immersive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImmersiveMenuActivity : ComponentActivity() {

  private val viewModel by viewModels<ImmersiveMenuViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    buildUi()
  }

  private fun buildUi() {
    setContent {
      MediaViewTheme {
        Row(
            modifier =
                Modifier.fillMaxSize()
                    .padding(15.dp)
                    .border(
                        width = 1.dp, color = AppColor.MetaBlu, shape = RoundedCornerShape(64.dp))
                    .clip(RoundedCornerShape(64.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(AppColor.GradientStart, AppColor.GradientEnd))),
            horizontalArrangement = Arrangement.SpaceBetween) {
              Column(
                  modifier = Modifier.align(Alignment.CenterVertically),
              ) {
                Text(
                    modifier = Modifier.offset(15.dp, 0.dp),
                    text = "You are in Immersive View",
                    color = AppColor.White)
              }

              Column(
                  modifier = Modifier.align(Alignment.CenterVertically),
              ) {
                OutlinedButton(
                    colors =
                        ButtonDefaults.buttonColors(
                            contentColor = AppColor.White,
                            containerColor = Color.Transparent,
                        ),
                    onClick = { viewModel.exitImmersiveMedia() }) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_minimize),
                            contentDescription = "Minimize")
                        Spacer(modifier = Modifier.size(Dimens.xSmall))
                        Text("Minimize")
                      }
                    }
              }
              Column(
                  modifier = Modifier.offset((-20).dp, 0.dp).align(Alignment.CenterVertically),
              ) {
                OutlinedButton(
                    colors =
                        ButtonDefaults.buttonColors(
                            contentColor = AppColor.White,
                            containerColor = Color.Transparent,
                        ),
                    onClick = { viewModel.closeImmersiveMedia() }) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_close),
                            contentDescription = "Close")
                        Spacer(modifier = Modifier.size(Dimens.xSmall))
                        Text("Close")
                      }
                    }
              }
            }
      }
    }
  }
}
