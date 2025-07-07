// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialShapes
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.SpatialTypography

@Composable
fun FocusTheme(content: @Composable () -> Unit) {
    SpatialTheme(
        colorScheme = focusColorScheme(),
        typography = focusTypo(),
        shapes = SpatialShapes(),
        content = content)
}

@Composable
fun focusTypo(): SpatialTypography {

//    val focusLightTypo = SpatialTypography(
//        headline1 = LocalTypography.current.headline1Strong.copy(
//            color = LocalColorScheme.current.primaryAlphaBackground))
////        headline1 = style =
////            LocalTypography.current.headline1Strong.copy(
////                color = LocalColorScheme.current.primaryAlphaBackground))
//    )

    return LocalTypography.current
}

@Composable
fun focusColorScheme(): SpatialColorScheme {

    val focusLightColorScheme = SpatialColorScheme(
        panel = Brush.horizontalGradient( //TODO
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF2F1FF) 
            )
        ),
        dialog = LocalColorScheme.current.dialog,
        menu = LocalColorScheme.current.menu,
        sideNavBackground = LocalColorScheme.current.sideNavBackground,
        primaryButton = Color(0xFF6153FF),
        secondaryButton = Color(0xFFF1F0F3),
        negativeButton = LocalColorScheme.current.negativeButton,
        controlButton = LocalColorScheme.current.controlButton,
        secondaryControlButton = LocalColorScheme.current.secondaryControlButton,
        secondaryControlBackground = LocalColorScheme.current.secondaryControlBackground,
        primaryAlphaBackground = LocalColorScheme.current.primaryAlphaBackground,
        secondaryAlphaBackground = LocalColorScheme.current.secondaryAlphaBackground,
        placeholder = LocalColorScheme.current.placeholder,
        primaryOpaqueButton = LocalColorScheme.current.primaryOpaqueButton,
        secondaryOpaqueButton = LocalColorScheme.current.secondaryOpaqueButton,
        hover = LocalColorScheme.current.hover,
        pressed = LocalColorScheme.current.pressed,
        active = LocalColorScheme.current.active,
        positive = LocalColorScheme.current.positive,
        negative = LocalColorScheme.current.negative,
        progressBarOnMedia = LocalColorScheme.current.progressBarOnMedia,
        progressOnMedia = LocalColorScheme.current.progressOnMedia,
        progressBarOnBackground = LocalColorScheme.current.progressBarOnBackground,
        progressOnBackground = LocalColorScheme.current.progressOnBackground
    )

//    val uiMode = LocalConfiguration.current.uiMode
//    if ((uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
//        return darkSpatialColorScheme()
//    } else {
//        return darkSpatialColorScheme()
//    }

    return focusLightColorScheme
}
