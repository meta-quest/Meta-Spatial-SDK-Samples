package com.meta.levinriegner.mediaview.app.shared.view.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens

@Composable
fun CloseButton(
    onPressed: () -> Unit,
) {
    IconButton(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = AppColor.White30,
                shape = CircleShape
            )
            .size(Dimens.large),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = AppColor.White,
        ),
        onClick = { onPressed() }
    ) {
        Icon(
            Icons.Filled.Close,
            contentDescription = "Close",
            modifier = Modifier
                .padding(6.dp)
        )
    }
}