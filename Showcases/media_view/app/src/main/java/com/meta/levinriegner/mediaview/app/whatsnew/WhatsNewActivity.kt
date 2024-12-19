// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.whatsnew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.meta.levinriegner.mediaview.BuildConfig
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.Constants
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import com.meta.levinriegner.mediaview.app.shared.view.component.CloseButton
import com.meta.levinriegner.mediaview.app.shared.view.component.RoundedButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WhatsNewActivity : ComponentActivity() {
    private val viewModel: WhatsNewViewModel by viewModels()

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
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = AppColor.MetaBlu,
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        )
                        .clip(
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        )
                ) {
                    when (viewModel.areReleaseNotesEnabled.collectAsState().value) {
                        false -> Box(Modifier)
                        true -> {
                            val uriHandler = LocalUriHandler.current

                            val whatsNew = viewModel.releaseNotes.collectAsState().value
                            val isDontShowAgainChecked =
                                viewModel.isDontShowAgainChecked.collectAsState().value

                            Row(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = .30f)
                                        .fillMaxHeight()
                                        .background(AppColor.DarkBackgroundSweep)
                                        .padding(
                                            horizontal = Dimens.small,
                                        )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        Image(
                                            rememberAsyncImagePainter(
                                                R.drawable.logo
                                            ),
                                            "logo",
                                        )
                                        Box(
                                            modifier = Modifier.height(Dimens.small)
                                        )
                                        RoundedButton(
                                            onClick = { uriHandler.openUri(Constants.WEBSITE_URL) },
                                            title = "Visit Our Website"
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(AppColor.BackgroundSweep)
                                        .padding(Dimens.small)

                                ) {
                                    // Top bar with close button
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            "V ${BuildConfig.VERSION_NAME} Updates",
                                            color = AppColor.White,
                                            textAlign = TextAlign.Start,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        CloseButton(
                                            onPressed = { viewModel.close() }
                                        )

                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(Dimens.small)
                                    )

                                    HorizontalDivider()


                                    // Content
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(.80f)
                                    ) {
                                        LazyVerticalStaggeredGrid(
                                            columns = StaggeredGridCells.Fixed(2),
                                            horizontalArrangement = Arrangement.spacedBy(
                                                Dimens.small,
                                            ),
                                            verticalItemSpacing = Dimens.small,
                                            contentPadding = PaddingValues(
                                                vertical = Dimens.small,
                                            )
                                        ) {
                                            items(
                                                items = whatsNew
                                            ) { releaseNote ->
                                                Column {
                                                    Text(
                                                        releaseNote.title,
                                                        color = AppColor.White,
                                                        textAlign = TextAlign.Start,
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                    Text(
                                                        releaseNote.description,
                                                        color = AppColor.White60,
                                                        textAlign = TextAlign.Start,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontSize = 10.sp,
                                                    )
                                                }
                                            }
                                        }
                                    }


                                    HorizontalDivider()

                                    // Bottom bar with Checkbox
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Checkbox(
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = AppColor.MetaBlu,
                                                    uncheckedColor = AppColor.White,
                                                    checkmarkColor = AppColor.White,

                                                    ),
                                                onCheckedChange = {
                                                    if (it) {
                                                        viewModel.checkDontShowAgain()
                                                    } else {
                                                        viewModel.uncheckDontShowAgain()
                                                    }
                                                },
                                                checked = isDontShowAgainChecked
                                            )
                                            Text(
                                                "Don't Show Again",
                                                color = AppColor.White,
                                                textAlign = TextAlign.Start,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 10.sp,
                                            )
                                        }

                                        Box(
                                            modifier = Modifier.padding(
                                                horizontal = Dimens.xSmall,
                                            )
                                        )

                                        RoundedButton(
                                            onClick = { viewModel.close() },
                                            title = "Continue",
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
}