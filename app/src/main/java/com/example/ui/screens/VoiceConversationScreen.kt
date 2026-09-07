package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.ChatMessage
import com.example.ui.components.AudioWaveformView
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.VoiceOrb
import com.example.ui.theme.FrostedGlassBorderLight
import com.example.ui.theme.FrostedGlassDark
import com.example.ui.theme.FrostedGlassDarkBorder
import com.example.ui.theme.FrostedGlassWhite
import com.example.ui.theme.FrostedHeroIndigoEnd
import com.example.ui.theme.FrostedHeroIndigoStart
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary

@Composable
fun VoiceConversationScreen(
  messages: List<ChatMessage>,
  isListening: Boolean,
  isAiSpeaking: Boolean,
  speechRms: Float,
  onToggleListening: () -> Unit,
  onStopSpeaking: () -> Unit,
  onBackToChat: () -> Unit,
  onEndConversation: () -> Unit
) {
  val context = LocalContext.current
  val isDark = isSystemInDarkTheme()

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      onToggleListening()
    }
  }

  fun checkAndListen() {
    val permission = Manifest.permission.RECORD_AUDIO
    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
      onToggleListening()
    } else {
      permissionLauncher.launch(permission)
    }
  }

  val lastUserMessage = messages.findLast { it.isUser }?.text ?: ""
  val lastAiMessage = messages.findLast { !it.isUser }?.text ?: "Hi! How was your day today?"

  FrostedBackgroundContainer {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Top Bar: Frosted Glass actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) FrostedGlassDark else FrostedGlassWhite)
            .border(
              width = 1.dp,
              color = if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight,
              shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onBackToChat)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("voice_back_to_chat_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ChatBubbleOutline,
              contentDescription = null,
              tint = LinguaPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Text View",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = LinguaPrimary
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFEE2E2).copy(alpha = 0.9f))
            .border(
              width = 1.dp,
              color = Color(0xFFEF4444).copy(alpha = 0.3f),
              shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onEndConversation)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("voice_end_session_button")
        ) {
          Text(
            text = "End Session",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFDC2626)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 2. Frosted Status Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(24.dp))
          .background(
            when {
              isListening -> LinguaSecondary.copy(alpha = 0.15f)
              isAiSpeaking -> LinguaPrimary.copy(alpha = 0.15f)
              else -> if (isDark) FrostedGlassDark else FrostedGlassWhite
            }
          )
          .border(
            width = 1.dp,
            color = when {
              isListening -> LinguaSecondary.copy(alpha = 0.3f)
              isAiSpeaking -> LinguaPrimary.copy(alpha = 0.3f)
              else -> if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight
            },
            shape = RoundedCornerShape(24.dp)
          )
          .padding(horizontal = 18.dp, vertical = 8.dp)
      ) {
        Text(
          text = when {
            isListening -> "🎙️ Listening to you..."
            isAiSpeaking -> "🔊 AI Teacher is speaking..."
            else -> "Tap microphone to speak"
          },
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = when {
            isListening -> LinguaSecondary
            isAiSpeaking -> LinguaPrimary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
          }
        )
      }

      Spacer(modifier = Modifier.weight(0.5f))

      // 3. Central Voice Orb
      VoiceOrb(
        isListening = isListening,
        isAiSpeaking = isAiSpeaking,
        size = 180.dp
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Animated waveform bars
      AudioWaveformView(
        isActive = isListening || isAiSpeaking,
        amplitude = if (isListening) speechRms else 0.7f,
        barCount = 22,
        maxBarHeight = 36.dp
      )

      Spacer(modifier = Modifier.weight(0.8f))

      // 4. Live Transcription Subtitles Area (Frosted Glass Container)
      FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = 6.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (lastUserMessage.isNotBlank()) {
            Text(
              text = "You: \"$lastUserMessage\"",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
          }

          Text(
            text = "AI: \"$lastAiMessage\"",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(26.dp))

      // 5. Voice Mode Bottom Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Mute/Stop speaker frosted button
        Box(
          modifier = Modifier
            .size(54.dp)
            .shadow(
              elevation = 4.dp,
              shape = CircleShape,
              spotColor = Color(0x1A4F46E5)
            )
            .clip(CircleShape)
            .background(if (isDark) FrostedGlassDark else FrostedGlassWhite)
            .border(
              1.dp,
              if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight,
              CircleShape
            )
            .clickable(onClick = onStopSpeaking),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isAiSpeaking) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
            contentDescription = "Stop audio",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Center Main Mic Button (Large 74dp)
        Box(
          modifier = Modifier
            .size(74.dp)
            .shadow(
              elevation = 8.dp,
              shape = CircleShape,
              spotColor = if (isListening) Color(0xFFEF4444) else LinguaPrimary
            )
            .clip(CircleShape)
            .background(
              brush = Brush.linearGradient(
                if (isListening) {
                  listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                } else {
                  listOf(FrostedHeroIndigoStart, FrostedHeroIndigoEnd)
                }
              )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            .clickable(onClick = { checkAndListen() })
            .testTag("voice_mode_mic_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = if (isListening) "Stop recording" else "Speak",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
          )
        }

        // Back to chat frosted button
        Box(
          modifier = Modifier
            .size(54.dp)
            .shadow(
              elevation = 4.dp,
              shape = CircleShape,
              spotColor = Color(0x1A4F46E5)
            )
            .clip(CircleShape)
            .background(if (isDark) FrostedGlassDark else FrostedGlassWhite)
            .border(
              1.dp,
              if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight,
              CircleShape
            )
            .clickable(onClick = onBackToChat),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "Open Chat",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
