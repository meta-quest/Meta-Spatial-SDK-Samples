package com.meta.pixelandtexel.scanner.android.views.components.smart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun TitleComposable(
    title: String,
    modifier: Modifier = Modifier,
    topButtonIcon: ImageVector,
    topButtonContentDescription: String = "Close Button",
    onClickTopButton: () -> Unit = { }
) {
    Column {
        Row(
            modifier = modifier
                .padding(MyPaddings.M)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = MyPaddings.S)
            )

            IconButton(
                onClick = onClickTopButton
            ) {
                Icon(
                    imageVector = topButtonIcon,
                    contentDescription = topButtonContentDescription,
                    tint = MaterialTheme.colorScheme.error
                )
            }

        }
        HorizontalDivider(
            thickness = 3.dp,
        )
    }
}

@Preview
@Composable
fun TitleRowPreview() {
    TitleComposable(
        title = "Smart Device",
        topButtonIcon = androidx.compose.material.icons.Icons.Default.Close
    )
}