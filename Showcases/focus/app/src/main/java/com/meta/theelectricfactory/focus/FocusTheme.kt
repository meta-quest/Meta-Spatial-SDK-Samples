// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialShapes
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.SpatialTypography
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp

object FocusColors {
    val primaryButton = Color(0xFF6153FF)
    val secondaryButton = Color(0xFFF1F0F3)
    val textColor = Color(0xFFFFFFFF)
    val panel =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF2F1FF)
                ),
        )
    val dialog =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF2F1FF)
                ),
        )

    // celeste fuerte #42A4FF
    // violeta tranqui #EFEEFF //paneles
    // violeta tranqui seleccionado #DFDCFF
    // violeta fuerte #6153FF
    // violeta fuerte deshabilitado #BFBAFF
    // gris tranqui #F1F0F3
    // texto ngro #16161B
    // texto gris fuerte #434152
    // texto gris subtitulo #A8A7B8
}

val onestFontFamily =
    FontFamily(
        Font(R.font.onest_regular, FontWeight.Normal),
        Font(R.font.onest_light, FontWeight.Light),
        Font(R.font.onest_extra_light, FontWeight.ExtraLight),
        Font(R.font.onest_medium, FontWeight.Medium),
        Font(R.font.onest_semi_bold, FontWeight.SemiBold),
        Font(R.font.onest_bold, FontWeight.Bold),
        Font(R.font.onest_extra_bold, FontWeight.ExtraBold),
        Font(R.font.onest_black, FontWeight.Black),
        Font(R.font.onest_thin, FontWeight.Thin),
    )

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

    val focusLightTypo = SpatialTypography(
        headline1 = LocalTypography.current.headline1.copy(
            fontFamily = onestFontFamily),
        headline2 = LocalTypography.current.headline2.copy(
            fontFamily = onestFontFamily),
        headline3 = LocalTypography.current.headline3.copy(
            fontFamily = onestFontFamily),
        body1 = LocalTypography.current.body1.copy( //primary button text
            fontFamily = onestFontFamily,
            fontSize = 20.sp),
        body2 = LocalTypography.current.body2.copy( //secondary label text
            fontFamily = onestFontFamily,
            fontSize = 20.sp),
        headline1Strong = LocalTypography.current.headline1.copy(
            fontFamily = onestFontFamily),
        headline2Strong = LocalTypography.current.headline2.copy(
            fontFamily = onestFontFamily),
        headline3Strong = LocalTypography.current.headline3.copy(
            fontFamily = onestFontFamily),
        body1Strong = LocalTypography.current.body1.copy( //Primary label text
            fontFamily = onestFontFamily,
            fontSize = 20.sp),
        body2Strong = LocalTypography.current.body2.copy(
            fontFamily = onestFontFamily), //fontWeight = FontWeight.ExtraBold
    )

    return focusLightTypo
}

@Composable
fun focusColorScheme(): SpatialColorScheme {

    val focusLightColorScheme = lightSpatialColorScheme()
        .copy(
            primaryButton = FocusColors.primaryButton,
            secondaryButton = FocusColors.secondaryButton,
            panel = FocusColors.panel,
            dialog = FocusColors.dialog,
        )

//    val focusDarkColorScheme = darkSpatialColorScheme()
//
//    val uiMode = LocalConfiguration.current.uiMode
//    if ((uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
//        return focusDarkColorScheme()
//    } else {
//        return focusLightColorScheme()
//    }

    return focusLightColorScheme
}