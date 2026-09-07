package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary

@Composable
fun SplashScreen() {
  val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "splash_scale"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            LinguaPrimary.copy(alpha = 0.08f),
            LinguaSecondary.copy(alpha = 0.08f)
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // App Logo with subtle pulsing glow
      Box(
        modifier = Modifier
          .size(110.dp)
          .scale(pulseScale)
          .clip(RoundedCornerShape(32.dp))
          .background(
            brush = Brush.linearGradient(
              listOf(LinguaPrimary, LinguaSecondary)
            )
          )
          .border(
            width = 3.dp,
            color = Color.White.copy(alpha = 0.8f),
            shape = RoundedCornerShape(32.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_lingua_ai_logo),
          contentDescription = "LinguaAI Logo",
          modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(24.dp))
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // App Name
      Text(
        text = "LinguaAI",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Tagline
      Text(
        text = "Your AI English Teacher",
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(36.dp))

      // Subtle AI animation indicator
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (i in 0..2) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(LinguaPrimary.copy(alpha = 0.5f + (i * 0.2f)))
          )
        }
      }
    }
  }
}
