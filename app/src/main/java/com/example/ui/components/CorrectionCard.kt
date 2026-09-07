package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.SessionCorrection
import com.example.ui.theme.LinguaCorrectionContainer
import com.example.ui.theme.LinguaCorrectionRed
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSuccess
import com.example.ui.theme.LinguaSuccessContainer

@Composable
fun CorrectionCard(
  correction: SessionCorrection,
  onTryAgain: () -> Unit,
  onContinue: () -> Unit,
  onPlayCorrectedAudio: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  FrostedGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("ai_correction_card"),
    shape = RoundedCornerShape(24.dp),
    elevation = 6.dp
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header tag
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LinguaPrimary.copy(alpha = 0.12f))
            .border(
              width = 1.dp,
              color = LinguaPrimary.copy(alpha = 0.25f),
              shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = LinguaPrimary,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Almost! Try saying:",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = LinguaPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Corrected Sentence in clean green accent container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(LinguaSuccessContainer.copy(alpha = 0.45f))
          .border(1.dp, LinguaSuccess.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
          .padding(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(LinguaSuccess),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "✓",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Text(
            text = "\"${correction.correctedSentence}\"",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
          )

          if (onPlayCorrectedAudio != null) {
            IconButton(
              onClick = onPlayCorrectedAudio,
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Listen to pronunciation",
                tint = LinguaPrimary
              )
            }
          }
        }
      }

      // Original text with gentle strikethrough indication
      if (correction.originalSentence.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 4.dp)
        ) {
          Text(
            text = "Your sentence: ",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "\"${correction.originalSentence}\"",
            fontSize = 12.sp,
            color = LinguaCorrectionRed,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Why? Explanation
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .border(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
            shape = RoundedCornerShape(14.dp)
          )
          .padding(12.dp)
      ) {
        Text(
          text = "Why?",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = correction.explanation,
          fontSize = 13.sp,
          lineHeight = 18.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action buttons: "Try Again" & "Continue Conversation"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onTryAgain,
          colors = ButtonDefaults.buttonColors(containerColor = LinguaPrimary),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("correction_try_again_button")
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Try Again",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
          )
        }

        OutlinedButton(
          onClick = onContinue,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("correction_continue_button")
        ) {
          Text(
            text = "Continue",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
