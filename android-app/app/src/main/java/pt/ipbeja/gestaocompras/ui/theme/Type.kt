package pt.ipbeja.gestaocompras.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultFont = FontFamily.Default

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.Bold,
        fontSize = 42.sp, lineHeight = 48.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp, lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = defaultFont, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.6.sp
    ),
)
