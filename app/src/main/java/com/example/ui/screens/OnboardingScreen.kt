package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiAvatar
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.theme.FrostedGlassBorderLight
import com.example.ui.theme.FrostedGlassDark
import com.example.ui.theme.FrostedGlassDarkBorder
import com.example.ui.theme.FrostedGlassWhite
import com.example.ui.theme.FrostedHeroCyan
import com.example.ui.theme.FrostedHeroIndigoEnd
import com.example.ui.theme.FrostedHeroIndigoStart
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary
import com.example.ui.theme.LinguaSuccess
import com.example.ui.theme.LinguaSuccessContainer

@Composable
fun OnboardingScreen(
  onGetStarted: () -> Unit,
  onSkip: () -> Unit
) {
  var currentPage by remember { mutableIntStateOf(0) }

  FrostedBackgroundContainer {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top bar: Skip button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        if (currentPage < 2) {
          TextButton(
            onClick = onSkip,
            modifier = Modifier.testTag("onboarding_skip_button")
          ) {
            Text(
              text = "Skip",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp
            )
          }
        } else {
          Spacer(modifier = Modifier.height(48.dp))
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Carousel Content
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        AnimatedContent(
          targetState = currentPage,
          transitionSpec = {
            if (targetState > initialState) {
              (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                slideOutHorizontally { width -> -width } + fadeOut()
              )
            } else {
              (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                slideOutHorizontally { width -> width } + fadeOut()
              )
            }
          },
          label = "onboarding_carousel"
        ) { page ->
          when (page) {
            0 -> OnboardingPageA()
            1 -> OnboardingPageB()
            else -> OnboardingPageC()
          }
        }
      }

      // Page indicator dots
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (i in 0..2) {
          val isSelected = i == currentPage
          Box(
            modifier = Modifier
              .size(if (isSelected) 24.dp else 8.dp, 8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(
                if (isSelected) LinguaPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Bottom Action Buttons
      if (currentPage == 2) {
        Button(
          onClick = onGetStarted,
          colors = ButtonDefaults.buttonColors(containerColor = LinguaPrimary),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = LinguaPrimary.copy(alpha = 0.3f))
            .testTag("onboarding_get_started_button")
        ) {
          Text(
            text = "Get Started",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
        }
      } else {
        Button(
          onClick = { currentPage++ },
          colors = ButtonDefaults.buttonColors(containerColor = LinguaPrimary),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = LinguaPrimary.copy(alpha = 0.3f))
            .testTag("onboarding_next_button")
        ) {
          Text(
            text = "Continue",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun OnboardingPageA() {
  val isDark = isSystemInDarkTheme()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxWidth()
  ) {
    // Visual: AI Teacher with frosted speech bubbles
    Box(
      modifier = Modifier.size(220.dp),
      contentAlignment = Alignment.Center
    ) {
      // Background glow circle
      Box(
        modifier = Modifier
          .size(190.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              listOf(FrostedHeroCyan.copy(alpha = 0.25f), Color.Transparent)
            )
          )
      )

      AiAvatar(
        size = 110.dp,
        isSpeaking = true,
        showOnlineIndicator = true
      )

      // Floating frosted speech bubble left
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .clip(RoundedCornerShape(16.dp))
          .background(if (isDark) FrostedGlassDark else FrostedGlassWhite)
          .border(
            1.dp,
            if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight,
            RoundedCornerShape(16.dp)
          )
          .padding(horizontal = 12.dp, vertical = 7.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Mic, contentDescription = null, tint = LinguaPrimary, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Speak freely!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LinguaPrimary)
        }
      }

      // Floating frosted speech bubble right
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .clip(RoundedCornerShape(16.dp))
          .background(if (isDark) FrostedGlassDark else FrostedGlassWhite)
          .border(
            1.dp,
            if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight,
            RoundedCornerShape(16.dp)
          )
          .padding(horizontal = 12.dp, vertical = 7.dp)
      ) {
        Text(text = "Hello there! 👋", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = "Improve your English\nby speaking.",
      fontSize = 28.sp,
      lineHeight = 34.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = (-0.5).sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "Have natural, stress-free conversations with your 24/7 AI teacher. Gain confidence without nervousness.",
      fontSize = 15.sp,
      lineHeight = 22.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )
  }
}

@Composable
private fun OnboardingPageB() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxWidth()
  ) {
    // Visual: Example conversation correction card with FrostedGlassCard
    FrostedGlassCard(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(28.dp),
      elevation = 4.dp
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        // User bubble
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(
                brush = Brush.linearGradient(listOf(FrostedHeroIndigoStart, FrostedHeroIndigoEnd))
              )
              .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Text(
              text = "Yesterday I go to market.",
              color = Color.White,
              fontSize = 14.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Teacher Correction Bubble
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LinguaSuccessContainer.copy(alpha = 0.6f))
            .border(1.dp, LinguaSuccess.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LinguaSuccess, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Almost! Try saying:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LinguaSuccess
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "\"Yesterday I went to the market.\"",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = "Make mistakes.\nLearn from them.",
      fontSize = 28.sp,
      lineHeight = 34.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = (-0.5).sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "Instant, gentle corrections explain the 'why' without feeling like a strict exam. You learn naturally through practice.",
      fontSize = 15.sp,
      lineHeight = 22.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )
  }
}

@Composable
private fun OnboardingPageC() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier.size(160.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(140.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.linearGradient(
              listOf(FrostedHeroIndigoStart.copy(alpha = 0.2f), FrostedHeroCyan.copy(alpha = 0.3f))
            )
          )
      )

      Icon(
        imageVector = Icons.Default.Schedule,
        contentDescription = null,
        tint = LinguaPrimary,
        modifier = Modifier.size(72.dp)
      )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = "Practice anytime with\nyour AI teacher.",
      fontSize = 28.sp,
      lineHeight = 34.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = (-0.5).sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "Just 5 to 10 minutes of daily speaking transforms your fluency and expands your vocabulary.",
      fontSize = 15.sp,
      lineHeight = 22.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )
  }
}
