// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.meta.pixelandtexel.geovoyage.R
import com.meta.spatial.uiset.theme.SpatialTypography

val montserratFontFamily =
    FontFamily(
        Font(R.font.montserrat_light, FontWeight.Light),
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_medium, FontWeight.Medium),
        Font(R.font.montserrat_semibold, FontWeight.SemiBold),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_black, FontWeight.Black),
    )

val baseline = SpatialTypography()

val GeoVoyageTypography =
    SpatialTypography(
        headline1Strong =
            baseline.headline1Strong.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
        headline1 =
            baseline.headline1.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
        headline2Strong =
            baseline.headline2Strong.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
        headline2 =
            baseline.headline2.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
        headline3Strong =
            baseline.headline3Strong.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
        headline3 =
            baseline.headline3.copy(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.Medium,
                color = GeoVoyageColors.textColor),
        body1Strong =
            baseline.body1Strong.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
        body1 =
            baseline.body1.copy(
                fontFamily = montserratFontFamily,
                fontWeight = FontWeight.Medium,
                color = GeoVoyageColors.textColor),
        body2Strong =
            baseline.body2Strong.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
        body2 =
            baseline.body2.copy(
                fontFamily = montserratFontFamily, color = GeoVoyageColors.textColor),
    )
