// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialShapes
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.SpatialTypography
import com.meta.spatial.uiset.theme.lightSpatialColorScheme
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object FocusColors {
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
                    Color(0xFF0FC71B)
                ),
        )

    val blue = Color(0xFF42A4FF)
    val darkBlue = Color(0xFF28226D)
    val disabledBlue = Color(0xFF004281)

    val purple = Color(0xFF6153FF)
    val darkPurple = Color(0xFF340087)
    val disabledPurple = Color(0xFFBFBAFF)
    val lightPurple= Color(0xFFDFDCFF)
    val selectedLightPurple = Color(0xFFEFEEFF)

    val black = Color(0xFF16161B)
    val darkGray = Color(0xFF434152)
    val lightGray = Color(0xFFF1F0F3)

    val lightBlue = Color(0xFFECF6FF)
    val lightBlue2 = Color(0xFFD9EDFF)
    val lightGreen = Color(0xFFDBFBF9)
    val aiPurple = Color(0xFFF4EEFF)
    val aiChat = Color(0xFFEADCFF)

    // Sticky Notes Colors
    val yellowStickyNote = Color(0xFFFFF874)
    val lightYellowStickyNote = Color(0xFFFFFDDC)
    val greenStickyNote = Color(0xFF8DFFD9)
    val lightGreenStickyNote = Color(0xFFE2FFF5)
    val pinkStickyNote = Color(0xFFFFA0C9)
    val lightPinkStickyNote = Color(0xFFFFDFED)
    val orangeStickyNote = Color(0xFFFFAA97)
    val lightOrangeStickyNote = Color(0xFFFFE7E1)
    val blueStickyNote = Color(0xFF8DCCFF)
    val lightBlueStickyNote = Color(0xFFDEF0FF)
    val purpleStickyNote = Color(0xFFB390FF)
    val lightPurpleStickyNote = Color(0xFFE9DFFF)
}

fun GetStickyColors(stickyColor: StickyColor): Pair<Color, Color> {
    return when (stickyColor) {
        StickyColor.Blue -> Pair(FocusColors.blueStickyNote, FocusColors.lightBlueStickyNote)
        StickyColor.Purple -> Pair(FocusColors.purpleStickyNote, FocusColors.lightPurpleStickyNote)
        StickyColor.Yellow -> Pair(FocusColors.yellowStickyNote, FocusColors.lightYellowStickyNote)
        StickyColor.Green -> Pair(FocusColors.greenStickyNote, FocusColors.lightGreenStickyNote)
        StickyColor.Pink -> Pair(FocusColors.pinkStickyNote, FocusColors.lightPinkStickyNote)
        StickyColor.Orange -> Pair(FocusColors.orangeStickyNote, FocusColors.lightOrangeStickyNote)
        else -> Pair(FocusColors.blueStickyNote, FocusColors.lightBlueStickyNote)
    }
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
        shapes = focusShapes(),
        content = content
    )
}

@Composable
fun squareShapes(): SpatialShapes {
    val shapes = SpatialShapes(
        large = RoundedCornerShape(15.dp),
    )
    return shapes
}

@Composable
fun focusShapes(): SpatialShapes {
    val focusShapes = SpatialShapes(
        small = RoundedCornerShape(15.dp), // SpatialTextField
        medium = RoundedCornerShape(24.dp), // TextTileButton
        large = RoundedCornerShape(30.dp), // Panels, Tooltips, Buttons
    )
    return focusShapes
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
            //color = FocusColors.textColor,
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
fun tooltipColor(blue: Boolean = false): SpatialColorScheme {

    var colorScheme = lightSpatialColorScheme()
        .copy(

            primaryAlphaBackground = if (blue) FocusColors.disabledBlue else FocusColors.darkPurple, // Text of tooltip
            menu =  if (blue) FocusColors.lightBlue2 else FocusColors.aiChat, // Background of tooltip
        )

    return colorScheme
}

@Composable
fun focusColorScheme(gray: Boolean = false): SpatialColorScheme {

    var focusLightColorScheme = lightSpatialColorScheme()
        .copy(
            primaryButton = FocusColors.purple,
            secondaryButton = FocusColors.lightPurple,
            panel = FocusColors.panel,
            dialog = FocusColors.dialog,
            //primaryAlphaBackground = FocusColors.purple,
        )

    if (gray) focusLightColorScheme = lightSpatialColorScheme()
        .copy(

            primaryButton = FocusColors.darkGray,
            secondaryButton = FocusColors.lightGray,
            secondaryAlphaBackground = FocusColors.darkGray, // EditTextField & TextTileButton label
            

//            panel = FocusColors.dialog,
//            dialog = FocusColors.dialog,
////            menu =  FocusColors.greenStickyNote,
//            sideNavBackground= FocusColors.greenStickyNote,
//            negativeButton = FocusColors.orangeStickyNote,
//            controlButton = FocusColors.orangeStickyNote,
//            secondaryControlButton = FocusColors.orangeStickyNote,
//            secondaryControlBackground = FocusColors.orangeStickyNote,
//            primaryAlphaBackground = FocusColors.darkPurple, // Texto de tooltip
//            menu =  FocusColors.aiChat,
//            placeholder =FocusColors.greenStickyNote,
//            primaryOpaqueButton = FocusColors.greenStickyNote,
//            secondaryOpaqueButton = FocusColors.greenStickyNote,
//            hover =FocusColors.greenStickyNote,
//            pressed = FocusColors.greenStickyNote,
//            active = FocusColors.greenStickyNote,
//            positive = FocusColors.greenStickyNote,
//            negative = FocusColors.greenStickyNote,
//            progressBarOnMedia = FocusColors.greenStickyNote,
//            progressOnMedia = FocusColors.greenStickyNote,
//            progressBarOnBackground = FocusColors.greenStickyNote,
//            progressOnBackground = FocusColors.greenStickyNote,

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