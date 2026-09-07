package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary
import kotlin.math.sin

@Composable
fun AudioWaveformView(
  modifier: Modifier = Modifier,
  isActive: Boolean = true,
  barCount: Int = 18,
  maxBarHeight: Dp = 36.dp,
  minBarHeight: Dp = 6.dp,
  barWidth: Dp = 3.dp,
  amplitude: Float = 0.5f,
  primaryColor: Color = LinguaPrimary,
  secondaryColor: Color = LinguaSecondary
) {
  val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
  val phase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * Math.PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "waveform_phase"
  )

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 0 until barCount) {
      val normalizedIndex = i.toFloat() / barCount.toFloat()
      val wave = sin(normalizedIndex * Math.PI * 2.5 + phase).toFloat()
      val factor = if (isActive) {
        val base = (0.25f + 0.75f * ((wave + 1f) / 2f))
        (base * (0.4f + 0.6f * amplitude.coerceIn(0.1f, 1f))).coerceIn(0.1f, 1f)
      } else {
        0.15f
      }

      val barHeight = minBarHeight + (maxBarHeight - minBarHeight) * factor
      val color = if (i % 2 == 0) primaryColor else secondaryColor

      Box(
        modifier = Modifier
          .width(barWidth)
          .height(barHeight)
          .clip(RoundedCornerShape(2.dp))
          .background(color.copy(alpha = if (isActive) 0.9f else 0.35f))
      )
    }
  }
}
