package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FrostedHeroCyan
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary

@Composable
fun ProgressRing(
  score: Int,
  modifier: Modifier = Modifier,
  size: Dp = 110.dp,
  strokeWidth: Dp = 10.dp,
  primaryColor: Color = LinguaPrimary,
  secondaryColor: Color = FrostedHeroCyan
) {
  val animatedProgress by animateFloatAsState(
    targetValue = score / 100f,
    animationSpec = tween(durationMillis = 1000),
    label = "score_ring"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val strokePx = strokeWidth.toPx()
      val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
      val topLeft = Offset(strokePx / 2f, strokePx / 2f)

      // Background track
      drawArc(
        color = primaryColor.copy(alpha = 0.12f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokePx, cap = StrokeCap.Round)
      )

      // Active progress arc with luminous sweep gradient
      drawArc(
        brush = Brush.sweepGradient(listOf(primaryColor, secondaryColor, primaryColor)),
        startAngle = -90f,
        sweepAngle = 360f * animatedProgress,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokePx, cap = StrokeCap.Round)
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "$score",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "/ 100",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun MetricProgressBar(
  label: String,
  percentage: Int,
  color: Color = LinguaPrimary,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = (percentage / 100f).coerceIn(0f, 1f),
    animationSpec = tween(800),
    label = "metric_bar"
  )

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "$percentage%",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = color
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(color.copy(alpha = 0.12f))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(animatedProgress)
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(
            brush = Brush.horizontalGradient(
              listOf(color, color.copy(alpha = 0.85f))
            )
          )
      )
    }
  }
}

@Composable
fun DailyGoalIndicator(
  currentCompleted: Int = 2,
  totalGoal: Int = 3,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 0 until totalGoal) {
      val isDone = i < currentCompleted
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(
            if (isDone) LinguaPrimary else Color.Transparent
          )
          .border(
            width = 1.5.dp,
            color = if (isDone) LinguaPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
            shape = CircleShape
          )
      )
    }
  }
}

@Composable
fun SevenDayImprovementChart(
  scores: List<Int> = listOf(62, 64, 65, 68, 69, 71, 74),
  days: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
  modifier: Modifier = Modifier
) {
  FrostedGlassCard(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    elevation = 3.dp
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Text(
        text = "Recent Improvement",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(16.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
          if (scores.size < 2) return@Canvas
          val width = size.width
          val height = size.height
          val minScore = 50f
          val maxScore = 100f
          val stepX = width / (scores.size - 1)

          val points = scores.mapIndexed { index, score ->
            val x = index * stepX
            val norm = (score - minScore) / (maxScore - minScore)
            val y = height - (norm * height)
            Offset(x, y)
          }

          // Fill path under curve
          val fillPath = Path().apply {
            moveTo(points.first().x, height)
            for (p in points) {
              lineTo(p.x, p.y)
            }
            lineTo(points.last().x, height)
            close()
          }

          drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
              listOf(LinguaPrimary.copy(alpha = 0.25f), Color.Transparent)
            )
          )

          // Line path
          val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
              lineTo(points[i].x, points[i].y)
            }
          }

          drawPath(
            path = strokePath,
            color = LinguaPrimary,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
          )

          // Plot points
          for (p in points) {
            drawCircle(
              color = Color.White,
              radius = 5.dp.toPx(),
              center = p
            )
            drawCircle(
              color = LinguaPrimary,
              radius = 3.dp.toPx(),
              center = p
            )
          }
        }
      }

      // Days row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        for (day in days) {
          Text(
            text = day,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
