// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.BuildConfig
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.enums.LlamaServerType
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.components.settings.SettingsRadioButtonGroupListItem
import com.meta.pixelandtexel.geovoyage.ui.components.settings.SettingsSwitchListItem
import com.meta.pixelandtexel.geovoyage.ui.components.settings.SettingsTextFieldListItem
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

@Composable
fun SettingsScreen() {
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
    SecondaryPanel(
        modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.tall_panel_height))) {
          Column {
            Column(
                modifier =
                    Modifier.padding(12.dp)
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              SettingsSwitchListItem("Enable Wit.ai filtering", isWitAiFilteringEnabled) {
                SettingsService.set(SettingsKey.WIT_AI_FILTERING_ENABLED, it)
              }
              SettingsSwitchListItem("Detect speech ended", isSilenceDetectionEnabled) {
                SettingsService.set(SettingsKey.SILENCE_DETECTION_ENABLED, it)
              }
              SettingsRadioButtonGroupListItem(
                  "Llama server",
                  llamaServerSelectedIndex,
                  llamaServerOptions,
              ) {
                val newType =
                    LlamaServerType.fromValue(it)
                        ?: throw Exception("Invalid LlamaServerType from value $it")
                SettingsService.set(SettingsKey.LLAMA_SERVER_TYPE, newType.value)
              }
              if (llamaServerSelectedIndex.intValue == LlamaServerType.OLLAMA.value) {
                SettingsTextFieldListItem(
                    "Ollama server URL",
                    stringResource(R.string.placeholder_ollama_url),
                    textFieldValue,
                ) {
                  SettingsService.set(SettingsKey.OLLAMA_URL, it)
                }
              }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                  "Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                  color = Color.Black,
                  style = MaterialTheme.typography.bodyMedium,
              )
            }
          }
        }
  }
}

@Preview(widthDp = 570, heightDp = 480, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
fun SettingsScreenPreview() {
  GeoVoyageTheme { SettingsScreen() }
}
