/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.spatialvideosample

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.spatial.toolkit.SpatialActivityManager
import java.io.File
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG = "MoviePanel"
const val VIDEO_DIRECTORY = "/sdcard/Oculus/VideoShots/"

data class Movie(val id: Int, val uri: Uri, val title: String) {
  companion object {
    fun fromLocalVideo(id: Int, title: String) =
        Movie(
            id,
            Uri.parse("android.resource://" + SpatialVideoSampleActivity.appPackageName + "/" + id),
            title,
        )

    fun fromRawVideo(rawName: String, title: String): Movie? {
      val resId =
          SpatialVideoSampleActivity.appContext.resources.getIdentifier(
              rawName,
              "raw",
              SpatialVideoSampleActivity.appPackageName,
          )
      return if (resId != 0) fromLocalVideo(resId, title) else null
    }
  }
}

class MovieViewModel : ViewModel() {

  private val _movies = mutableStateOf(listOf<Movie>())
  val movies: State<List<Movie>> = _movies

  init {
    viewModelScope.launch {
      _movies.value =
          listOf(
                  Movie.fromRawVideo("doggie", "Doggie"),
                  Movie.fromRawVideo("mediagiant", "Media Giant"),
                  Movie.fromRawVideo("carousel", "Carousel"),
                  Movie.fromRawVideo("salmon", "Salmon"),
              )
              .filterNotNull()
      // Example of loading from a CDN url
      // Movie(
      //     123,
      //     Uri.parse(""),
      //     "CDN: Popcorn Video")
      // loadLocalVideos()
    }
  }

  fun selectMovie(movie: Movie) {
    SpatialActivityManager.executeOnVrActivity<SpatialVideoSampleActivity> { activity ->
      activity.setVideo(movie.uri)
      activity.playVideo()
    }
  }

  fun nextVideo(currentUri: Uri) {
    val index: Int? =
        movies.value.indexOfFirst { it.uri == currentUri }?.let { (it + 1) % movies.value.size }
    selectMovie(movies.value[index ?: 0])
  }

  fun previousVideo(currentUri: Uri) {
    val index: Int? =
        movies.value
            .indexOfFirst { it.uri == currentUri }
            ?.let { (it - 1 + movies.value.size) % movies.value.size }
    selectMovie(movies.value[index ?: 0])
  }

  fun loadLocalVideos() {
    viewModelScope.launch {
      val localVideos =
          File(VIDEO_DIRECTORY)
              .listFiles()
              ?.filter {
                // only show certain files to filter down SD card
                it.isFile && it.name.lowercase(Locale.getDefault()).contains("spatial_video")
              }
              ?.map { Movie(it.hashCode(), Uri.fromFile(it), it.nameWithoutExtension) }
              ?: emptyList()
      _movies.value = _movies.value + localVideos
    }
  }
}

@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
) {
  val movies by viewModel.movies
  Column(
      modifier =
          Modifier.fillMaxSize()
              .graphicsLayer { alpha = 1.0f }
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF1C2E33).copy(alpha = 1.0f))
              .graphicsLayer { alpha = 1.0f }
              .padding(16.2.dp)) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Text(
              text = "Spatial Video Library",
              minLines = 1,
              fontFamily = FontFamily(Font(R.font.noto_sans_regular)),
              fontSize = 20.sp,
              lineHeight = 18.88.sp,
              fontWeight = FontWeight(700),
              color = Color(0xFFF0F0F0),
              textAlign = TextAlign.Start,
              modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
          )
        }
        LazyVerticalGrid(columns = GridCells.Fixed(1)) {
          items(movies) { movie -> MovieListItem(movie = movie) { viewModel.selectMovie(it) } }
        }
      }
}

/**
 * This component should be used for media files to generate a preview. It will crop the video in
 * half so it only shows one eye
 */
@Composable
fun VideoThumbnail(
    video: Uri,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
  val context = LocalContext.current
  val thumbnail = remember(video) { mutableStateOf<Bitmap?>(null) }
  LaunchedEffect(video) {
    withContext(Dispatchers.IO) {
      val retriever = MediaMetadataRetriever()
      try {
        if (video.scheme == "http" || video.scheme == "https") {
          Log.d(TAG, "video thumbnail for URI (${video})")
          retriever.setDataSource(video.toString(), HashMap())
        } else {
          retriever.setDataSource(context, video)
        }

        thumbnail.value = retriever.getFrameAtTime(0)
      } catch (e: IllegalArgumentException) {
        Log.e(TAG, "Unable to render video thumbnail for URI (${video})", e)
      } catch (e: SecurityException) {
        Log.e(TAG, "Unable to render video thumbnail for URI (${video})", e)
      } catch (e: RuntimeException) {
        Log.e(TAG, "Unable to render video thumbnail for URI (${video})", e)
      } finally {
        retriever.release()
      }
    }
  }
  thumbnail.value?.let {
    val imageBitmap: ImageBitmap =
        Bitmap.createBitmap(it, 0, 0, it.width / 2, it.height).asImageBitmap()
    Image(
        painter = BitmapPainter(imageBitmap),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier =
            modifier.clickable(onClick = onClick).graphicsLayer {
              // Set the scale of the images directly so they don't "double animate"
              scaleX = 1.0f
              scaleY = 1.0f
            },
    )
  }
}

@Composable
fun MovieListItem(movie: Movie, onMovieSelected: (Movie) -> Unit) {
  Column() {
    Box(
        modifier =
            Modifier.fillMaxWidth().height(150.dp).padding(8.dp).clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
      VideoThumbnail(movie.uri) { onMovieSelected(movie) }
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(start = (8.dp), bottom = (10.dp)),
        contentAlignment = Alignment.BottomStart,
    ) {
      Text(
          text = movie.title,
          minLines = 1,
          style =
              TextStyle(
                  fontSize = 14.sp,
                  lineHeight = 13.49.sp,
                  fontFamily = FontFamily(Font(R.font.noto_sans_regular)),
                  fontWeight = FontWeight(400),
                  color = Color(0xFFF0F0F0),
              ),
      )
    }
  }
}

class MoviePanel : ComponentActivity() {
  override fun onCreate(savedInstanceBundle: Bundle?) {
    super.onCreate(savedInstanceBundle)
    val viewModel: MovieViewModel by viewModels()
    MoviePanel.viewModel = viewModel
    setContent { MovieListScreen(viewModel) }
  }

  companion object {
    lateinit var viewModel: MovieViewModel
  }
}
