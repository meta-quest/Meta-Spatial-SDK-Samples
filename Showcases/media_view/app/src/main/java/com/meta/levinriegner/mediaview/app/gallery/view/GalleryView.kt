// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.gallery.filter.titleResId
import com.meta.levinriegner.mediaview.app.onboarding.view.OnboardingButton
import com.meta.levinriegner.mediaview.app.shared.model.UiState
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import com.meta.levinriegner.mediaview.app.shared.view.ErrorView
import com.meta.levinriegner.mediaview.app.shared.view.LoadingView
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.model.MediaSortBy

@Composable
fun GalleryView(
    uiState: UiState<List<MediaModel>>,
    filter: MediaFilter,
    sortBy: MediaSortBy,
    showMetadata: Boolean,
    onRefresh: () -> Unit,
    onMediaSelected: (MediaModel) -> Unit,
    onSortBy: (MediaSortBy) -> Unit,
    onToggleMetadata: (Boolean) -> Unit,
    onOnboardingButtonPressed: () -> Unit,
) {
    MediaViewTheme {
        Scaffold(
            modifier =
            Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = AppColor.MetaBlu,
                    shape = RoundedCornerShape(Dimens.radiusMedium)
                )
                .clip(RoundedCornerShape(Dimens.radiusMedium))
        ) { innerPadding ->
            when (uiState) {
                UiState.Idle -> Box(Modifier)
                UiState.Loading -> LoadingView(modifier = Modifier.fillMaxSize())
                is UiState.Success ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppColor.BackgroundSweep),
                    ) {
                        Header(
                            filter = filter,
                            sortBy = sortBy,
                            onSortBy = onSortBy,
                            fileCount = uiState.data.size,
                            showMetadata = showMetadata,
                            onToggleMetadata = onToggleMetadata,
                            onOnboardingButtonPressed = onOnboardingButtonPressed,
                        )
                        MediaGrid(
                            media = uiState.data,
                            showMetadata = showMetadata,
                            modifier = Modifier.padding(innerPadding),
                            onItemClicked = onMediaSelected,
                        )
                    }

                is UiState.Error ->
                    ErrorView(
                        modifier = Modifier.fillMaxSize(),
                        description = uiState.message,
                        onActionButtonPressed = onRefresh,
                    )
            }
        }
    }
}

@Composable
private fun Header(
    filter: MediaFilter,
    sortBy: MediaSortBy,
    onSortBy: (MediaSortBy) -> Unit,
    fileCount: Int,
    showMetadata: Boolean,
    onToggleMetadata: (Boolean) -> Unit,
    onOnboardingButtonPressed: () -> Unit,
) {
    var sortExpanded by remember { mutableStateOf(false) }
    Column {
        Box(modifier = Modifier.padding(Dimens.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(filter.titleResId()),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.size(Dimens.xSmall))
                    Text(
                        text = stringResource(R.string.viewing_n_files, fileCount),
                        style = MaterialTheme.typography.bodySmall.copy(color = AppColor.White60),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    OnboardingButton(
                        onPressed = onOnboardingButtonPressed
                    )
                    Box(modifier = Modifier.size(Dimens.medium))

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier) {
                        OutlinedButton(
                            onClick = { sortExpanded = true },
                            modifier = Modifier
                                .height(40.dp)
                                .width(90.dp),
                            contentPadding = PaddingValues(start = 30.dp, end = 0.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors =
                            ButtonDefaults.buttonColors(
                                contentColor = AppColor.White,
                                containerColor = Color.Transparent,
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Absolute.Left,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_sortby),
                                    contentDescription = "Button Icon",
                                    modifier = Modifier
                                        .size(20.dp)
                                        .offset(x = (-20).dp, y = 0.dp)
                                )
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(x = (-17).dp, y = 0.dp),
                                    text = stringResource(id = R.string.sort_by),
                                    // style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Left,
                                )
                            }
                        }

                        MaterialTheme(
                            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                        ) {
                            DropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = { sortExpanded = false },
                                modifier =
                                Modifier
                                    .shadow(2.dp)
                                    .border(1.dp, AppColor.MetaBlu, RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(AppColor.GradientStart, AppColor.GradientEnd)
                                        )
                                    ),
                            ) {
                                for (option in MediaSortBy.entries) {
                                    DropdownMenuItem(
                                        contentPadding = PaddingValues(10.dp),
                                        trailingIcon =
                                        if (option == sortBy)
                                            ({
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            })
                                        else null,
                                        text = {
                                            Text(
                                                fontSize = 10.sp,
                                                text =
                                                stringResource(
                                                    when (option) {
                                                        MediaSortBy.DateDesc -> R.string.sort_by_date_desc
                                                        MediaSortBy.DateAsc -> R.string.sort_by_date_asc
                                                        MediaSortBy.SizeAsc -> R.string.sort_by_size_asc
                                                        MediaSortBy.SizeDesc -> R.string.sort_by_size_desc
                                                        MediaSortBy.NameAsc -> R.string.sort_by_name_asc
                                                        MediaSortBy.NameDesc -> R.string.sort_by_name_desc
                                                    }
                                                )
                                            )
                                        },
                                        onClick = {
                                            sortExpanded = false
                                            onSortBy(option)
                                        },
                                    )
                                    if (option != MediaSortBy.NameDesc)
                                        HorizontalDivider(
                                            color = AppColor.White15,
                                            thickness = 1.dp
                                        )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.size(Dimens.medium))
                    Switch(
                        thumbContent = {
                            Icon(
                                Icons.Sharp.Info,
                                "Toggle media info",
                            )
                        },
                        colors = SwitchDefaults.colors().copy(
                            uncheckedThumbColor = AppColor.White60,
                            uncheckedBorderColor = Color.Transparent,
                            uncheckedTrackColor = AppColor.White15,
                            checkedThumbColor = Color.White,
                            checkedBorderColor = Color.Transparent,
                            checkedTrackColor = AppColor.White15,
                            checkedIconColor = AppColor.MetaBlu,
                        ),
                        checked = showMetadata,
                        onCheckedChange = {
                            onToggleMetadata(it)
                        })
                }
            }
        }
        HorizontalDivider(color = AppColor.White15, thickness = 1.dp)
    }
}

@Composable
private fun MediaGrid(
    media: List<MediaModel>,
    modifier: Modifier = Modifier,
    showMetadata: Boolean,
    onItemClicked: (MediaModel) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        contentPadding = PaddingValues(Dimens.large),
        verticalArrangement = Arrangement.spacedBy(Dimens.small),
        horizontalArrangement = Arrangement.spacedBy(Dimens.small),
        columns = GridCells.Adaptive(Dimens.galleryItemSize)
    ) {
        items(media.size) { index ->
            MediaItemView(
                media[index],
                showMetadata,
                onItemClicked = onItemClicked,
            )
        }
    }
}