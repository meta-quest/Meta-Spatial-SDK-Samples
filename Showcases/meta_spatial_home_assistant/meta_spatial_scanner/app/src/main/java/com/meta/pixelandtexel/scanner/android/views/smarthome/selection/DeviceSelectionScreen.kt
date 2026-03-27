package com.meta.pixelandtexel.scanner.android.views.smarthome.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun DeviceSelectionScreen(
    viewModel: DeviceSelectionViewModel,
    onOptionSelected: (Device) -> Unit
) {
    val options by viewModel.options.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOptions()
    }

    Card(
        modifier = Modifier.padding(MyPaddings.M),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(MyPaddings.L),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Which device do you want to pair?",
                style = MaterialTheme.typography.headlineSmall
            )

            if (options.isEmpty()) {
                CircularProgressIndicator()
            } else {
                options.forEach { option ->
                    Button(
                        onClick = { onOptionSelected(option) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = option.name)
                    }
                }
            }
        }
    }
}