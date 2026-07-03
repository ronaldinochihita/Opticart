package pt.ipbeja.gestaocompras.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = OpticartVerde,
    onPrimary = LightSurface,
    primaryContainer = OpticartVerdeSuave,
    onPrimaryContainer = OpticartVerdeEscuro,
    secondary = OpticartLaranja,
    onSecondary = LightSurface,
    error = OpticartVermelho,
    onError = LightSurface,
    background = LightBackground,
    onBackground = OpticartTextoForte,
    surface = LightSurface,
    onSurface = OpticartTextoForte,
    surfaceVariant = OpticartVerdeSuave,
    onSurfaceVariant = OpticartTextoSuave,
    outline = Color(0xFFDDDDDD)
)

private val DarkColors = darkColorScheme(
    primary = OpticartVerde,
    onPrimary = LightSurface,
    primaryContainer = OpticartVerdeEscuro,
    onPrimaryContainer = OpticartVerdeSuave,
    secondary = OpticartLaranja,
    onSecondary = LightSurface,
    error = OpticartVermelho,
    onError = LightSurface,
    background = DarkBackground,
    onBackground = LightSurface,
    surface = DarkSurface,
    onSurface = LightSurface,
)

@Composable
fun GestaoComprasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
