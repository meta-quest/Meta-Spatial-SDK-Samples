// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.input.SpatialTextField
import com.meta.spatial.uiset.theme.SpatialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.meta.spatial.uiset.button.PrimaryIconButton
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.tooltip.SpatialTooltipContent
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.Message
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.WebView
import com.meta.theelectricfactory.focus.ui.focusColorScheme
import com.meta.theelectricfactory.focus.utils.focusDP
import com.meta.theelectricfactory.focus.ui.onestFontFamily
import com.meta.theelectricfactory.focus.ui.squareShapes
import com.meta.theelectricfactory.focus.ui.tooltipColor

@Composable
fun AIPanel() {

    var immersiveActivity = ImmersiveActivity.getInstance()
    var messageInput = remember { mutableStateOf("") }
    var messagesList = remember { mutableStateListOf<Message>() }
    var stickyAvailable = remember { mutableStateOf(false) }
    var sendButtonLoading = remember { mutableStateOf(false) }
    var sendButtonIcon = remember { mutableIntStateOf(R.drawable.send) }
    val currentProjectUuid by immersiveActivity?.focusViewModel!!.currentProjectUuid.collectAsState()

    LaunchedEffect(currentProjectUuid) {
        messagesList.clear()
    }

    // For loading icon animation
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        )
    )

    return FocusTheme {
        Column {
            Row (modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(50.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpatialTheme(
                    colorScheme = tooltipColor()
                ) {
                    SpatialTooltipContent(
                        modifier = Modifier
                            .clip(SpatialTheme.shapes.large),
                        title = "AI Exchange",
                    )
                }

                Box(modifier = Modifier
                    .height(40.dp)
                    .aspectRatio(1f)
                ) {
                    SecondaryCircleButton(
                        onClick = {
                            immersiveActivity?.ShowAIPanel(false)
                        },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.close),
                                contentDescription = "Close",
                                tint = Color.Unspecified
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.size(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(SpatialTheme.shapes.large)
                    .background(FocusColors.selectedLightPurple),
                contentAlignment = Alignment.BottomCenter
            ) {

                Column {
                    Box( modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.815f)
                        .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        LazyColumn(
                            reverseLayout = true,
                            contentPadding = PaddingValues(40.dp, 25.dp, 40.dp, 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(messagesList) { message ->
                                ChatMessageItem(
                                    message = message.text,
                                    isUser = message.isUser
                                )
                            }
                        }

                        if (messagesList.isEmpty()) AiDisclaimerText()

                        Box(
                            modifier = Modifier.padding(30.dp)
                        ) {
                            SpatialTheme(
                                shapes = squareShapes()
                            ) {
                                PrimaryButton(
                                    label = "Sticky note last message",
                                    leading = { Icon(
                                        painterResource(id = R.drawable.sticky_note_icon),
                                        contentDescription = "sticky",
                                        tint = Color.Unspecified
                                    )},
                                    onClick = {
                                        immersiveActivity?.summarize(immersiveActivity.lastAIResponse)
                                        stickyAvailable.value = false
                                    },
                                    isEnabled = stickyAvailable.value
                                )
                            }
                        }
                    }

                    Box( modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(FocusColors.panel)
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(40.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
                        ) {
                            SpatialTheme(
                                colorScheme = focusColorScheme(true)
                            ) {
                                SpatialTextField(
                                    modifier = Modifier
                                        .width(460.dp)
                                        .height(100.dp),
                                    label = "", //TODO !!!! !!! esto esta descentrando el input!
                                    placeholder = "Write a message",
                                    value = messageInput.value,
                                    onValueChange = { messageInput.value = it }
                                )
                            }

                            Box(modifier = Modifier
                                .height(70.dp)
                                .aspectRatio(1f)
                            ) {
                                SpatialTheme(
                                    shapes = squareShapes()
                                ) {
                                    PrimaryIconButton(
                                        icon = {
                                            Icon(
                                                painterResource(id = sendButtonIcon.intValue),
                                                contentDescription = "Send",
                                                tint = Color.Unspecified,
                                                modifier = if (sendButtonLoading.value) Modifier.graphicsLayer(
                                                    rotationZ = rotation
                                                ) else Modifier
                                            )
                                        },
                                        onClick = {
                                            if (messageInput.value != "") {
                                                messagesList.add(
                                                    0,
                                                    Message(messageInput.value, true)
                                                )
                                                immersiveActivity?.askToAI(messageInput.value, {
                                                    messagesList.add(
                                                        0,
                                                        Message(
                                                            immersiveActivity.lastAIResponse,
                                                            false
                                                        )
                                                    )
                                                    stickyAvailable.value = true
                                                    activeLoadingState(
                                                        false,
                                                        sendButtonLoading,
                                                        sendButtonIcon
                                                    )
                                                })
                                                messageInput.value = ""
                                                activeLoadingState(
                                                    true,
                                                    sendButtonLoading,
                                                    sendButtonIcon
                                                )
                                            }
                                        },
                                        isEnabled = !sendButtonLoading.value && messageInput.value.isNotEmpty(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiDisclaimerText() {
    val annotatedText = AnnotatedString.Builder().apply {
        append("This application uses generative AI to respond to queries, and those responses may be inaccurate or inappropriate. ")
        pushStringAnnotation(tag = "OpenBrowser", annotation = "")
        withStyle(style = SpanStyle(
            color = FocusColors.purpleStickyNote,
            textDecoration = TextDecoration.Underline
        )) {
            append("Learn More")
        }
        pop()
        append(". \n\n Your queries and the generative AI responses are not retained by Meta.")
    }.toAnnotatedString()

    Box(
        modifier = Modifier.padding(80.dp, 80.dp, 80.dp, 300.dp),
    ) {
        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "OpenBrowser", start = offset, end = offset)
                    .firstOrNull()?.let {
                        WebView("https://www.facebook.com/privacy/guide/genai/")
                    }
            },
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontFamily = onestFontFamily,
                color = FocusColors.disabledPurple
            ),
        )
    }
}

fun activeLoadingState(state: Boolean, sendButtonLoading: MutableState<Boolean>, sendButtonIcon: MutableIntState) {
    ImmersiveActivity.getInstance()?.waitingForAI = state
    sendButtonLoading.value = state
    sendButtonIcon.intValue = if (state) R.drawable.loading else R.drawable.send
}

@Composable
fun ChatMessageItem(
    message: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    val avatarSize = 60.dp
    val textColor =  if (isUser) FocusColors.darkBlue else FocusColors.darkPurple
    val messageColor = if (isUser) FocusColors.lightPurple else FocusColors.aiChat
    val avatarRes = if (isUser) R.drawable.user_avatar else R.drawable.ai_avatar

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "AI Avatar",
                modifier = Modifier
                    .size(avatarSize)
                    .padding(end = 10.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(500.dp)
                .background(
                    brush = SolidColor(messageColor),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = message,
                fontSize = 20.sp,
                lineHeight = 25.sp,
                color = textColor,
                fontFamily = onestFontFamily
            )
        }

        if (isUser) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(avatarSize)
                    .padding(start = 10.dp)
            )
        }
    }
}

@Preview(
    widthDp = (0.3f * focusDP).toInt(),
    heightDp = (0.5f * focusDP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun AIPanelPreview() {
    AIPanel()
}