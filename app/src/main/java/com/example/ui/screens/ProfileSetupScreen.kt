package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.theme.FrostedGlassBorderLight
import com.example.ui.theme.FrostedGlassDark
import com.example.ui.theme.FrostedGlassDarkBorder
import com.example.ui.theme.FrostedGlassWhite
import com.example.ui.theme.FrostedGlassWhiteSubtle
import com.example.ui.theme.LinguaPrimary

@Composable
fun ProfileSetupScreen(
  initialName: String = "Alex",
  onStartLearning: (name: String, level: String, goal: String) -> Unit
) {
  var name by remember { mutableStateOf(initialName) }
  var selectedLevel by remember { mutableStateOf("Beginner") }
  var selectedGoal by remember { mutableStateOf("Daily conversation") }

  val levels = listOf(
    "Beginner" to "I know basic words but struggle to speak",
    "Intermediate" to "I can talk with some pauses and mistakes",
    "Advanced" to "I speak well but want higher fluency",
    "Not sure" to "Let the AI teacher assess my level"
  )

  val goals = listOf(
    "Daily conversation" to "💬 Natural everyday chats & socializing",
    "Speaking" to "🎙️ Speak fluently without nervousness",
    "Job" to "💼 Career growth & professional settings",
    "Interview" to "🎯 Ace English job or visa interviews",
    "College" to "🎓 Studies, lectures & academic discussions"
  )

  FrostedBackgroundContainer {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
      Text(
        text = "Let's personalize your teacher",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Your AI teacher will adapt its vocabulary and speech rate to fit you perfectly.",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 1. Name Input
      Text(
        text = "What's your name?",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        placeholder = { Text("Enter your name") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LinguaPrimary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
          focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
          unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("setup_name_input")
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 2. English Level Selection
      Text(
        text = "What's your English level?",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(10.dp))

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        levels.forEach { (level, desc) ->
          val isSelected = selectedLevel == level
          SelectableCard(
            title = level,
            subtitle = desc,
            isSelected = isSelected,
            onClick = { selectedLevel = level },
            testTag = "level_${level.lowercase().replace(" ", "_")}"
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 3. Learning Goal Selection
      Text(
        text = "Why do you want to improve English?",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(10.dp))

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        goals.forEach { (goal, desc) ->
          val isSelected = selectedGoal == goal
          SelectableCard(
            title = goal,
            subtitle = desc,
            isSelected = isSelected,
            onClick = { selectedGoal = goal },
            testTag = "goal_${goal.lowercase().replace(" ", "_")}"
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // 4. Start Learning Button
      Button(
        onClick = {
          onStartLearning(name.ifBlank { "Alex" }, selectedLevel, selectedGoal)
        },
        colors = ButtonDefaults.buttonColors(containerColor = LinguaPrimary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = LinguaPrimary.copy(alpha = 0.3f))
          .testTag("start_learning_button")
      ) {
        Text(
          text = "Start Learning",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(8.dp))
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun SelectableCard(
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  val isDark = isSystemInDarkTheme()
  val baseBg = if (isDark) FrostedGlassDark else FrostedGlassWhite
  val baseBorder = if (isDark) FrostedGlassDarkBorder else FrostedGlassBorderLight

  Box(
    modifier = Modifier
      .testTag(testTag)
      .fillMaxWidth()
      .shadow(
        elevation = if (isSelected) 6.dp else 2.dp,
        shape = RoundedCornerShape(20.dp),
        spotColor = if (isSelected) LinguaPrimary.copy(alpha = 0.25f) else Color(0x140F172A)
      )
      .clip(RoundedCornerShape(20.dp))
      .background(
        if (isSelected) LinguaPrimary.copy(alpha = 0.12f) else baseBg
      )
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        brush = if (isSelected) {
          Brush.linearGradient(listOf(LinguaPrimary, LinguaPrimary.copy(alpha = 0.7f)))
        } else {
          Brush.linearGradient(
            listOf(
              if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.9f),
              baseBorder
            )
          )
        },
        shape = RoundedCornerShape(20.dp)
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(16.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = if (isSelected) LinguaPrimary else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = subtitle,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(if (isSelected) LinguaPrimary else Color.Transparent)
          .border(
            width = 1.5.dp,
            color = if (isSelected) LinguaPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}
