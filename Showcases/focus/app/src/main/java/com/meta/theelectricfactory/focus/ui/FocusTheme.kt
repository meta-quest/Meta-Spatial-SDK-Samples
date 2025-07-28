// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.ui

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
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.data.StickyColor

enum class FocusColorSchemes {
    Main, Gray, BlueTooltip, PurpleTooltip, Transparent
}

enum class FocusShapes {
    Main, Squared
}

val focusFont = FontFamily(
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

fun getStickyColors(stickyColor: StickyColor): Pair<Color, Color> {
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

@Composable
fun FocusTheme(content: @Composable () -> Unit) {
    SpatialTheme(
        colorScheme = focusColorScheme(FocusColorSchemes.Main),
        typography = focusTypo(),
        shapes = focusShapes(FocusShapes.Main),
        content = content
    )
}

@Composable
fun focusColorScheme(mode: FocusColorSchemes = FocusColorSchemes.Main): SpatialColorScheme {

    var colorScheme = lightSpatialColorScheme().copy(
        primaryButton = FocusColors.purple,
        secondaryButton = FocusColors.lightPurple,
        panel = FocusColors.panel,
        dialog = FocusColors.dialog,
    )

    if (mode == FocusColorSchemes.Gray) {
        colorScheme = lightSpatialColorScheme().copy(
            primaryButton = FocusColors.darkGray,
            secondaryButton = FocusColors.lightGray,
            secondaryAlphaBackground = FocusColors.darkGray, // EditTextField & TextTileButton label
            active = FocusColors.purple,
        )
    } else if (mode == FocusColorSchemes.Transparent) {
        colorScheme = lightSpatialColorScheme().copy(
            secondaryButton = Color.Transparent,
        )
    } else if (mode == FocusColorSchemes.BlueTooltip) {
        colorScheme = lightSpatialColorScheme().copy(
            primaryAlphaBackground = FocusColors.disabledBlue, // Text of tooltip
            menu =  FocusColors.lightBlue2, // Background of tooltip
        )
    } else if (mode == FocusColorSchemes.PurpleTooltip) {
        colorScheme = lightSpatialColorScheme().copy(
            primaryAlphaBackground = FocusColors.darkPurple,
            menu =  FocusColors.aiChat,
        )
    }

    return colorScheme
}

@Composable
fun focusShapes(mode: FocusShapes = FocusShapes.Main): SpatialShapes {
    var shapes = SpatialShapes(
        small = RoundedCornerShape(15.dp), // SpatialTextField
        medium = RoundedCornerShape(24.dp), // TextTileButton
        large = RoundedCornerShape(30.dp), // Panels, Tooltips, Buttons
    )

    if (mode == FocusShapes.Squared) {
        shapes = SpatialShapes(
            large = RoundedCornerShape(15.dp),
        )
    }
    return shapes
}

@Composable
fun focusTypo(): SpatialTypography {

    val typo = SpatialTypography(
        headline1 = LocalTypography.current.headline1.copy(
            fontFamily = focusFont
        ),
        headline2 = LocalTypography.current.headline2.copy(
            fontFamily = focusFont
        ),
        headline3 = LocalTypography.current.headline3.copy(
            fontFamily = focusFont
        ),
        body1 = LocalTypography.current.body1.copy( //primary button text
            fontFamily = focusFont,
            //color = FocusColors.textColor,
            fontSize = 20.sp),
        body2 = LocalTypography.current.body2.copy( //secondary label text
            fontFamily = focusFont,
            fontSize = 20.sp),
        headline1Strong = LocalTypography.current.headline1.copy(
            fontFamily = focusFont
        ),
        headline2Strong = LocalTypography.current.headline2.copy(
            fontFamily = focusFont
        ),
        headline3Strong = LocalTypography.current.headline3.copy(
            fontFamily = focusFont
        ),
        body1Strong = LocalTypography.current.body1.copy( //Primary label text
            fontFamily = focusFont,
            fontSize = 20.sp),
        body2Strong = LocalTypography.current.body2.copy(
            fontFamily = focusFont
        ), //fontWeight = FontWeight.ExtraBold
    )

    return typo
}