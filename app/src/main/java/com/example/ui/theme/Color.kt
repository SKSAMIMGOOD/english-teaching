package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// LinguaAI Core Visual Identity: Indigo, Blue, Cyan, Slate
val LinguaPrimary = Color(0xFF4F46E5) // Vibrant Indigo
val LinguaPrimaryDark = Color(0xFF4338CA)
val LinguaPrimaryContainer = Color(0xFFEEF2FF)
val LinguaOnPrimaryContainer = Color(0xFF312E81)

val LinguaSecondary = Color(0xFF0891B2) // Vivid Cyan
val LinguaSecondaryContainer = Color(0xFFE0F2FE)
val LinguaOnSecondaryContainer = Color(0xFF0C4A6E)

val LinguaTertiary = Color(0xFF8B5CF6) // Gentle Violet Accent
val LinguaTertiaryContainer = Color(0xFFF3E8FF)

val LinguaBackgroundLight = Color(0xFFF8FAFC) // Off-white/slate light neutral
val LinguaSurfaceLight = Color(0xFFFFFFFF)
val LinguaSurfaceVariantLight = Color(0xFFF1F5F9)
val LinguaBorderLight = Color(0xFFE2E8F0)

val LinguaTextPrimaryLight = Color(0xFF0F172A) // Dark Navy Charcoal
val LinguaTextSecondaryLight = Color(0xFF475569) // Slate Muted
val LinguaTextTertiaryLight = Color(0xFF64748B)

// Success & Correction Accents
val LinguaSuccess = Color(0xFF10B981)
val LinguaSuccessContainer = Color(0xFFD1FAE5)
val LinguaCorrectionRed = Color(0xFFEF4444)
val LinguaCorrectionContainer = Color(0xFFFEE2E2)
val LinguaCorrectionText = Color(0xFF991B1B)
val LinguaCorrectionHighlight = Color(0xFFFEF3C7) // Warm subtle yellow highlight

// Frosted Glass Specific Tokens
val FrostedGlassWhite = Color(0xF2FFFFFF) // 95% translucent white for frosted panels
val FrostedGlassWhiteSubtle = Color(0xD9FFFFFF) // 85% translucent white
val FrostedGlassBorderLight = Color(0x80FFFFFF) // Specular highlight border
val FrostedGlassBorderSubtle = Color(0x33CBD5E1) // Soft shadow border
val FrostedGlassDark = Color(0xEE131C2E) // Frosted dark panel matching dark surface
val FrostedGlassDarkBorder = Color(0x33818CF8) // Subtle luminous indigo specular rim
val FrostedHeroIndigoStart = Color(0xFF4F46E5)
val FrostedHeroIndigoEnd = Color(0xFF3730A3)
val FrostedHeroCyan = Color(0xFF22D3EE)
val FrostedFluencyGreen = Color(0xFF22C55E)

// Dark Mode Palette
val LinguaBackgroundDark = Color(0xFF0B0F19) // Deep obsidian midnight
val LinguaSurfaceDark = Color(0xFF131C2E) // Midnight navy card surface
val LinguaSurfaceVariantDark = Color(0xFF1E293B) // Slate 800
val LinguaBorderDark = Color(0xFF2E3D56) // Clean slate border
val LinguaTextPrimaryDark = Color(0xFFF8FAFC) // Crisp white-slate text
val LinguaTextSecondaryDark = Color(0xFFCBD5E1) // Readable slate 300
val LinguaTextTertiaryDark = Color(0xFFA5B4FC) // Soft lavender-indigo for badges and labels

/**
 * Complete Semantic Design Tokens ensuring 100% readable contrast in both Light and Dark modes.
 */
data class LinguaColorTokens(
  val background: Color,
  val surface: Color,
  val surfaceSecondary: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val textMuted: Color,
  val border: Color,
  val borderSubtle: Color,
  val primary: Color,
  val primaryText: Color,
  val secondary: Color,
  val success: Color,
  val successContainer: Color,
  val warning: Color,
  val error: Color,
  val inputBackground: Color,
  val cardBackground: Color,
  val isDark: Boolean
)

val LightLinguaTokens = LinguaColorTokens(
  background = LinguaBackgroundLight,
  surface = LinguaSurfaceLight,
  surfaceSecondary = LinguaSurfaceVariantLight,
  textPrimary = LinguaTextPrimaryLight,
  textSecondary = LinguaTextSecondaryLight,
  textMuted = LinguaTextTertiaryLight,
  border = LinguaBorderLight,
  borderSubtle = FrostedGlassBorderSubtle,
  primary = LinguaPrimary,
  primaryText = Color.White,
  secondary = LinguaSecondary,
  success = LinguaSuccess,
  successContainer = LinguaSuccessContainer,
  warning = Color(0xFFF59E0B),
  error = LinguaCorrectionRed,
  inputBackground = Color.White,
  cardBackground = FrostedGlassWhite,
  isDark = false
)

val DarkLinguaTokens = LinguaColorTokens(
  background = LinguaBackgroundDark,
  surface = LinguaSurfaceDark,
  surfaceSecondary = LinguaSurfaceVariantDark,
  textPrimary = LinguaTextPrimaryDark,
  textSecondary = LinguaTextSecondaryDark,
  textMuted = LinguaTextTertiaryDark,
  border = LinguaBorderDark,
  borderSubtle = Color(0x33475569),
  primary = Color(0xFF818CF8),
  primaryText = Color.White,
  secondary = Color(0xFF38BDF8),
  success = Color(0xFF34D399),
  successContainer = Color(0xFF064E3B),
  warning = Color(0xFFFBBF24),
  error = Color(0xFFF87171),
  inputBackground = LinguaSurfaceVariantDark,
  cardBackground = FrostedGlassDark,
  isDark = true
)

// Legacy alias definitions to prevent any test regression
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = LinguaPrimary
val PurpleGrey40 = LinguaSecondary
val Pink40 = LinguaTertiary

