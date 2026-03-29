package loli.kanna.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import loli.kanna.resources.Res
import loli.kanna.resources.manrope_bold
import loli.kanna.resources.manrope_regular
import loli.kanna.resources.manrope_medium
import loli.kanna.resources.manrope_semibold
import org.jetbrains.compose.resources.Font

@Composable
fun getTypography(): Typography {
    val bodyFontFamily =
        FontFamily(
            Font(Res.font.manrope_regular, FontWeight.Normal),
            Font(Res.font.manrope_bold, FontWeight.Bold),
            Font(Res.font.manrope_semibold, FontWeight.SemiBold),
            Font(Res.font.manrope_medium, FontWeight.Medium),
        )

    val baseline = Typography()

    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = bodyFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = bodyFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = bodyFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = bodyFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = bodyFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = bodyFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = bodyFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = bodyFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = bodyFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    )
}
