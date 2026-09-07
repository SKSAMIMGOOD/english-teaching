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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.theme.FrostedFluencyGreen
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary

@Composable
fun ProfileScreen(
  userProfile: UserProfile?,
  onUpdateVoiceSpeed: (Float) -> Unit,
  onUpdateCorrectionPreference: (String) -> Unit,
  onToggleDarkMode: () -> Unit,
  onResetData: () -> Unit,
  onEditProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val profile = userProfile ?: UserProfile()
  var dailyReminderEnabled by remember { mutableStateOf(true) }
  var showResetDialog by remember { mutableStateOf(false) }

  FrostedBackgroundContainer(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp)
        .testTag("profile_screen")
    ) {
      Text(
        text = "Profile & Settings",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(18.dp))

      // 1. User Profile Header Frosted Card
      FrostedGlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("profile_info_card"),
        shape = RoundedCornerShape(28.dp),
        elevation = 4.dp
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(LinguaPrimary.copy(alpha = 0.15f))
              .border(1.dp, LinguaPrimary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = LinguaPrimary,
              modifier = Modifier.size(30.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = profile.name,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "Level: ${profile.englishLevel}",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Goal: ${profile.learningGoal}",
              fontSize = 13.sp,
              color = LinguaPrimary,
              fontWeight = FontWeight.SemiBold
            )
          }

          IconButtonAction(
            icon = Icons.Default.Edit,
            description = "Edit profile",
            onClick = onEditProfile
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Preferences",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Voice Speaking Speed Setting Card
      FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = 3.dp
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = LinguaPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "AI Speaking Speed",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            val speedText = String.format("%.1fx", profile.voiceSpeed)
            Text(
              text = speedText,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = LinguaPrimary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Slider(
            value = profile.voiceSpeed,
            onValueChange = { onUpdateVoiceSpeed(it) },
            valueRange = 0.8f..1.2f,
            steps = 3,
            colors = SliderDefaults.colors(
              thumbColor = LinguaPrimary,
              activeTrackColor = LinguaPrimary
            ),
            modifier = Modifier.testTag("voice_speed_slider")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("0.8x (Slower)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("1.0x (Normal)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("1.2x (Faster)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. AI Correction Preference ("Gentle" / "Thorough")
      FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = 3.dp
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = LinguaSecondary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AI Correction Style",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val styles = listOf("Gentle", "Thorough")
            styles.forEach { style ->
              val isSelected = profile.correctionPreference == style
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(14.dp))
                  .background(
                    if (isSelected) LinguaPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                  )
                  .border(
                    1.dp,
                    if (isSelected) LinguaPrimary else Color.Transparent,
                    RoundedCornerShape(14.dp)
                  )
                  .clickable { onUpdateCorrectionPreference(style) }
                  .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (style == "Gentle") "🌱 Gentle" else "🔍 Thorough",
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) LinguaPrimary else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Daily Reminder Toggle
      SettingToggleItem(
        icon = Icons.Default.Notifications,
        iconColor = LinguaPrimary,
        title = "Daily Practice Reminder",
        subtitle = "Gentle notification to practice 5 min daily",
        checked = dailyReminderEnabled,
        onCheckedChange = { dailyReminderEnabled = it }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 5. Dark Theme Toggle
      SettingToggleItem(
        icon = Icons.Default.DarkMode,
        iconColor = LinguaSecondary,
        title = "Dark Theme",
        subtitle = "Eye-safe appearance for night sessions",
        checked = profile.isDarkMode,
        onCheckedChange = { onToggleDarkMode() }
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 6. Reset Practice Data Button
      OutlinedButton(
        onClick = { showResetDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("reset_data_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Reset Practice History", fontWeight = FontWeight.SemiBold)
      }

      Spacer(modifier = Modifier.height(24.dp))

      if (showResetDialog) {
        AlertDialog(
          onDismissRequest = { showResetDialog = false },
          title = { Text("Reset Practice Data?") },
          text = { Text("This will clear your conversation history and reset your practice scores.") },
          confirmButton = {
            Button(
              onClick = {
                showResetDialog = false
                onResetData()
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
              Text("Reset")
            }
          },
          dismissButton = {
            TextButton(onClick = { showResetDialog = false }) {
              Text("Cancel")
            }
          }
        )
      }
    }
  }
}

@Composable
private fun SettingToggleItem(
  icon: ImageVector,
  iconColor: Color,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  FrostedGlassCard(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    elevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier.size(22.dp)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = LinguaPrimary
        )
      )
    }
  }
}

@Composable
private fun IconButtonAction(
  icon: ImageVector,
  description: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(38.dp)
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
      .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), CircleShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = description,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(18.dp)
    )
  }
}
