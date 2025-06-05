package com.meta.pixelandtexel.geovoyage.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.util.extension.scaleOnPressed

@Composable
fun QuizButton(
    label: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    hasSelectedAnswer: Boolean = false,
    didAnswerCorrectly: Boolean = false,
    isCorrectAnswer: Boolean = false,
) {
  val backgroundColor =
      if (hasSelectedAnswer) {
        if (isCorrectAnswer) {
          colorResource(R.color.quiz_correct_green)
        } else if (!didAnswerCorrectly) {
          colorResource(R.color.quiz_incorrect_red)
        } else LocalColorScheme.current.primaryButton
      } else LocalColorScheme.current.primaryButton

  val interactionSource = remember { MutableInteractionSource() }
  val horizontalPadding = dimensionResource(com.meta.spatial.uiset.R.dimen.spacing_24)

  val regularButtonModifier =
      Modifier.height(dimensionResource(com.meta.spatial.uiset.R.dimen.button_height))
  val buttonModifier =
      if (hasSelectedAnswer && didAnswerCorrectly && !isCorrectAnswer) {
        regularButtonModifier.then(Modifier.alpha(0.35f))
      } else regularButtonModifier

  return Box(
      modifier =
          Modifier.scaleOnPressed(interactionSource = interactionSource)
              .then(buttonModifier)
              .then(Modifier.fillMaxWidth())
              .then(
                  Modifier.background(
                          color = backgroundColor,
                          shape = LocalShapes.current.large,
                      )
                      .clip(LocalShapes.current.large)
                      .semantics { role = Role.Button }
                      .hoverable(interactionSource = interactionSource)
                      .clickable(
                          enabled = isEnabled,
                          onClick = onClick,
                          role = Role.Button,
                          indication = LocalIndication.current,
                          interactionSource = interactionSource,
                      ),
              ),
      contentAlignment = Alignment.Center,
  ) {
    Row(
        modifier = Modifier.padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
      Text(
          text = label,
          color = Color.White,
          style = LocalTypography.current.body1,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewQuizButtonUnanswered() {
  GeoVoyageTheme { QuizButton(label = "Primary Button", onClick = {}) }
}

@Preview
@Composable
private fun PreviewQuizButtonAnsweredThisCorrectly() {
  GeoVoyageTheme {
    QuizButton(
        label = "Primary Button",
        onClick = {},
        hasSelectedAnswer = true,
        didAnswerCorrectly = true,
        isCorrectAnswer = true)
  }
}

@Preview
@Composable
private fun PreviewQuizButtonAnsweredCorrectly() {
  GeoVoyageTheme {
    QuizButton(
        label = "Primary Button",
        onClick = {},
        hasSelectedAnswer = true,
        didAnswerCorrectly = true,
        isCorrectAnswer = false)
  }
}

@Preview
@Composable
private fun PreviewQuizButtonAnsweredIncorrectly() {
  GeoVoyageTheme {
    QuizButton(
        label = "Primary Button",
        onClick = {},
        hasSelectedAnswer = true,
        didAnswerCorrectly = false)
  }
}
