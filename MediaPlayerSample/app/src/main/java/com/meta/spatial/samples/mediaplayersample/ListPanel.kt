/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mediaplayersample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meta.spatial.toolkit.SpatialActivityManager
import kotlinx.coroutines.launch

data class MovieResponse(val results: List<Movie>)

data class Movie(
    val id: Int,
    val title: String,
    val poster_path: Int,
    val youtubeId: String = "",
    val overview: String = "",
)

data class VideoResponse(val id: Int, val results: List<Video>)

data class Video(val key: String, val type: String, val official: Boolean, val site: String)

class MovieViewModel : ViewModel() {

  private val _movies = mutableStateOf(listOf<Movie>())
  val movies: State<List<Movie>> = _movies

  private val _currentMovie = mutableStateOf<Movie?>(null)
  val currentMovie: State<Movie?> = _currentMovie

  init {
    _movies.value =
        listOf(
            Movie(
                id = -1,
                title = "Soloist 3d",
                poster_path = R.drawable.soloist_poster,
                overview = "Demonstration of stereoscopic video",
            ),
            Movie(
                id = 0,
                title = "Fitness on Meta Quest 3 | Not Moved",
                poster_path = R.drawable.movie00,
                youtubeId = "UDzllafQLGI",
            ),
            Movie(
                id = 1,
                title = "Qu3stions with NBA All-Star Tyrese Haliburton | Meta Quest 3",
                poster_path = R.drawable.movie01,
                youtubeId = "0qi6V-yOULg",
            ),
            Movie(
                id = 2,
                title = "This is Meta Quest 3",
                poster_path = R.drawable.movie02,
                youtubeId = "JlSMZg5cwKQ",
            ),
            Movie(
                id = 3,
                title = "Meta Quest 3 | Expand Your World | The Instrument",
                poster_path = R.drawable.movie03,
                youtubeId = "Exu7r2vZpcw",
            ),
            Movie(
                id = 4,
                title = "Introducing Meta Quest 3 | Coming This Fall",
                poster_path = R.drawable.movie04,
                youtubeId = "vMDIpFQYG4A",
            ),
            Movie(
                id = 5,
                title = "Introducing Meta Quest 3",
                poster_path = R.drawable.movie05,
                youtubeId = "5AKl_cEB26c",
            ),
            Movie(
                id = 6,
                title = "PianoVision Mixed Reality on Meta Quest 3",
                poster_path = R.drawable.movie06,
                youtubeId = "dGPPIF71FBo",
            ),
            Movie(
                id = 7,
                title = "Experience BAM, the ultimate battle game in mixed reality!",
                poster_path = R.drawable.movie07,
                youtubeId = "73lKUfuLw4A",
            ),
            Movie(
                id = 8,
                title =
                    "@VRwithJasmine gets her #MetaQuest3 play area ready to go Question is, what game to play first?",
                poster_path = R.drawable.movie08,
                youtubeId = "SBljI8B2zj0",
            ))
  }

  fun selectMovie(movie: Movie) {
    _currentMovie.value = movie
  }
}

@Composable
fun MovieApp(viewModel: MovieViewModel) {
  val navController = rememberNavController()
  val context = LocalContext.current

  Row(
      modifier =
          Modifier.fillMaxSize()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF0f0f0f))
              .padding(8.dp)) {
        NavHost(
            navController = navController,
            startDestination = "movieList",
            contentAlignment = Alignment.Center) {
              composable("movieList", enterTransition = null) {
                MovieListScreen(navController, viewModel)
              }
              composable("movieDetail", enterTransition = null) {
                MovieDetailScreen(navController, viewModel)
              }
            }
      }
}

@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieViewModel,
) {
  val movies by viewModel.movies

  Column() {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
      items(movies) { movie ->
        MovieListItem(movie = movie) {
          viewModel.selectMovie(it)
          navController.navigate("movieDetail")
        }
      }
    }
  }
}

@Composable
fun ImageItem(
    painter: Painter,
    onClick: () -> Unit,
    perfx: Boolean = true,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isHovered by interactionSource.collectIsHoveredAsState()
  val brightness = 100f
  val colorMatrix =
      floatArrayOf(
          1f,
          0f,
          0f,
          0f,
          brightness,
          0f,
          1f,
          0f,
          0f,
          brightness,
          0f,
          0f,
          1f,
          0f,
          brightness,
          0f,
          0f,
          0f,
          1f,
          0f)
  Image(
      painter = painter,
      contentDescription = null,
      contentScale = ContentScale.FillBounds,
      colorFilter =
          if (isHovered && perfx) ColorFilter.colorMatrix(ColorMatrix(colorMatrix)) else null,
      modifier =
          Modifier.fillMaxWidth()
              .height(160.dp)
              .clickable(onClick = onClick)
              .hoverable(interactionSource = interactionSource))
}

@Composable
fun MovieListItem(movie: Movie, onMovieSelected: (Movie) -> Unit) {
  val context = LocalContext.current
  LaunchedEffect(true) {}
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(160.dp)
              .padding(start = 8.dp, end = 8.dp, bottom = 6.dp)
              .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)),
      contentAlignment = Alignment.BottomCenter) {
        ImageItem(
            painter = painterResource(movie.poster_path), onClick = { onMovieSelected(movie) })
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))),
            contentAlignment = Alignment.Center) {
              Text(
                  text = movie.title,
                  style = MaterialTheme.typography.body1,
                  color = Color.White,
                  minLines = 1,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                  textAlign = TextAlign.Center,
                  modifier =
                      Modifier.fillMaxWidth()
                          .align(Alignment.BottomCenter)
                          .padding(start = 8.dp, end = 8.dp, bottom = 8.dp))
            }
      }
}

@Composable
fun MovieDetailScreen(navController: NavController, viewModel: MovieViewModel) {
  val selectedMovie = viewModel.currentMovie.value
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val interactionSource = remember { MutableInteractionSource() }
  val isHovered by interactionSource.collectIsHoveredAsState()

  selectedMovie?.let { movie ->
    LaunchedEffect(true) {
      coroutineScope.launch {
        Log.i("movie", "${movie.title}")

        if (movie.id == -1) {
          SpatialActivityManager.executeOnVrActivity<MediaPlayerSampleActivity> { activity ->
            activity.playVideo(
                "android.resource://" + context.getPackageName() + "/" + R.raw.soloist)
          }
        } else {
          SpatialActivityManager.executeOnVrActivity<MediaPlayerSampleActivity> { activity ->
            activity.playVideo("https://www.youtube.com/embed/${movie.youtubeId}?autoplay=1")
          }
        }
      }
    }
    Scaffold(
        topBar = {
          IconButton(
              interactionSource = interactionSource,
              onClick = { navController.navigate("movieList") }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isHovered) Color.Gray else Color.White)
              }
        },
        backgroundColor = Color(0xFF1f1f1f)) {
          Column(
              modifier = Modifier.fillMaxSize().padding(start = 32.dp, end = 32.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(movie.poster_path), contentDescription = null)
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = movie.title,
                    minLines = 1,
                    style = MaterialTheme.typography.h5,
                    color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview,
                    minLines = 1,
                    style = MaterialTheme.typography.body1,
                    color = Color.White)
              }
        }
  }
      ?: run {
        Text(
            text = "No movie selected",
            minLines = 1,
            style = MaterialTheme.typography.h6,
            color = Color.White)
      }
}

class ListPanel : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val viewModel: MovieViewModel by viewModels()
    setContent { MovieApp(viewModel) }
  }
}
