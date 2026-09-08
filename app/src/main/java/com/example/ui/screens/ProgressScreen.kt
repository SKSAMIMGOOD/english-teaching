package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationSession
import com.example.data.model.SessionCorrection
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ProgressScreen(
  userProfile: UserProfile?,
  allSessions: List<ConversationSession> = emptyList(),
  recentCorrections: List<SessionCorrection> = emptyList(),
  onStartPractice: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val streak = userProfile?.streakDays ?: 0
  val hasSessions = allSessions.isNotEmpty()

  val overallScore = if (hasSessions) allSessions.map { it.overallScore }.average().toInt() else 0
  val speakingScore = if (hasSessions) allSessions.map { it.speakingScore }.average().toInt() else 0
  val grammarScore = if (hasSessions) allSessions.map { it.grammarScore }.average().toInt() else 0
  val vocabScore = if (hasSessions) allSessions.map { it.vocabularyScore }.average().toInt() else 0
  val fluencyScore = if (hasSessions) allSessions.map { it.fluencyScore }.average().toInt() else 0

  val totalMinutes = allSessions.sumOf { it.durationSeconds } / 60
  val totalSessions = allSessions.size
  val totalCorrections = allSessions.sumOf { it.correctionsCount }

  // 7-day data from real sessions
  val (chartScores, chartDays) = remember(allSessions) {
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val scores = mutableListOf<Int>()
    val days = mutableListOf<String>()

    for (i in 6 downTo 0) {
      val dayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
      days.add(dayFormat.format(dayCal.time))

      val targetYear = dayCal.get(Calendar.YEAR)
      val targetDay = dayCal.get(Calendar.DAY_OF_YEAR)
      val daySessions = allSessions.filter {
        val sc = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        sc.get(Calendar.YEAR) == targetYear && sc.get(Calendar.DAY_OF_YEAR) == targetDay
      }
      val avgDayScore = if (daySessions.isNotEmpty()) {
        daySessions.map { it.overallScore }.average().toInt()
      } else 0
      scores.add(avgDayScore)
    }
    Pair(scores, days)
  }

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
            text = if (hasSessions) "Real-time speaking mastery" else "Start practicing to track progress",
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
              text = when {
                overallScore >= 82 -> "Advanced Speaker"
                overallScore >= 70 -> "Conversational"
                overallScore > 0 -> "Elementary"
                else -> "Ready to Begin"
              },
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (hasSessions) "$totalSessions practice sessions completed" else "Complete your first conversation",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (hasSessions) FrostedFluencyGreen else Color(0xFF94A3B8)
            )
          }

          ProgressRing(
            score = overallScore,
            size = 96.dp,
            strokeWidth = 9.dp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Real Stats Summary Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Sessions
        FrostedGlassCard(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(20.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LinguaPrimary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("SESSIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("$totalSessions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
          }
        }

        // Practice Time
        FrostedGlassCard(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(20.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = LinguaSecondary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("TIME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("${totalMinutes}m", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
          }
        }

        // Corrections
        FrostedGlassCard(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(20.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = FrostedFluencyGreen,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("FIXED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("$totalCorrections", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. The 4 Key Metrics Card (Real Room Data)
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
            percentage = speakingScore,
            color = LinguaPrimary
          )

          Spacer(modifier = Modifier.height(14.dp))

          MetricProgressBar(
            label = "Grammar",
            percentage = grammarScore,
            color = LinguaSecondary
          )

          Spacer(modifier = Modifier.height(14.dp))

          MetricProgressBar(
            label = "Vocabulary",
            percentage = vocabScore,
            color = LinguaTertiary
          )

          Spacer(modifier = Modifier.height(14.dp))

          MetricProgressBar(
            label = "Fluency",
            percentage = fluencyScore,
            color = LinguaSuccess
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Recent Improvement 7-Day Line Chart (Real Daily Scores)
      SevenDayImprovementChart(
        scores = chartScores,
        days = chartDays
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 4. AI Recommendation Card (Adaptive & Actionable)
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

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "AI Learning Recommendation",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            val recommendationText = when {
              !hasSessions -> "Start your first practice session today! Consistent 5-minute conversations build long-term speaking fluency and confidence."
              recentCorrections.isNotEmpty() -> {
                val latest = recentCorrections.first()
                "Recent correction tip: Instead of \"${latest.originalSentence}\", try saying \"${latest.correctedSentence}\". ${latest.explanation}"
              }
              grammarScore < speakingScore -> "Your speaking flow is solid! Focus on past-tense verb agreements (e.g. 'I went', 'we decided') to boost your grammar rating."
              else -> "Great progress on grammar and vocabulary! Challenge yourself next with Real-Life Situations or AI Question Practice."
            }
            Text(
              text = recommendationText,
              fontSize = 13.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!hasSessions) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(LinguaPrimary.copy(alpha = 0.12f))
                  .clickable(onClick = onStartPractice)
                  .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Start Practice",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = LinguaPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.ArrowForward,
                  contentDescription = null,
                  tint = LinguaPrimary,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
