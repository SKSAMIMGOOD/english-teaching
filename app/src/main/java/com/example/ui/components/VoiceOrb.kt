package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary
import com.example.ui.theme.LinguaTertiary

@Composable
fun VoiceOrb(
  modifier: Modifier = Modifier,
  isListening: Boolean,
  isAiSpeaking: Boolean,
  size: Dp = 200.dp
) {
  val infiniteTransition = rememberInfiniteTransition(label = "voice_orb")

  val pulse by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = if (isListening || isAiSpeaking) 1.15f else 1.02f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isAiSpeaking) 700 else if (isListening) 900 else 2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "orb_pulse"
  )

  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "orb_rotation"
  )

  val outerAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "outer_alpha"
  )

  Box(
    modifier = modifier.size(size * 1.5f),
    contentAlignment = Alignment.Center
  ) {
    // Outer glow aura
    Box(
      modifier = Modifier
        .size(size * 1.45f)
        .scale(pulse)
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              (if (isListening) LinguaSecondary else LinguaPrimary).copy(alpha = outerAlpha),
              Color.Transparent
            )
          )
        )
    )

    // Rotating gradient ring
    Box(
      modifier = Modifier
        .size(size * 1.15f)
        .rotate(rotation)
        .scale(pulse)
        .clip(CircleShape)
        .border(
          width = 3.dp,
          brush = Brush.sweepGradient(
            listOf(
              LinguaPrimary,
              LinguaSecondary,
              LinguaTertiary,
              LinguaSecondary,
              LinguaPrimary
            )
          ),
          shape = CircleShape
        )
    )

    // Inner glowing sphere
    Box(
      modifier = Modifier
        .size(size)
        .scale(pulse)
        .clip(CircleShape)
        .background(
          brush = Brush.linearGradient(
            colors = listOf(
              LinguaPrimary.copy(alpha = 0.85f),
              LinguaTertiary.copy(alpha = 0.85f),
              LinguaSecondary.copy(alpha = 0.9f)
            )
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      // Center teacher avatar
      Image(
        painter = painterResource(id = R.drawable.ai_teacher_avatar),
        contentDescription = "AI Teacher",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(size * 0.76f)
          .clip(CircleShape)
          .border(3.dp, Color.White.copy(alpha = 0.85f), CircleShape)
      )
    }
  }
}
