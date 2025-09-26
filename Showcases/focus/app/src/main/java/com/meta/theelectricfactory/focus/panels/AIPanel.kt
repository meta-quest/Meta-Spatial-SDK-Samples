// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.button.PrimaryIconButton
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.tooltip.SpatialTooltipContent
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.managers.AIManager
import com.meta.theelectricfactory.focus.managers.PanelManager
import com.meta.theelectricfactory.focus.tools.WebView
import com.meta.theelectricfactory.focus.ui.FocusColorSchemes
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusShapes
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ui.focusColorScheme
import com.meta.theelectricfactory.focus.ui.focusFont
import com.meta.theelectricfactory.focus.ui.focusShapes
import com.meta.theelectricfactory.focus.utils.FOCUS_DP
import com.meta.theelectricfactory.focus.viewmodels.FocusViewModel

data class Message(val text: String, val isUser: Boolean)

@Composable
fun AIPanel() {

  var messageInput = remember { mutableStateOf("") }
  var messagesList = remember { mutableStateListOf<Message>() }
  var stickyAvailable = remember { mutableStateOf(false) }
  var sendButtonLoading = remember { mutableStateOf(false) }
  var sendButtonIcon = remember { mutableIntStateOf(R.drawable.send) }
  val currentProjectUuid by FocusViewModel.instance.currentProjectUuid.collectAsState()

  stickyAvailable.value = false

  // Message list is cleared when the project changes
  LaunchedEffect(currentProjectUuid) { messagesList.clear() }

  // For loading icon animation
  val infiniteTransition = rememberInfiniteTransition()
  val rotation by
      infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = 360f,
          animationSpec =
              infiniteRepeatable(animation = tween(durationMillis = 1000, easing = LinearEasing)),
      )

  return FocusTheme {
    Column {
      Row(
          modifier = Modifier.align(Alignment.CenterHorizontally).height(40.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        SpatialTheme(colorScheme = focusColorScheme(FocusColorSchemes.PurpleTooltip)) {
          SpatialTooltipContent(
              modifier = Modifier.clip(SpatialTheme.shapes.large),
              title = "AI Exchange",
          )
        }
        SecondaryCircleButton(
            onClick = { PanelManager.instance.showAIPanel(false) },
            icon = {
              Icon(
                  painterResource(id = R.drawable.close),
                  contentDescription = "Close",
                  tint = Color.Unspecified,
              )
            },
        )
      }

      Spacer(modifier = Modifier.size(10.dp))

      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .fillMaxHeight()
                  .clip(SpatialTheme.shapes.large)
                  .background(FocusColors.selectedLightPurple),
          contentAlignment = Alignment.BottomCenter,
      ) {
        Column {
          Box(
              modifier =
                  Modifier.fillMaxWidth().fillMaxHeight(0.815f).align(Alignment.CenterHorizontally),
              contentAlignment = Alignment.BottomCenter,
          ) {
            LazyColumn(
                reverseLayout = true,
                contentPadding = PaddingValues(40.dp, 25.dp, 40.dp, 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              items(messagesList) { message ->
                ChatMessageItem(message = message.text, isUser = message.isUser)
              }
            }

            if (messagesList.isEmpty()) AiDisclaimerText()

            Box(modifier = Modifier.padding(30.dp)) {
              SpatialTheme(shapes = focusShapes(FocusShapes.Squared)) {
                PrimaryButton(
                    label = "Sticky note last message",
                    leading = {
                      Icon(
                          painterResource(id = R.drawable.sticky_note_icon),
                          contentDescription = "sticky",
                          tint = Color.Unspecified,
                      )
                    },
                    onClick = {
                      AIManager.instance.summarize(AIManager.instance.lastAIResponse)
                      stickyAvailable.value = false
                    },
                    isEnabled = stickyAvailable.value,
                )
              }
            }
          }

          Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(FocusColors.panel)) {
            Row(
                modifier = Modifier.align(Alignment.TopCenter).padding(10.dp, 40.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            ) {
              SpatialTheme(colorScheme = focusColorScheme(FocusColorSchemes.Gray)) {
                TextField(
                    value = messageInput.value,
                    onValueChange = { messageInput.value = it },
                    singleLine = true,
                    modifier =
                        Modifier.width(380.dp)
                            .height(54.dp)
                            .background(
                                FocusColors.lightGray,
                                shape = focusShapes(FocusShapes.Squared).large,
                            ),
                    textStyle = TextStyle(fontFamily = focusFont, color = Color.Black),
                    placeholder = {
                      Text(
                          text = "Write a message",
                          fontFamily = focusFont,
                          color = FocusColors.gray,
                      )
                      LocalFocusManager.current.clearFocus()
                    },
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                              onSendMessage(
                                  messageInput,
                                  messagesList,
                                  sendButtonLoading,
                                  sendButtonIcon,
                                  stickyAvailable,
                              )
                            }
                        ),
                )
              }

              Box(modifier = Modifier.height(48.dp)) {
                SpatialTheme(shapes = focusShapes(FocusShapes.Squared)) {
                  PrimaryIconButton(
                      icon = {
                        Icon(
                            painterResource(id = sendButtonIcon.intValue),
                            contentDescription = "Send",
                            tint = Color.Unspecified,
                            modifier =
                                if (sendButtonLoading.value)
                                    Modifier.graphicsLayer(rotationZ = rotation)
                                else Modifier,
                        )
                      },
                      onClick = {
                        onSendMessage(
                            messageInput,
                            messagesList,
                            sendButtonLoading,
                            sendButtonIcon,
                            stickyAvailable,
                        )
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

fun activeLoadingState(
    state: Boolean,
    sendButtonLoading: MutableState<Boolean>,
    sendButtonIcon: MutableIntState,
) {
  AIManager.instance.waitingForAI = state
  sendButtonLoading.value = state
  sendButtonIcon.intValue = if (state) R.drawable.loading else R.drawable.send
}

fun onSendMessage(
    messageInput: MutableState<String>,
    messagesList: MutableList<Message>,
    sendButtonLoading: MutableState<Boolean>,
    sendButtonIcon: MutableIntState,
    stickyAvailable: MutableState<Boolean>,
) {
  if (messageInput.value.isNotEmpty()) {
    messagesList.add(0, Message(messageInput.value, true))
    AIManager.instance.askToAI(
        messageInput.value,
        {
          messagesList.add(0, Message(AIManager.instance.lastAIResponse, false))
          stickyAvailable.value = true
          activeLoadingState(false, sendButtonLoading, sendButtonIcon)
        },
    )
    messageInput.value = ""
    activeLoadingState(true, sendButtonLoading, sendButtonIcon)
  }
}

@Composable
fun AiDisclaimerText() {
  val annotatedText =
      AnnotatedString.Builder()
          .apply {
            append(
                "This application uses generative AI to respond to queries, and those responses may be inaccurate or inappropriate. "
            )
            pushStringAnnotation(tag = "OpenBrowser", annotation = "")
            withStyle(
                style =
                    SpanStyle(
                        color = FocusColors.purpleStickyNote,
                        textDecoration = TextDecoration.Underline,
                    )
            ) {
              append("Learn More")
            }
            pop()
            append(". \n\n Your queries and the generative AI responses are not retained by Meta.")
          }
          .toAnnotatedString()

  Box(
      modifier = Modifier.padding(100.dp, 90.dp, 100.dp, 230.dp),
  ) {
    ClickableText(
        text = annotatedText,
        onClick = { offset ->
          annotatedText
              .getStringAnnotations(tag = "OpenBrowser", start = offset, end = offset)
              .firstOrNull()
              ?.let { WebView("https://www.facebook.com/privacy/guide/genai/") }
        },
        style =
            TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontFamily = focusFont,
                color = FocusColors.disabledPurple,
            ),
    )
  }
}

@Composable
fun ChatMessageItem(message: String, isUser: Boolean, modifier: Modifier = Modifier) {
  val avatarSize = 60.dp
  val textColor = if (isUser) FocusColors.darkBlue else FocusColors.darkPurple
  val messageColor = if (isUser) FocusColors.lightPurple else FocusColors.aiChat
  val avatarRes = if (isUser) R.drawable.user_avatar else R.drawable.ai_avatar

  Row(
      modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.Center,
  ) {
    if (!isUser) {
      Image(
          painter = painterResource(id = avatarRes),
          contentDescription = "AI Avatar",
          modifier = Modifier.size(avatarSize).padding(end = 10.dp),
      )
    }

    Box(
        modifier =
            Modifier.width(400.dp)
                .background(brush = SolidColor(messageColor), shape = RoundedCornerShape(16.dp))
                .padding(20.dp),
        contentAlignment = Alignment.TopStart,
    ) {
      Text(
          text = message,
          fontSize = 18.sp,
          lineHeight = 23.sp,
          color = textColor,
          fontFamily = focusFont,
      )
    }

    if (isUser) {
      Image(
          painter = painterResource(id = avatarRes),
          contentDescription = "User Avatar",
          modifier = Modifier.size(avatarSize).padding(start = 10.dp),
      )
    }
  }
}

@Preview(
    widthDp = (0.3f * FOCUS_DP).toInt(),
    heightDp = (0.5f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun AIPanelPreview() {
  AIPanel()
}
