// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.askearth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.geovoyage.ui.components.ScrollableTextAreaWithScrollBar
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

/**
 * Displays successful results of a query.
 *
 * @param result Result returned in response to the user-input question.
 */
@Composable
fun SuccessScreen(result: String) {
  SecondaryPanel { ScrollableTextAreaWithScrollBar(text = result) }
}

@Preview(
    widthDp = 932,
    heightDp = 650,
)
@Composable
private fun SuccessScreenPreview() {
  GeoVoyageTheme {
    SuccessScreen(
        result =
            "**The Oldest Rainforest on Earth**\n======================================================\nThe oldest rainforest on earth is the **Daintree Rainforest**, located in Queensland, Australia. It is estimated to be around **180 million years old**, dating back to the Jurassic period. This ancient rainforest has been continuously growing for over 180 million years, making it the oldest continuously surviving rainforest on the planet.\nThe Daintree Rainforest is a UNESCO World Heritage Site and is home to an incredible array of plant and animal species, many of which are found nowhere else on earth. It is a true natural wonder and a testament to the incredible resilience and adaptability of life on our planet.",
    )
  }
}
