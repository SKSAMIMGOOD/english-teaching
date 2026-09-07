package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF0C4A6E),
    secondaryContainer = Color(0xFF0369A1),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = Color(0xFFA78BFA),
    background = LinguaBackgroundDark,
    surface = LinguaSurfaceDark,
    surfaceVariant = LinguaSurfaceVariantDark,
    onBackground = LinguaTextPrimaryDark,
    onSurface = LinguaTextPrimaryDark,
    onSurfaceVariant = LinguaTextSecondaryDark,
    outline = LinguaBorderDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LinguaPrimary,
    onPrimary = Color.White,
    primaryContainer = LinguaPrimaryContainer,
    onPrimaryContainer = LinguaOnPrimaryContainer,
    secondary = LinguaSecondary,
    onSecondary = Color.White,
    secondaryContainer = LinguaSecondaryContainer,
    onSecondaryContainer = LinguaOnSecondaryContainer,
    tertiary = LinguaTertiary,
    tertiaryContainer = LinguaTertiaryContainer,
    background = LinguaBackgroundLight,
    surface = LinguaSurfaceLight,
    surfaceVariant = LinguaSurfaceVariantLight,
    onBackground = LinguaTextPrimaryLight,
    onSurface = LinguaTextPrimaryLight,
    onSurfaceVariant = LinguaTextSecondaryLight,
    outline = LinguaBorderLight,
  )

@Composable
fun LinguaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  LinguaTheme(darkTheme = darkTheme, content = content)
}

