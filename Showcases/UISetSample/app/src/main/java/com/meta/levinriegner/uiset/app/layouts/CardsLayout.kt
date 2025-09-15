// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.levinriegner.uiset.R
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.card.OutlinedCard
import com.meta.spatial.uiset.card.PrimaryCard
import com.meta.spatial.uiset.card.SecondaryCard
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CheckCircle
import com.meta.spatial.uiset.theme.icons.regular.Star

@Composable
fun CardsLayout() {
    PanelScaffold("Cards") {
        Column(
            Modifier.padding(24.dp),
        ) {
            // FIRST ROW
            Row {
                PrimaryCard(
                    modifier = Modifier.weight(1f),
                    onClick = { /* no-op */ },
                ) {
                    Text(
                        text = "Primary Card",
                        style = LocalTypography.current.body1Strong,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "This is a primary card with elevated appearance. Perfect for main content areas that need prominence.",
                        style = LocalTypography.current.body2,
                    )
                }
                Spacer(Modifier.width(76.dp))
                
                SecondaryCard(
                    modifier = Modifier.weight(1f),
                    onClick = { /* no-op */ },
                ) {
                    Text(
                        text = "Secondary Card",
                        style = LocalTypography.current.body1Strong
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "This is a secondary card with subtle background. Great for supporting content.",
                        style = LocalTypography.current.body2
                    )
                }
                Spacer(Modifier.width(76.dp))
                
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    onClick = { /* no-op */ },
                ) {
                    Text(
                        text = "Outlined Card",
                        style = LocalTypography.current.body1Strong
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "This is an outlined card with clear boundaries. Minimal visual treatment with border definition.",
                        style = LocalTypography.current.body2
                    )
                }
            }
            
            Spacer(Modifier.height(60.dp))

            // SECOND ROW
            Row {
                PrimaryCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = 0.dp,
                    onClick = { /* no-op */ },
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Image(
                            painterResource(R.drawable.sample_thumbnail),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Ocean Life",
                                style = LocalTypography.current.body1,
                                color = SpatialColor.white100
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Explore underwater scenes",
                                style = LocalTypography.current.body1,
                                color = SpatialColor.white90
                            )
                        }

                        Icon(
                            SpatialIcons.Regular.CheckCircle,
                            contentDescription = "",
                            tint = SpatialColor.white100,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.width(76.dp))

                SecondaryCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = 0.dp,
                    onClick = { /* no-op */ },
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Image(
                            painterResource(R.drawable.sample_thumbnail),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            SpatialColor.black60,
                                            SpatialColor.black100
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Ocean Life",
                                style = LocalTypography.current.body1,
                                color = SpatialColor.white100
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Explore underwater scenes",
                                style = LocalTypography.current.body1,
                                color = SpatialColor.white90
                            )
                        }
                        Icon(
                            SpatialIcons.Regular.CheckCircle,
                            contentDescription = "",
                            tint = SpatialColor.white100,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.width(76.dp))

                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = 0.dp,
                    onClick = { /* no-op */ },
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Image(
                            painterResource(R.drawable.sample_thumbnail),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            SpatialColor.black60,
                                            SpatialColor.black100
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                          Text(
                              text = "Ocean Life",
                              style = LocalTypography.current.body1,
                              color = SpatialColor.white100
                          )
                          Spacer(Modifier.height(4.dp))
                          Text(
                              text = "Explore underwater scenes",
                              style = LocalTypography.current.body1,
                              color = SpatialColor.white90
                          )
                        }
                        Icon(
                            SpatialIcons.Regular.CheckCircle,
                            contentDescription = "",
                            tint = SpatialColor.white100,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                        )
                        Image(
                            painterResource(R.drawable.sample_avatar),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(60.dp))
            
            // THIRD ROW
            Row {
                PrimaryCard(
                    modifier = Modifier.weight(1f),
                    onClick = { /* no-op */ },
                ) {
                    ProductCardContent(
                        title = "Ocean Life",
                        subtitle = "Explore underwater scenes",
                        price = "$1.99",
                        isPrimary = true
                    )
                }
                Spacer(Modifier.width(76.dp))
                
                SecondaryCard(
                    modifier = Modifier.weight(1f),
                    onClick = { /* no-op */ },
                ) {
                    ProductCardContent(
                        title = "Ocean Life",
                        subtitle = "Explore underwater scenes",
                        price = "$2.99"
                    )
                }
                Spacer(Modifier.width(76.dp))
                
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    onClick = { /* no-op */ },
                ) {
                    ProductCardContent(
                        title = "Ocean Life",
                        subtitle = "Explore underwater scenes",
                        price = "$3.99"
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCardContent(
    title: String,
    subtitle: String,
    price: String,
    isPrimary: Boolean = false,
) {
    val color = LocalContentColor.current
    
    // Media
    Box {
        Image(
            painterResource(R.drawable.sample_thumbnail),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        
        // Label
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(
                    color = SpatialColor.white90,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = "Label",
                style = LocalTypography.current.body2Strong,
                color = SpatialColor.RLDSBlack100,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        
        // Check icon
        Icon(
            SpatialIcons.Regular.CheckCircle,
            contentDescription = "",
            tint = SpatialColor.white100,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(32.dp)
        )
    }

    Spacer(Modifier.height(16.dp))

    // Info row: title/subtitle + price
    Row {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = LocalTypography.current.body1,
                color = color
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = LocalTypography.current.body2,
                color = color.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier
                .background(
                    color = if (isPrimary) SpatialColor.gray20 else SpatialColor.white20,
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Text(
                text = price,
                style = LocalTypography.current.body1,
                color = SpatialColor.RLDSBlack100,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                SpatialIcons.Regular.Star,
                contentDescription = "",
                modifier = Modifier.size(20.dp),
                tint = color
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "4.5",
                style = LocalTypography.current.body1,
                color = color
            )
            Text(
                text = " (99)",
                style = LocalTypography.current.body1,
                color = color.copy(alpha = 0.8f)
            )
        }
        Text(
            text = "$19.99",
            style = LocalTypography.current.body1.copy(textDecoration = TextDecoration.LineThrough),
            color = color.copy(alpha = 0.8f)
        )
        Spacer(Modifier.width(10.dp))
    }
}

@Preview(
    widthDp = 1363,
    heightDp = 1080,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun CardsLayoutPreview() {
    CardsLayout()
}
