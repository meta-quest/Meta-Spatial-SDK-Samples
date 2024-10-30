// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.onboarding.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StepModel(
    val id: Long,
    val imageUri: Uri,
    val title: String,
    val description: String,
) : Parcelable