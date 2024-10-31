package com.meta.levinriegner.mediaview.app.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.Constants
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import timber.log.Timber


@OptIn(ExperimentalTextApi::class)
@Composable
fun PrivacyPolicyView(
    modifier: Modifier = Modifier,
    onAccepted: () -> Unit,
) {
    val annotatedString = buildAnnotatedString {
        val termsText = stringResource(id = R.string.privacy_policy_dialog_terms)
        val privacyText = stringResource(id = R.string.privacy_policy_dialog_privacy)
        val str = stringResource(
            id = R.string.privacy_policy_dialog_description,
            stringResource(R.string.privacy_policy_accept_button),
            termsText,
            privacyText
        )
        val termsRange =
            (str.indexOf(termsText)..(str.indexOf(termsText) + termsText.length))
        val privacyRange =
            (str.indexOf(privacyText)..(str.indexOf(privacyText) + privacyText.length))

        // Entire string
        append(str)
        // Terms
        addStyle(
            style = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            ),
            start = termsRange.first,
            end = termsRange.last,
        )
        addUrlAnnotation(
            UrlAnnotation(Constants.TERMS_AND_CONDITIONS_URL),
            start = termsRange.first,
            end = termsRange.last
        )

        // Privacy Policy
        addStyle(
            style = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            ),
            start = privacyRange.first,
            end = privacyRange.last,
        )
        addUrlAnnotation(
            UrlAnnotation(Constants.PRIVACY_POLICY_URL),
            start = privacyRange.first,
            end = privacyRange.last
        )
    }
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier.padding(Dimens.xLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.privacy_policy_dialog_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(color = AppColor.White),
        )
        Spacer(modifier = Modifier.height(Dimens.large))
        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodyLarge.copy(color = AppColor.White, textAlign = TextAlign.Center),
            onClick = {
                annotatedString
                    .getUrlAnnotations(it, it)
                    .firstOrNull()?.let { annotation ->
                        uriHandler.openUri(annotation.item.url)
                    }
            }
        )
        Spacer(modifier = Modifier.height(Dimens.large))
        OutlinedButton(
            onClick = onAccepted,
            colors =
            ButtonDefaults.buttonColors(
                contentColor = AppColor.White,
                containerColor = Color.Transparent,
            ),
        ) {
            Text(
                text = stringResource(id = R.string.privacy_policy_accept_button),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}