package com.meta.levinriegner.mediaview.app.shared.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun TouchDebugGridView(
    modifier: Modifier = Modifier,
    cellSize: Int = 10,
    count: Int = 100,
    spacing: Int = 1,
) {
    val lowlightColor = Color.Red.copy(alpha = 0.3f)
    val highlightColor = Color.Green
    val touchedIndexes = remember { mutableStateListOf<String>() }
    return Box(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, highlightColor)
    ) {
        Column(modifier = Modifier.height(cellSize.dp * count)) {
            for (i in 0 until count) {
                Row(modifier = Modifier.width(cellSize.dp * count)) {
                    for (j in 0 until count) {
                        val index = "$i-$j"
                        Box(modifier = Modifier
                            .size(cellSize.dp)
                            .padding(end = spacing.dp, bottom = spacing.dp)
                            .background(
                                if (index in touchedIndexes) highlightColor else lowlightColor
                            )
                            .clickable {
                                Timber.i("Touched $index")
                                touchedIndexes.add(index)
                            })
                    }
                }
            }
        }

    }
}