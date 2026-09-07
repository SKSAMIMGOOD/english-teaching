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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.MetricProgressBar
import com.example.ui.components.ProgressRing
import com.example.ui.components.SevenDayImprovementChart
import com.example.ui.theme.FrostedFluencyGreen
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary
import com.example.ui.theme.LinguaSuccess
import com.example.ui.theme.LinguaTertiary

@Composable
fun ProgressScreen(
  userProfile: UserProfile?,
  modifier: Modifier = Modifier
) {
  val overallScore = 72
  val streak = userProfile?.streakDays ?: 3

  FrostedBackgroundContainer(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp)
        .testTag("progress_screen")
    ) {
      // Top Title & Streak Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Your Progress",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Consistent speaking builds mastery",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Streak Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFF7ED))
            .border(1.dp, Color(0xFFFDBA74).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = Color(0xFFEA580C),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$streak-day streak",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFC2410C)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 1. Overall Score Hero Frosted Card
      FrostedGlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("overall_score_card"),
        shape = RoundedCornerShape(28.dp),
        elevation = 4.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "OVERALL ENGLISH LEVEL",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Conversational",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "+6% higher than last week",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = FrostedFluencyGreen
            )
          }

          ProgressRing(
            score = overallScore,
            size = 96.dp,
            strokeWidth = 9.dp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. The 4 Key Metrics Card
      FrostedGlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("metrics_card"),
        shape = RoundedCornerShape(28.dp),
        elevation = 3.dp
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Key Skill Breakdown",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(16.dp))

          MetricProgressBar(
            label = "Speaking",
            percentage = 72,
            color = LinguaPrimary
          )

          Spacer(modifier = Modifier.height(14.dp))

          MetricProgressBar(
            label = "Grammar",
            percentage = 68,
            color = LinguaSecondary
          )

          Spacer(modifier = Modifier.height(14.dp))

          MetricProgressBar(
            label = "Vocabulary",
            percentage = 76,
            color = LinguaTertiary
          )

          Spacer(modifier = Modifier.height(14.dp))

          MetricProgressBar(
            label = "Fluency",
            percentage = 70,
            color = LinguaSuccess
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Recent Improvement 7-Day Line Chart
      SevenDayImprovementChart(
        scores = listOf(63, 65, 66, 68, 69, 70, 72),
        days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 4. AI Recommendation Card
      FrostedGlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_recommendation_card"),
        shape = RoundedCornerShape(24.dp),
        elevation = 3.dp
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(
                brush = Brush.linearGradient(listOf(LinguaPrimary, LinguaSecondary))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          Column {
            Text(
              text = "AI Recommendation",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Try speaking for 5 more minutes today. Focus on past-tense sentences (e.g. 'I went', 'I studied').",
              fontSize = 13.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
