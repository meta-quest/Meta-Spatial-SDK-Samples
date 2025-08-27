// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.view

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import timber.log.Timber

@Composable
fun VideoView(
    uri: Uri,
    is360Video: Boolean,
) {
  // Config
  val autoPlay = false
  // Setup ExoPlayer
  val context = LocalContext.current
  val exoPlayer = remember { ExoPlayer.Builder(context).build() }
  val mediaSource = remember(uri) { MediaItem.fromUri(uri) }
  LaunchedEffect(mediaSource) {
    exoPlayer.setMediaItem(mediaSource)
    exoPlayer.prepare()
    exoPlayer.playWhenReady = autoPlay
  }
  // State
  var shouldShowControls by remember { mutableStateOf(!autoPlay) }
  var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
  var totalDuration by remember { mutableLongStateOf(0L) }
  var currentTime by remember { mutableLongStateOf(0L) }
  var bufferedPercentage by remember { mutableIntStateOf(0) }
  var playbackState by remember { mutableIntStateOf(exoPlayer.playbackState) }
  val playerListener =
      object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
          super.onEvents(player, events)
          totalDuration = player.duration.coerceAtLeast(0L)
          currentTime = player.currentPosition.coerceAtLeast(0L)
          bufferedPercentage = player.bufferedPercentage
          isPlaying = player.isPlaying
          playbackState = player.playbackState
        }
      }
  // Current position
  LaunchedEffect(exoPlayer, isPlaying) {
    while (isActive && isPlaying) {
      currentTime = exoPlayer.currentPosition.coerceAtLeast(0L)
      delay(50)
    }
  }
  // Auto show/hide controls
  LaunchedEffect(shouldShowControls, isPlaying) {
    if (!isPlaying) {
      shouldShowControls = true
    } else if (shouldShowControls) {
      delay(3000)
      shouldShowControls = false
    }
  }
  // Dispose
  DisposableEffect(Unit) {
    exoPlayer.addListener(playerListener)
    onDispose {
      exoPlayer.removeListener(playerListener)
      exoPlayer.release()
    }
  }

  Box(Modifier.background(color = Color.Black)) {
    AndroidView(
        factory = { ctx ->
          PlayerView(ctx).apply {
            player = exoPlayer
            useController = false
            layoutParams =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
          }
        },
        modifier = Modifier.fillMaxSize().clickable { shouldShowControls = !shouldShowControls },
    )
    PlayerControls(
        modifier = Modifier.fillMaxSize(),
        isVisible = { shouldShowControls },
        isPlaying = { isPlaying },
        title = { exoPlayer.mediaMetadata.displayTitle?.toString() },
        playbackState = { playbackState },
        onReplayClick = { exoPlayer.seekBack() },
        onForwardClick = { exoPlayer.seekForward() },
        onPauseToggle = {
          when {
            exoPlayer.isPlaying -> {
              // pause the video
              Timber.i("Pause pressed")
              exoPlayer.pause()
            }

            !exoPlayer.isPlaying && playbackState == STATE_ENDED -> {
              Timber.i("Replay pressed")
              exoPlayer.seekTo(0)
              exoPlayer.playWhenReady = true
              shouldShowControls = false
            }

            else -> {
              // play the video
              // it's already paused
              Timber.i("Play pressed")
              exoPlayer.play()
              shouldShowControls = false
            }
          }
          isPlaying = !isPlaying
        },
        totalDuration = { totalDuration },
        currentTime = { currentTime },
        bufferedPercentage = { bufferedPercentage },
        onSeekChanged = { timeMs: Float ->
          exoPlayer.seekTo(timeMs.toLong())
          exoPlayer.playWhenReady = true
        },
        is360Video = is360Video,
    )
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerControls(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    title: () -> String?,
    onReplayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onPauseToggle: () -> Unit,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    playbackState: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    is360Video: Boolean,
) {

  val visible = remember(isVisible()) { isVisible() }

  AnimatedVisibility(modifier = modifier, visible = visible, enter = fadeIn(), exit = fadeOut()) {
    Column(
        modifier = Modifier.background(Color.Black.copy(alpha = 0.6f)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      TopControl(modifier = Modifier.fillMaxWidth(), title = title)

      Box(
          modifier = Modifier.fillMaxWidth().weight(1f).clickable { onPauseToggle() },
          contentAlignment = Alignment.Center,
      ) {
        CenterControls(
            modifier = if (is360Video) Modifier.size(64.dp) else Modifier,
            isPlaying = isPlaying,
            playbackState = playbackState,
        )
      }

      BottomControls(
          modifier =
              Modifier.fillMaxWidth(fraction = if (is360Video) 0.50f else 0.95f)
                  .animateEnterExit(
                      enter = slideInVertically(initialOffsetY = { fullHeight: Int -> fullHeight }),
                      exit = slideOutVertically(targetOffsetY = { fullHeight: Int -> fullHeight }),
                  ),
          totalDuration = totalDuration,
          currentTime = currentTime,
          bufferedPercentage = bufferedPercentage,
          onSeekChanged = onSeekChanged,
      )
    }
  }
}

@Composable
private fun TopControl(modifier: Modifier = Modifier, title: () -> String?) {
  val videoTitle = remember(title()) { title() }
  if (videoTitle == null) return
  Text(
      modifier = modifier.padding(16.dp).fillMaxWidth(),
      textAlign = TextAlign.Center,
      text = videoTitle,
      style = MaterialTheme.typography.titleMedium,
      color = AppColor.White60,
  )
}

@Composable
private fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    playbackState: () -> Int,
) {
  val isVideoPlaying = remember(isPlaying()) { isPlaying() }

  val playerState = remember(playbackState()) { playbackState() }
  if (playerState == STATE_BUFFERING) return

  Icon(
      when {
        isVideoPlaying -> {
          Icons.Default.Pause
        }

        !isVideoPlaying && playerState == STATE_ENDED -> {
          Icons.Default.Replay
        }

        else -> {
          Icons.Default.PlayArrow
        }
      },
      modifier = Modifier.size(40.dp),
      tint = AppColor.White,
      contentDescription = "Play/Pause",
  )
}

@Composable
private fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
) {

  val duration = remember(totalDuration()) { totalDuration() }

  val videoTime = remember(currentTime()) { currentTime() }

  val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

  Column(modifier = modifier.padding(bottom = 32.dp)) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Slider(
          value = buffer.toFloat(),
          enabled = false,
          onValueChange = { /*do nothing*/ },
          valueRange = 0f..100f,
          colors =
              SliderDefaults.colors(
                  disabledThumbColor = Color.Transparent,
                  disabledActiveTrackColor = Color.Gray,
              ),
      )

      Slider(
          modifier = Modifier.fillMaxWidth(),
          value = videoTime.toFloat(),
          onValueChange = onSeekChanged,
          valueRange = 0f..duration.toFloat(),
          colors =
              SliderDefaults.colors(
                  thumbColor = AppColor.White,
                  activeTrackColor = AppColor.White,
                  inactiveTrackColor = AppColor.White30,
              ),
      )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
          modifier = Modifier.padding(horizontal = 16.dp),
          text = videoTime.coerceAtLeast(0L).formatMinSec(),
          color = AppColor.White,
      )

      Text(
          modifier = Modifier.padding(horizontal = 16.dp),
          text = duration.formatMinSec(),
          color = AppColor.White,
      )
    }
  }
}

fun Long.formatMinSec(): String {
  return String.format(
      Locale.getDefault(),
      "%02d:%02d",
      TimeUnit.MILLISECONDS.toMinutes(this),
      TimeUnit.MILLISECONDS.toSeconds(this) -
          TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this)),
  )
}
