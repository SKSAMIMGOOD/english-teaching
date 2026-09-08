package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FrostedHeroCyan
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaTheme

/**
 * Standard Frosted Glass container card with specular edge reflections and subtle depth.
 */
@Composable
fun FrostedGlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(28.dp),
  elevation: Dp = 2.dp,
  backgroundColor: Color? = null,
  borderColor: Color? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val isDark = LinguaTheme.isDark
  val colors = LinguaTheme.colors
  val bg = backgroundColor ?: colors.cardBackground
  val specularBorder = borderColor ?: colors.borderSubtle

  Box(
    modifier = modifier
      .shadow(
        elevation = elevation,
        shape = shape,
        spotColor = if (isDark) Color(0x33000000) else Color(0x1A4F46E5),
        ambientColor = if (isDark) Color(0x1F000000) else Color(0x0D0F172A)
      )
      .clip(shape)
      .background(bg)
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.85f),
            specularBorder
          )
        ),
        shape = shape
      ),
    content = content
  )
}

/**
 * Atmospheric background container with soft radiant gradients and subtle luminous orbs
 * that illuminate the frosted glass surfaces placed above them.
 */
@Composable
fun FrostedBackgroundContainer(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val isDark = LinguaTheme.isDark
  val colors = LinguaTheme.colors

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(colors.background)
  ) {
    // Top-right soft indigo luminous aura
    Box(
      modifier = Modifier
        .size(260.dp)
        .offset(x = 180.dp, y = (-50).dp)
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              if (isDark) colors.primary.copy(alpha = 0.18f) else colors.primary.copy(alpha = 0.08f),
              Color.Transparent
            )
          )
        )
    )

    // Mid-left subtle cyan atmospheric glow
    Box(
      modifier = Modifier
        .size(240.dp)
        .offset(x = (-90).dp, y = 260.dp)
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              if (isDark) FrostedHeroCyan.copy(alpha = 0.15f) else FrostedHeroCyan.copy(alpha = 0.08f),
              Color.Transparent
            )
          )
        )
    )

    // Bottom-right subtle violet accent glow
    Box(
      modifier = Modifier
        .size(280.dp)
        .offset(x = 160.dp, y = 560.dp)
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              if (isDark) Color(0xFFA855F7).copy(alpha = 0.12f) else Color(0xFFA855F7).copy(alpha = 0.06f),
              Color.Transparent
            )
          )
        )
    )

    // Main screen content rendered over the luminous atmosphere
    content()
  }
}
