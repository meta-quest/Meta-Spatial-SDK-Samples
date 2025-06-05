package com.meta.pixelandtexel.geovoyage.ui.components.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.input.SpatialTextField

@Composable
fun SettingsTextFieldListItem(
    label: String,
    placeholder: String,
    textFieldValue: MutableState<String>,
    onChanged: (String) -> Unit
) {
  Row(modifier = Modifier.padding(start = 24.dp, end = 12.dp).fillMaxWidth()) {
    SpatialTextField(
        label = label,
        value = textFieldValue.value,
        placeholder = placeholder,
        onValueChange = { textFieldValue.value = it },
        onSubmit = { onChanged(it) },
        singleLine = true,
        autoCorrectEnabled = false,
        autoValidate = false,
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Uri,
        modifier = Modifier.fillMaxWidth())
  }
  HorizontalDivider(color = Color.White)
}
