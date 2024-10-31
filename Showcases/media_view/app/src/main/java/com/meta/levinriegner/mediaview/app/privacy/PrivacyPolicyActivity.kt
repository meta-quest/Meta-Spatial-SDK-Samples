// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.privacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyPolicyActivity : ComponentActivity() {

    private val viewModel by viewModels<PrivacyPolicyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.onCreate()
        buildUi()
    }

    private fun buildUi() {
        setContent {
            MediaViewTheme {
                Box(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .border(1.dp, AppColor.MetaBlu, RoundedCornerShape(Dimens.radiusMedium))
                        .clip(RoundedCornerShape(Dimens.radiusMedium))
                ) {
                    PrivacyPolicyView(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppColor.BackgroundSweep),
                        onAccepted = { viewModel.acceptPrivacyPolicy() },
                    )
                }
            }
        }
    }
}
