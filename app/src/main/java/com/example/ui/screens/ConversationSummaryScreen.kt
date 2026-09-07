package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationSession
import com.example.data.model.SessionCorrection
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.MetricProgressBar
import com.example.ui.components.ProgressRing
import com.example.ui.theme.FrostedHeroIndigoEnd
import com.example.ui.theme.FrostedHeroIndigoStart
import com.example.ui.theme.LinguaCorrectionContainer
import com.example.ui.theme.LinguaCorrectionRed
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary
import com.example.ui.theme.LinguaSuccess
import com.example.ui.theme.LinguaSuccessContainer

@Composable
fun ConversationSummaryScreen(
  session: ConversationSession?,
  corrections: List<SessionCorrection>,
  onPracticeAgain: () -> Unit,
  onBackToHome: () -> Unit
) {
  val durationMins = ((session?.durationSeconds ?: 480) / 60).coerceAtLeast(1)
  val overall = session?.overallScore ?: 74
  val grammar = session?.grammarScore ?: 72
  val vocab = session?.vocabularyScore ?: 78
  val fluency = session?.fluencyScore ?: 70

  FrostedBackgroundContainer {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Celebration Header
      Text(
        text = "Nice work! 🎉",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Schedule,
          contentDescription = null,
          tint = LinguaPrimary,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "You practiced for $durationMins minutes.",
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Overall Score Ring in Frosted Glass Container
      FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = 4.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "SESSION SCORE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF94A3B8)
          )

          Spacer(modifier = Modifier.height(16.dp))

          ProgressRing(
            score = overall,
            size = 120.dp,
            strokeWidth = 10.dp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Feedback Metrics Frosted Card
      FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = 3.dp
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Your Feedback",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(16.dp))

          MetricProgressBar(
            label = "Grammar",
            percentage = grammar,
            color = LinguaPrimary
          )

          Spacer(modifier = Modifier.height(12.dp))

          MetricProgressBar(
            label = "Vocabulary",
            percentage = vocab,
            color = LinguaSecondary
          )

          Spacer(modifier = Modifier.height(12.dp))

          MetricProgressBar(
            label = "Fluency",
            percentage = fluency,
            color = LinguaSuccess
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Today's Mistakes Section
      FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = 3.dp
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Today's Mistakes",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(14.dp))

          if (corrections.isEmpty()) {
            Text(
              text = "Outstanding! No major grammar mistakes were detected in this session.",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              corrections.take(3).forEach { item ->
                MistakeReviewItem(item)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Action Buttons: "Practice Again" & "Back to Home"
      Button(
        onClick = onPracticeAgain,
        colors = ButtonDefaults.buttonColors(
          containerColor = LinguaPrimary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("summary_practice_again_button")
      ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Practice Again",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedButton(
        onClick = onBackToHome,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("summary_back_to_home_button")
      ) {
        Text(
          text = "Back to Home",
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun MistakeReviewItem(item: SessionCorrection) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .border(
        width = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
      )
      .padding(14.dp)
  ) {
    // Original (red ❌)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .background(LinguaCorrectionContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = null,
          tint = LinguaCorrectionRed,
          modifier = Modifier.size(12.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = item.originalSentence,
        fontSize = 13.sp,
        color = LinguaCorrectionRed,
        fontWeight = FontWeight.Medium
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Corrected (green ✅)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .background(LinguaSuccessContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = LinguaSuccess,
          modifier = Modifier.size(12.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = item.correctedSentence,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    if (item.explanation.isNotBlank()) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = item.explanation,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 30.dp)
      )
    }
  }
}
