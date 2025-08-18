// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.rememberAsyncImagePainter
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.onboarding.view.OnboardingControls
import com.meta.levinriegner.mediaview.app.onboarding.view.OnboardingSlide
import com.meta.levinriegner.mediaview.app.onboarding.view.OnboardingVideo
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import com.meta.levinriegner.mediaview.app.shared.view.ErrorView
import com.meta.levinriegner.mediaview.app.shared.view.component.CloseButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// TODO: Consider a safe rememberPagerState replacement to avoid this experimental opt-in
@androidx.annotation.OptIn(UnstableApi::class)
@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
  private val viewModel: OnboardingViewModel by viewModels()

  private lateinit var pagerState: PagerState
  private lateinit var pagerCoroutineScope: CoroutineScope
  private lateinit var exoPlayer: ExoPlayer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    buildUi()
  }

  private fun buildUi() {
    setContent {
      // UI
      MediaViewTheme {
        Surface(
            modifier =
                Modifier.fillMaxSize()
                    .background(AppColor.BackgroundSweep)
                    .border(
                        width = 1.dp,
                        color = AppColor.MetaBlu,
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                    )
                    .clip(shape = RoundedCornerShape(Dimens.radiusMedium))) {
              Column(Modifier.fillMaxSize()) {
                // Content
                when (val state = viewModel.state.collectAsState().value) {
                  OnboardingState.Idle -> Box(Modifier)

                  is OnboardingState.OnboardingStarted -> {
                    val context = LocalContext.current

                    pagerState = rememberPagerState(pageCount = { state.steps.count() })
                    pagerCoroutineScope = rememberCoroutineScope()

                    exoPlayer = remember {
                      ExoPlayer.Builder(context).build().apply {
                        repeatMode = Player.REPEAT_MODE_ONE
                        playWhenReady = true
                      }
                    }

                    LaunchedEffect(pagerState.currentPage) {
                      val resourceId = state.steps[pagerState.currentPage].resourceId

                      val mediaItem =
                          MediaItem.fromUri(
                              Uri.Builder()
                                  .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                  .path(resourceId.toString())
                                  .build())

                      exoPlayer.setMediaItem(mediaItem)
                      exoPlayer.prepare()
                    }

                    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

                    Row(modifier = Modifier.fillMaxSize()) {
                      val currentStep = state.steps[pagerState.currentPage]

                      Crossfade(targetState = currentStep, label = currentStep.title) { step ->
                        if (step.isVideo) {
                          OnboardingVideo(
                              exoPlayer,
                              modifier = Modifier.fillMaxHeight().fillMaxWidth(.75f),
                          )
                        } else {
                          Image(
                              rememberAsyncImagePainter(
                                  step.resourceId,
                              ),
                              "${step.title} image",
                              modifier =
                                  Modifier.fillMaxWidth(.75f)
                                      .background(AppColor.DarkBackgroundSweep),
                          )
                        }
                      }

                      Column(
                          horizontalAlignment = Alignment.CenterHorizontally,
                          verticalArrangement = Arrangement.SpaceBetween,
                          modifier =
                              Modifier.fillMaxSize()
                                  .background(AppColor.BackgroundSweep)
                                  .padding(Dimens.small),
                      ) {

                        // Top bar with close button
                        Box(modifier = Modifier.align(Alignment.End)) {
                          CloseButton(
                              onPressed = {
                                pagerCoroutineScope.launch { pagerState.scrollToPage(0) }
                                viewModel.close()
                              })
                        }

                        val onFinishButtonPressed =
                            if (!pagerState.canScrollForward) {
                              {
                                pagerCoroutineScope.launch { pagerState.scrollToPage(0) }
                                viewModel.close()
                              }
                            } else {
                              null
                            }

                        HorizontalPager(userScrollEnabled = false, state = pagerState) { index ->
                          val step = state.steps[index]

                          OnboardingSlide(
                              title = step.title,
                              description = step.description,
                          )
                        }

                        OnboardingControls(
                            onPreviousButtonPressed = {
                              pagerCoroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                              }
                            },
                            onNextButtonPressed = {
                              pagerCoroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                              }
                            },
                            onFinishButtonPressed = onFinishButtonPressed,
                            currentStep = pagerState.currentPage + 1,
                            totalSteps = pagerState.pageCount,
                        )
                      }
                    }
                  }

                  is OnboardingState.Error -> {
                    ErrorView(
                        modifier = Modifier.fillMaxSize(),
                        description = state.reason,
                        actionButtonText = stringResource(id = R.string.close),
                        onActionButtonPressed = {},
                    )
                  }
                }
              }
            }
      }
    }
  }
}
