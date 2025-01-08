package com.meta.levinriegner.mediaview.app.player.menu.immersive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens


@Composable
fun ImmersiveMenuView(
    state: ImmersiveMenuState,
    onMinimize: () -> Unit,
    onEnterEdit: () -> Unit,
    onExitEdit: () -> Unit,
    onSaveAsNewImage: () -> Unit,
) {
    return Row(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(15.dp)
            .border(
                width = 1.dp, color = AppColor.MetaBlu, shape = RoundedCornerShape(64.dp)
            )
            .clip(RoundedCornerShape(64.dp))
            .background(
                Brush.verticalGradient(
                    listOf(AppColor.GradientStart, AppColor.GradientEnd)
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        when (state) {
            is ImmersiveMenuState.Initial -> {
                Text(
                    modifier = Modifier
                        .padding(horizontal = Dimens.medium)
                        .weight(1f),
                    text = stringResource(R.string.immersive_mode_active_label),
                    textAlign = TextAlign.Center,
                    color = AppColor.White
                )
                Row {
                    if (state.canEdit)
                        OutlinedButton(
                            modifier = Modifier.padding(end = Dimens.medium),
                            colors =
                            ButtonDefaults.buttonColors(
                                contentColor = AppColor.White,
                                containerColor = Color.Transparent,
                            ),
                            border = BorderStroke(1.dp, AppColor.White30),
                            onClick = { onEnterEdit() }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Crop,
                                    contentDescription = "Crop"
                                )
                                Spacer(modifier = Modifier.size(Dimens.xSmall))
                                Text(stringResource(R.string.crop))
                            }
                        }
                    OutlinedButton(
                        modifier = Modifier.padding(end = Dimens.medium),
                        colors =
                        ButtonDefaults.buttonColors(
                            contentColor = AppColor.White,
                            containerColor = Color.Transparent,
                        ),
                        border = BorderStroke(1.dp, AppColor.White30),
                        onClick = { onMinimize() }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_minimize),
                                contentDescription = "Minimize"
                            )
                            Spacer(modifier = Modifier.size(Dimens.xSmall))
                            Text(stringResource(R.string.minimize))
                        }
                    }
                }

            }

            is ImmersiveMenuState.Editing -> {
                OutlinedIconButton(
                    modifier = Modifier
                        .padding(Dimens.medium)
                        .aspectRatio(1f),
                    colors =
                    IconButtonDefaults.iconButtonColors(
                        contentColor = AppColor.White,
                        containerColor = Color.Transparent,
                    ),
                    border = BorderStroke(1.dp, AppColor.White30),
                    onClick = { onExitEdit() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                ElevatedButton(
                    modifier = Modifier.padding(end = Dimens.medium),
                    colors =
                    ButtonDefaults.buttonColors(
                        contentColor = AppColor.White,
                        containerColor = AppColor.MetaBlu,
                    ),
                    onClick = { onSaveAsNewImage() }) {
                    Text(
                        stringResource(R.string.save_as_new_image)
                    )
                }
            }
        }
    }
}
