// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.BuildConfig
import com.meta.pixelandtexel.geovoyage.enums.LlamaServerType
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

@Composable
fun SettingsScreen() {
  val showDialog = remember { mutableStateOf(false) }
  val textFieldValue = remember { mutableStateOf(SettingsService.get(SettingsKey.OLLAMA_URL, "")) }

  val isWitAiFilteringEnabled = remember { mutableStateOf(true) }
  val isSilenceDetectionEnabled = remember { mutableStateOf(true) }
  val llamaServerSelectedIndex = remember { mutableIntStateOf(LlamaServerType.AWS_BEDROCK.value) }

  val llamaServerOptions = listOf("Ollama", "AWS Bedrock")

  LaunchedEffect(null) {
    isWitAiFilteringEnabled.value = SettingsService.get(SettingsKey.WIT_AI_FILTERING_ENABLED, true)
    isSilenceDetectionEnabled.value =
        SettingsService.get(SettingsKey.SILENCE_DETECTION_ENABLED, true)
    llamaServerSelectedIndex.intValue =
        SettingsService.get(SettingsKey.LLAMA_SERVER_TYPE, LlamaServerType.AWS_BEDROCK.value)
  }

  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
    SecondaryPanel(modifier = Modifier.width(610.dp).height(640.dp)) {
      Column() {
        if (showDialog.value) {
          AlertDialog(
              onDismissRequest = { showDialog.value = false },
              title = { Text(text = "Enter Text") },
              text = {
                TextField(
                    value = textFieldValue.value,
                    onValueChange = { textFieldValue.value = it },
                    modifier = Modifier.fillMaxWidth())
              },
              confirmButton = {
                TextButton(
                    onClick = {
                      SettingsService.set(SettingsKey.OLLAMA_URL, textFieldValue.value)
                      showDialog.value = false
                    }) {
                      Text("OK")
                    }
              },
              dismissButton = {
                TextButton(
                    onClick = {
                      textFieldValue.value = SettingsService.get(SettingsKey.OLLAMA_URL, "")
                      showDialog.value = false
                    }) {
                      Text("Cancel")
                    }
              })
        }

        Column(
            modifier =
                Modifier.padding(12.dp)
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
              SettingsToggleListItem("Enable Wit.ai filtering", isWitAiFilteringEnabled) {
                SettingsService.set(SettingsKey.WIT_AI_FILTERING_ENABLED, it)
              }
              SettingsToggleListItem("Detect speech ended", isSilenceDetectionEnabled) {
                SettingsService.set(SettingsKey.SILENCE_DETECTION_ENABLED, it)
              }
              SettingsSegmentedButtonListItem(
                  "Llama server", llamaServerSelectedIndex, llamaServerOptions) {
                    val newType =
                        LlamaServerType.fromValue(it)
                            ?: throw Exception("Invalid LlamaServerType from value $it")
                    SettingsService.set(SettingsKey.LLAMA_SERVER_TYPE, newType.value)
                  }
              if (llamaServerSelectedIndex.intValue == LlamaServerType.OLLAMA.value) {
                SettingsListItem("Ollama server", textFieldValue) { showDialog.value = true }
              }
            }
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = 11.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  "Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                  color = Color.Black,
                  style = MaterialTheme.typography.bodyMedium)
            }
      }
    }
  }
}

@Composable
fun SettingsToggleListItem(
    text: String,
    enabled: MutableState<Boolean>,
    onSettingChanged: (newValue: Boolean) -> Unit
) {
  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(48.dp).padding(12.dp, 0.dp).fillMaxSize()) {
        Text(
            text = text,
            color = Color.Black,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
        )
        Switch(
            checked = enabled.value,
            onCheckedChange = { checked ->
              enabled.value = checked
              onSettingChanged(checked)
            })
      }
  HorizontalDivider(color = Color.White)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSegmentedButtonListItem(
    headlineText: String,
    selectedIdx: MutableIntState,
    options: List<String>,
    onOptionSelected: (idx: Int) -> Unit
) {
  Column(
      verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp).fillMaxWidth()) {
      Text(
          text = headlineText,
          color = Color.Black,
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black))
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(12.dp, 0.dp).fillMaxWidth()) {
          SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, label ->
              SegmentedButton(
                  selected = index == selectedIdx.intValue,
                  onClick = {
                    selectedIdx.intValue = index
                    onOptionSelected(index)
                  },
                  shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
              ) {
                Text(text = label, style = MaterialTheme.typography.titleLarge)
              }
            }
          }
        }
  }
  HorizontalDivider(color = Color.White)
}

@Composable
fun SettingsListItem(
    headlineText: String,
    textFieldValue: MutableState<String>,
    onClicked: () -> Unit
) {
  ListItem(
      modifier = Modifier.height(64.dp).clickable { onClicked() },
      trailingContent = {
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Navigate",
            tint = Color.Black)
      },
      supportingContent = {
        Text(
            textFieldValue.value,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
        )
      },
      headlineContent = {
        Text(
            headlineText,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
        )
      })
  HorizontalDivider(color = Color.White)
}

@Preview(widthDp = 932, heightDp = 650, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
fun SettingsScreenPreview() {
  GeoVoyageTheme { SettingsScreen() }
}
