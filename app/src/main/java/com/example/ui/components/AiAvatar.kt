package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.LinguaSuccess

@Composable
fun AiAvatar(
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  isSpeaking: Boolean = false,
  isThinking: Boolean = false,
  showOnlineIndicator: Boolean = true
) {
  val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isSpeaking) 1.15f else if (isThinking) 1.06f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isSpeaking) 700 else 1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "avatar_pulse"
  )

  val ringAlpha by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ring_alpha"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    // Speaking / Thinking glowing ripple rings
    if (isSpeaking) {
      Box(
        modifier = Modifier
          .size(size * 1.35f)
          .scale(pulseScale)
          .clip(CircleShape)
          .background(LinguaPrimary.copy(alpha = ringAlpha * 0.4f))
      )
      Box(
        modifier = Modifier
          .size(size * 1.18f)
          .clip(CircleShape)
          .background(LinguaSecondary.copy(alpha = 0.25f))
      )
    }

    // Avatar image container
    Box(
      modifier = Modifier
        .size(size)
        .scale(if (isSpeaking || isThinking) pulseScale else 1f)
        .clip(CircleShape)
        .background(
          brush = Brush.linearGradient(
            listOf(LinguaPrimary, LinguaSecondary)
          )
        )
        .border(
          width = 2.dp,
          brush = Brush.linearGradient(
            listOf(LinguaPrimary.copy(alpha = 0.8f), LinguaSecondary)
          ),
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ai_teacher_avatar),
        contentDescription = "AI English Teacher Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(size)
          .clip(CircleShape)
      )
    }

    // Green online status dot
    if (showOnlineIndicator) {
      Box(
        modifier = Modifier
          .size((size.value * 0.26f).coerceIn(9f, 14f).dp)
          .align(Alignment.BottomEnd)
          .offset(x = (-1).dp, y = (-1).dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface)
          .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size((size.value * 0.2f).coerceIn(7f, 11f).dp)
            .clip(CircleShape)
            .background(LinguaSuccess)
        )
      }
    }
  }
}
