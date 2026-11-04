package com.meta.pixelandtexel.scanner.android.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun Panel(
    outerPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .clip(LocalShapes.current.large)
                .background(
                    brush = LocalColorScheme.current.panel,
                    shape = LocalShapes.current.large
                )
                .padding(if (outerPadding) 20.dp else 0.dp)
    ) {
        content.invoke()
    }
}

@Preview(widthDp = 516, heightDp = 414)
@Composable
private fun PanelPreview() {
    SpatialTheme { Panel {} }
}
