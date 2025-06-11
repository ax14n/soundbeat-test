package com.example.soundbeat_test.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8C42),           // Naranja más brillante sobre fondo oscuro
    onPrimary = Color.Black,

    secondary = Color(0xFFB0B0B0),         // Gris claro sobre fondo oscuro
    onSecondary = Color.Black,

    tertiary = Color(0xFFD1BFA3),
    onTertiary = Color.Black,

    background = Color(0xFF121212),        // Fondo negro industrial
    onBackground = Color(0xFFE0E0E0),      // Texto claro sobre fondo negro

    surface = Color(0xFF2A2929),
    onSurface = Color(0xFFEFEFEF),

    primaryContainer = Color(0xFFFFBB88),
    onPrimaryContainer = Color(0xFF3B2000),

    secondaryContainer = Color(0xFF2C2C2C),
    onSecondaryContainer = Color(0xFFE0E0E0),

    tertiaryContainer = Color(0xFF4E433A),
    onTertiaryContainer = Color(0xFFFFF5EB)
)
//    darkColorScheme(
//    primary = Color(0xFF313A76),
//    secondary = Color(0xFF585B68),
//    tertiary = Pink80,
//    background = Color(0xFF111111),
//
//    )

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF026374),           // Naranja oxidado / hierro oxidado
    onPrimary = Color.White,               // Texto blanco sobre botón primario

    secondary = Color(0xFFB24C00),         // Gris acero
    onSecondary = Color.White,             // Contraste para textos sobre secundario

    tertiary = Color(0xFF5E503F),          // Marrón cuero envejecido
    onTertiary = Color.White,

    background = Color(0xFFE5E5E5),        // Gris cemento claro (fondo general)
    onBackground = Color(0xFF1C1C1C),      // Texto gris carbón sobre fondo claro

    surface = Color(0xFFF1F0F0),           // Superficies como tarjetas, etc.
    onSurface = Color(0xFF2B2B2B),         // Texto oscuro sobre superficie clara

    primaryContainer = Color(0xFFFFD8C2),  // Naranja claro para elementos menos intensos
    onPrimaryContainer = Color(0xFF4D2600),

    secondaryContainer = Color(0xFFD6D6D6),
    onSecondaryContainer = Color(0xFF303030),

    tertiaryContainer = Color(0xFFBFAF9B),
    onTertiaryContainer = Color(0xFF3A2C1E)
)
//    lightColorScheme(
//    primary = Color(0xFFD55510),
//    secondary = Color(0xFFBDBCBC),
//    tertiary = Pink40,
//    background = Color(0xFFC7CACE),

/* Other default colors to override
surface = Color(0xFFFFFBFE),
onPrimary = Color.White,
onSecondary = Color.White,
onTertiary = Color.White,
onBackground = Color(0xFF1C1B1F),
onSurface = Color(0xFF1C1B1F),
*/


@Composable
fun SoundBeat_TestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}