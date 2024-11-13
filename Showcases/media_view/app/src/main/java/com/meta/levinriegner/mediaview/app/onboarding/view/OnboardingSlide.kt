// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor

@Composable
fun OnboardingSlide(
    title: String,
    description: String,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(fraction = .85f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            title,
            color = AppColor.White,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            description,
            color = AppColor.White60,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
        )
    }
}