package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AvailableRoleplayScenarios
import com.example.data.model.ChatMessage
import com.example.data.model.PracticeMode
import com.example.data.model.RoleplayScenario
import com.example.data.model.SessionCorrection
import com.example.ui.components.AiAvatar
import com.example.ui.components.AudioWaveformView
import com.example.ui.components.ChatBubble
import com.example.ui.components.CorrectionCard
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.theme.FrostedGlassBorderLight
import com.example.ui.theme.FrostedGlassBorderSubtle
import com.example.ui.theme.FrostedGlassDark
import com.example.ui.theme.FrostedGlassDarkBorder
import com.example.ui.theme.FrostedGlassWhiteSubtle
import com.example.ui.theme.FrostedHeroCyan
import com.example.ui.theme.FrostedHeroIndigoEnd
import com.example.ui.theme.FrostedHeroIndigoStart
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary
import com.example.ui.theme.LinguaSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiConversationScreen(
  messages: List<ChatMessage>,
  isAiThinking: Boolean,
  isAiSpeaking: Boolean,
  isListening: Boolean,
  speechRms: Float,
  activeCorrection: SessionCorrection?,
  onSendMessage: (String) -> Unit,
  onToggleListening: () -> Unit,
  onPlayAudio: (String) -> Unit,
  onOpenVoiceMode: () -> Unit,
  onEndConversation: () -> Unit,
  onRetryCorrection: (SessionCorrection) -> Unit,
  onDismissCorrection: () -> Unit,
  practiceMode: PracticeMode = PracticeMode.FREE_CONVERSATION,
  roleplayScenario: RoleplayScenario? = null,
  onChangeMode: (PracticeMode, RoleplayScenario?) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var textInput by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val isDark = isSystemInDarkTheme()
  var showModeDialog by remember { mutableStateOf(false) }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      onToggleListening()
    }
  }

  fun requestMicAndListen() {
    val permission = Manifest.permission.RECORD_AUDIO
    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
      onToggleListening()
    } else {
      permissionLauncher.launch(permission)
    }
  }

  // Auto-scroll on new messages or thinking state
  LaunchedEffect(messages.size, isAiThinking, activeCorrection) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  if (showModeDialog) {
    BasicAlertDialog(
      onDismissRequest = { showModeDialog = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(28.dp))
          .background(MaterialTheme.colorScheme.surface)
          .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
          .padding(20.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Switch Practice Mode",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showModeDialog = false },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4 Modes
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Free conversation
            ModeSelectRow(
              emoji = "💬",
              title = "Free Conversation",
              subtitle = "Everyday natural chat",
              isSelected = practiceMode == PracticeMode.FREE_CONVERSATION && roleplayScenario == null,
              onClick = {
                showModeDialog = false
                onChangeMode(PracticeMode.FREE_CONVERSATION, null)
              }
            )

            // AI Questions
            ModeSelectRow(
              emoji = "🎯",
              title = "AI Question Practice",
              subtitle = "Targeted questions & feedback",
              isSelected = practiceMode == PracticeMode.AI_QUESTIONS,
              onClick = {
                showModeDialog = false
                onChangeMode(PracticeMode.AI_QUESTIONS, null)
              }
            )

            // Speak & Correct
            ModeSelectRow(
              emoji = "📝",
              title = "Speak & Correct",
              subtitle = "In-depth grammar evaluation",
              isSelected = practiceMode == PracticeMode.SPEAK_AND_CORRECT,
              onClick = {
                showModeDialog = false
                onChangeMode(PracticeMode.SPEAK_AND_CORRECT, null)
              }
            )

            Text(
              text = "Or choose a scenario:",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            AvailableRoleplayScenarios.take(3).forEach { sc ->
              ModeSelectRow(
                emoji = sc.iconEmoji,
                title = sc.title,
                subtitle = sc.subtitle,
                isSelected = practiceMode == PracticeMode.REAL_LIFE_SITUATIONS && roleplayScenario?.id == sc.id,
                onClick = {
                  showModeDialog = false
                  onChangeMode(PracticeMode.REAL_LIFE_SITUATIONS, sc)
                }
              )
            }
          }
        }
      }
    }
  }

  FrostedBackgroundContainer(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .imePadding()
    ) {
      // 1. Frosted Glass Top Header Bar
      val barBg = if (isDark) FrostedGlassDark else FrostedGlassWhiteSubtle
      val barBorder = if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            spotColor = Color(0x144F46E5)
          )
          .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
          .background(barBg)
          .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
              listOf(
                Color.White.copy(alpha = 0.5f),
                barBorder
              )
            ),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
          )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            AiAvatar(
              size = 42.dp,
              isSpeaking = isAiSpeaking,
              isThinking = isAiThinking,
              showOnlineIndicator = true
            )

            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "AI English Teacher",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LinguaSuccess)
                )
              }

              Text(
                text = when {
                  isAiSpeaking -> "Speaking aloud..."
                  isAiThinking -> "Thinking..."
                  isListening -> "Listening to you..."
                  else -> "Online & ready to chat"
                },
                fontSize = 12.sp,
                color = if (isAiSpeaking || isListening) LinguaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Header Actions: Voice Mode button & End Session
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Switch to dedicated full-screen voice orb mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(LinguaPrimary.copy(alpha = 0.12f))
                .border(
                  width = 1.dp,
                  color = LinguaPrimary.copy(alpha = 0.25f),
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onOpenVoiceMode)
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .testTag("open_voice_mode_button")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.GraphicEq,
                  contentDescription = null,
                  tint = LinguaPrimary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Voice",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = LinguaPrimary
                )
              }
            }

            // Finish / End session button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                .border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onEndConversation)
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .testTag("end_conversation_button")
            ) {
              Text(
                text = "Finish",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Active Mode Strip with Switcher
      val modeTitle = when (practiceMode) {
        PracticeMode.FREE_CONVERSATION -> "Free Conversation"
        PracticeMode.AI_QUESTIONS -> "AI Question Practice"
        PracticeMode.REAL_LIFE_SITUATIONS -> roleplayScenario?.title ?: "Real-Life Situation"
        PracticeMode.SPEAK_AND_CORRECT -> "Speak & Correct"
      }
      val modeEmoji = when (practiceMode) {
        PracticeMode.FREE_CONVERSATION -> "💬"
        PracticeMode.AI_QUESTIONS -> "🎯"
        PracticeMode.REAL_LIFE_SITUATIONS -> roleplayScenario?.iconEmoji ?: "🎭"
        PracticeMode.SPEAK_AND_CORRECT -> "📝"
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(text = modeEmoji, fontSize = 14.sp)
          Text(
            text = modeTitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(LinguaPrimary.copy(alpha = 0.1f))
            .clickable { showModeDialog = true }
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Switch Mode",
            tint = LinguaPrimary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Switch Mode",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = LinguaPrimary
          )
        }
      }

      // 2. Chat Conversation History Area
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          items(messages, key = { it.id }) { msg ->
            ChatBubble(
              message = msg,
              onPlayAudio = onPlayAudio,
              onTryAgainCorrection = onRetryCorrection,
              onContinueCorrection = onDismissCorrection
            )
          }

          // AI Thinking Shimmer Indicator
          if (isAiThinking) {
            item(key = "ai_thinking") {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .padding(vertical = 8.dp)
                  .testTag("ai_thinking_indicator")
              ) {
                AiAvatar(size = 32.dp, isThinking = true, showOnlineIndicator = false)
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) FrostedGlassDark else FrostedGlassWhiteSubtle)
                    .border(
                      width = 1.dp,
                      color = if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight,
                      shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = null,
                      tint = LinguaPrimary,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = "Thinking...",
                      fontSize = 13.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 3. Floating Active Correction Banner (if visible at bottom)
      activeCorrection?.let { correction ->
        AnimatedVisibility(
          visible = true,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            CorrectionCard(
              correction = correction,
              onTryAgain = { onRetryCorrection(correction) },
              onContinue = onDismissCorrection,
              onPlayCorrectedAudio = { onPlayAudio(correction.correctedSentence) }
            )
          }
        }
      }

      // 4. Live Listening / Waveform Feedback Indicator
      AnimatedVisibility(
        visible = isListening || isAiSpeaking,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(if (isDark) FrostedGlassDark.copy(alpha = 0.9f) else FrostedGlassWhiteSubtle)
            .border(
              width = 0.5.dp,
              color = LinguaPrimary.copy(alpha = 0.2f),
              shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = if (isListening) "🎙️ Listening to you..." else "🔊 AI Speaking...",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = LinguaPrimary
            )
            AudioWaveformView(
              isActive = true,
              amplitude = if (isListening) speechRms else 0.6f,
              barCount = 14,
              maxBarHeight = 22.dp
            )
          }
        }
      }

      // 5. Input Controls: Frosted Glass Bottom Panel
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            spotColor = Color(0x1A4F46E5)
          )
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(barBg)
          .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
              listOf(
                Color.White.copy(alpha = 0.6f),
                barBorder
              )
            ),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
          )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Large Prominent Microphone Action Button ("Tap to speak")
          Box(
            modifier = Modifier
              .size(50.dp)
              .shadow(
                elevation = 6.dp,
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
              .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.35f),
                shape = CircleShape
              )
              .clickable(onClick = { requestMicAndListen() })
              .testTag("chat_mic_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
              contentDescription = if (isListening) "Stop listening" else "Tap to speak",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          // Text input field with frosted borders
          OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            placeholder = {
              Text(
                text = if (isListening) "Listening..." else "Type or tap mic...",
                fontSize = 14.sp
              )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = LinguaPrimary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
              focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
              unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
              onSend = {
                if (textInput.isNotBlank()) {
                  val toSend = textInput
                  textInput = ""
                  onSendMessage(toSend)
                }
              }
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("chat_text_input")
          )

          // Send button
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(
                if (textInput.isNotBlank()) LinguaPrimary else MaterialTheme.colorScheme.surfaceVariant
              )
              .clickable(
                enabled = textInput.isNotBlank(),
                onClick = {
                  if (textInput.isNotBlank()) {
                    val toSend = textInput
                    textInput = ""
                    onSendMessage(toSend)
                  }
                }
              )
              .testTag("chat_send_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = "Send message",
              tint = if (textInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ModeSelectRow(
  emoji: String,
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (isSelected) LinguaPrimary.copy(alpha = 0.12f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      )
      .border(
        width = 1.dp,
        color = if (isSelected) LinguaPrimary else Color.Transparent,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = emoji, fontSize = 20.sp)
    Spacer(modifier = Modifier.width(10.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) LinguaPrimary else MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    if (isSelected) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(LinguaPrimary)
      )
    }
  }
}

