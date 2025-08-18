// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.levinriegner.uiset.app.util.view.StatefulWrapper
import com.meta.spatial.uiset.input.SpatialTextField
import com.meta.spatial.uiset.input.foundation.FieldValidationState
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Basic
import com.meta.spatial.uiset.theme.icons.regular.CheckboxCircle
import kotlinx.coroutines.flow.flow

@Composable
fun BirdseyeTextField() {
  PanelScaffold("Text Field") {
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.background(SpatialTheme.colorScheme.hover).fillMaxWidth()) {
      Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "  Component",
            style =
                LocalTypography.current.headline1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
          Box(modifier = Modifier.width(300.dp)) {
            StatefulWrapper(initialValue = "") { value, onChanged ->
              SpatialTextField(
                  label = "Label",
                  placeholder = "Value",
                  leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
                  value = value,
                  onValueChange = onChanged,
                  autoValidate = false,
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(20.dp))
      }
      Spacer(modifier = Modifier.height(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        "  Component Definitions & Variations",
        style =
            LocalTypography.current.headline1Strong.copy(
                color = LocalColorScheme.current.primaryAlphaBackground),
    )
    Spacer(modifier = Modifier.height(40.dp))

    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "No Placeholder",
              value = value,
              onValueChange = onChanged,
              autoValidate = false,
          )
        }

        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Placeholder",
              placeholder = "Value",
              value = value,
              onValueChange = onChanged,
              autoValidate = false,
          )
        }

        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Icon",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              value = value,
              onValueChange = onChanged,
              autoValidate = false,
          )
        }
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Disabled",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              value = value,
              onValueChange = onChanged,
              enabled = false,
              autoValidate = false,
          )
        }
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Trailing Icon",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              trailingIcon = { Icon(SpatialIcons.Regular.CheckboxCircle, "") },
              value = value,
              onValueChange = onChanged,
              autoValidate = false,
          )
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Validation: Valid",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              value = value,
              onValueChange = onChanged,
              initialState = FieldValidationState.Valid,
              autoValidate = true,
              validationPredicate = { flow { FieldValidationState.Valid } },
          )
        }
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Validation: Invalid",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              value = value,
              onValueChange = onChanged,
              initialState = FieldValidationState.Invalid,
              autoValidate = true,
              validationPredicate = { flow { FieldValidationState.Invalid } },
          )
        }
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Validation: Validating",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              value = value,
              onValueChange = onChanged,
              initialState = FieldValidationState.Validating,
              autoValidate = true,
              validationPredicate = { flow { FieldValidationState.Invalid } },
          )
        }
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Helper Text",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              value = value,
              onValueChange = onChanged,
              autoValidate = false,
              helperText = "Helper",
          )
        }
      }
    }
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 2000,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeTextFieldPreview() {
  BirdseyeTextField()
}
