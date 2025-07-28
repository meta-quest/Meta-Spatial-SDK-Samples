// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ui.getStickyColors
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.data.StickyColor
import com.meta.theelectricfactory.focus.utils.FOCUS_DP
import com.meta.theelectricfactory.focus.ui.focusFont

@Composable
fun StickyNotePanel(
    uuid: Int,
    message: String = "",
    color: StickyColor,
) {
    var messageInput = remember { mutableStateOf(message) }
    val (mainColor, lightColor) = getStickyColors(color)

    var textSize = 25.sp
    var lineHeight = 30.sp
    if (messageInput.value.length > 130) {
        textSize = 20.sp
        lineHeight = 22.sp
    }

    return FocusTheme {
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(LocalShapes.current.small)
                    .background(mainColor),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column {
                    Box( modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.17f)
                        .align(Alignment.CenterHorizontally)
                    )

                    Box( modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(lightColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(5.dp),
                        ) {
//                            SpatialTextField(
//                                modifier = Modifier
//                                    .fillMaxWidth(),
//                                label = "",
//                                placeholder = "Type something",
//                                value = "",
//                                onValueChange = { }
//                            )
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = messageInput.value,
                                onValueChange = { messageInput.value = it },
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        ImmersiveActivity.getInstance()!!.DB.updateStickyMessage(uuid, messageInput.value)
                                    }
                                ),
                                placeholder = {
                                    Text(
                                        text ="Type something...",
                                        fontFamily = focusFont,
                                        fontSize = textSize,
                                        lineHeight = lineHeight,
                                    )},
                                textStyle = TextStyle(
                                    fontSize = textSize,
                                    fontFamily = focusFont,
                                    lineHeight = lineHeight
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    widthDp = (0.14f * FOCUS_DP).toInt(),
    heightDp = (0.14f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun StickyNotePanelPreview() {
    val uuid = 0
    val message = "This is a sticky note"
    val color = StickyColor.Yellow
    StickyNotePanel(uuid, message, color)
}