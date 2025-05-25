package de.syntax.institut.projectweek.cocktailconnoisse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import de.syntax.institut.projectweek.cocktailconnoisse.R

val comicReliefFontFamily = FontFamily(
    Font(R.font.comic_relief)
)

val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = comicReliefFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = comicReliefFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = comicReliefFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = comicReliefFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = comicReliefFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = comicReliefFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = comicReliefFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = comicReliefFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = comicReliefFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = comicReliefFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = comicReliefFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = comicReliefFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = comicReliefFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = comicReliefFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = comicReliefFontFamily),
)
