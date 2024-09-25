// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.filter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaFilterActivity : ComponentActivity() {

  private val viewModel: MediaFilterViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    buildUi()
  }

  private fun buildUi() {
    setContent {
      // Observables
      val filters = viewModel.filters.collectAsState()
      // UI
      MediaViewTheme {
        Scaffold(
            modifier =
                Modifier.fillMaxSize()
                    .border(
                        width = 4.dp, color = AppColor.MetaBlu, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))) { innerPadding ->
              Column(
                  modifier = Modifier.padding(innerPadding).background(AppColor.BackgroundSweep)) {
                    FilterList(
                        pickerFilter = filters.value,
                        onFilterSelected = { viewModel.onFilterSelected(it) },
                        onUpload = { viewModel.onUpload() })
                  }
            }
      }
    }
  }
}

@Composable
private fun FilterList(
    pickerFilter: List<UiMediaFilter>,
    onFilterSelected: (UiMediaFilter) -> Unit,
    onUpload: () -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 40.dp)) {
    LazyVerticalGrid(columns = GridCells.Fixed(1), modifier = Modifier) {
      items(pickerFilter.size) { index ->
        Button(
            modifier =
                Modifier.padding(horizontal = 10.dp, vertical = 20.dp).height(96.dp).fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        if (pickerFilter[index].isSelected) AppColor.ButtonSelect
                        else Color.Transparent,
                    contentColor = AppColor.White,
                ),
            onClick = { onFilterSelected(pickerFilter[index]) }) {
              IconTextLayoutForButton(
                  stringResource(pickerFilter[index].type.titleResId()),
                  pickerFilter[index].type.iconResId())
            }
      }
    }

    Row(
        modifier = Modifier.fillMaxWidth().weight(0.5F).background(Color.Transparent),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {}

    Row(verticalAlignment = Alignment.Bottom) {
      OutlinedButton(
          modifier =
              Modifier.padding(horizontal = 10.dp, vertical = 20.dp).height(96.dp).fillMaxWidth(),
          colors =
              ButtonDefaults.buttonColors(
                  contentColor = AppColor.White,
                  containerColor = Color.Transparent,
              ),
          onClick = { onUpload() }) {
            IconTextLayoutForButton("Download Media", R.drawable.icon_upload)
          }
    }
  }
}

// TODO: Make this more generic by accepting more parameters like font size/weight/alignment etc
@Composable
private fun IconTextLayoutForButton(
    textToDisplay: String,
    iconAsset: Int,
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Absolute.Left,
      modifier = Modifier.fillMaxWidth()
      // .fillMaxSize()
      ) {
        Icon(
            painter = painterResource(id = iconAsset),
            contentDescription = "Button Icon",
            modifier = Modifier.size(128.dp))

        Spacer(modifier = Modifier.width(40.dp))
        Text(
            text = textToDisplay,
            color = Color.White,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
        )
      }
}
