package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.SessionCorrection
import com.example.ui.theme.FrostedHeroIndigoEnd
import com.example.ui.theme.FrostedHeroIndigoStart
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaTheme

@Composable
fun ChatBubble(
  message: ChatMessage,
  onPlayAudio: (String) -> Unit,
  onTryAgainCorrection: (SessionCorrection) -> Unit,
  onContinueCorrection: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LinguaTheme.isDark
  val colors = LinguaTheme.colors

  if (message.isUser) {
    // User message (right aligned) - Frosted Indigo Pill
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.End
    ) {
      Box(
        modifier = Modifier
          .widthIn(max = 280.dp)
          .shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(
              topStart = 20.dp,
              topEnd = 20.dp,
              bottomStart = 20.dp,
              bottomEnd = 6.dp
            ),
            spotColor = colors.primary.copy(alpha = 0.25f)
          )
          .clip(
            RoundedCornerShape(
              topStart = 20.dp,
              topEnd = 20.dp,
              bottomStart = 20.dp,
              bottomEnd = 6.dp
            )
          )
          .background(
            brush = Brush.linearGradient(
              listOf(FrostedHeroIndigoStart, FrostedHeroIndigoEnd)
            )
          )
          .border(
            width = 1.dp,
            brush = Brush.linearGradient(
              listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.1f))
            ),
            shape = RoundedCornerShape(
              topStart = 20.dp,
              topEnd = 20.dp,
              bottomStart = 20.dp,
              bottomEnd = 6.dp
            )
          )
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .testTag("chat_bubble_user")
      ) {
        Text(
          text = message.text,
          color = Color.White,
          fontSize = 15.sp,
          lineHeight = 21.sp,
          fontWeight = FontWeight.Normal
        )
      }
    }
  } else {
    // AI Teacher message (left aligned) - Frosted Glass Container
    val cardBg = colors.cardBackground
    val cardBorder = colors.borderSubtle

    Column(
      modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AiAvatar(
          size = 36.dp,
          showOnlineIndicator = false
        )

        Column(modifier = Modifier.weight(1f, fill = false)) {
          Box(
            modifier = Modifier
              .widthIn(max = 300.dp)
              .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(
                  topStart = 6.dp,
                  topEnd = 20.dp,
                  bottomStart = 20.dp,
                  bottomEnd = 20.dp
                ),
                spotColor = Color(0x1A4F46E5),
                ambientColor = Color(0x0F0F172A)
              )
              .clip(
                RoundedCornerShape(
                  topStart = 6.dp,
                  topEnd = 20.dp,
                  bottomStart = 20.dp,
                  bottomEnd = 20.dp
                )
              )
              .background(cardBg)
              .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                  listOf(
                    if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.9f),
                    cardBorder
                  )
                ),
                shape = RoundedCornerShape(
                  topStart = 6.dp,
                  topEnd = 20.dp,
                  bottomStart = 20.dp,
                  bottomEnd = 20.dp
                )
              )
              .padding(horizontal = 16.dp, vertical = 12.dp)
              .testTag("chat_bubble_ai")
          ) {
            Column {
              Text(
                text = message.text,
                color = colors.textPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
              )

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(LinguaPrimary.copy(alpha = 0.08f))
                    .border(
                      width = 0.5.dp,
                      color = LinguaPrimary.copy(alpha = 0.2f),
                      shape = CircleShape
                    )
                ) {
                  IconButton(
                    onClick = { onPlayAudio(message.text) },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.VolumeUp,
                      contentDescription = "Listen to teacher",
                      tint = LinguaPrimary,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }

          // Inline correction if attached to this turn
          message.correction?.let { corr ->
            Spacer(modifier = Modifier.height(10.dp))
            CorrectionCard(
              correction = corr,
              onTryAgain = { onTryAgainCorrection(corr) },
              onContinue = onContinueCorrection,
              onPlayCorrectedAudio = { onPlayAudio(corr.correctedSentence) }
            )
          }
        }
      }
    }
  }
}
