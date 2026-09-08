package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary
import com.example.ui.theme.LinguaTheme
import com.example.ui.viewmodel.MainTab

@Composable
fun LinguaBottomNav(
  currentTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LinguaTheme.isDark
  val colors = LinguaTheme.colors
  val navBg = colors.cardBackground
  val navBorder = colors.borderSubtle

  Box(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        spotColor = if (isDark) Color(0x33000000) else Color(0x1F4F46E5),
        ambientColor = if (isDark) Color(0x1F000000) else Color(0x140F172A)
      )
      .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .background(navBg)
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.85f),
            navBorder
          )
        ),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
      )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Home
      NavTabItem(
        label = "Home",
        isSelected = currentTab == MainTab.HOME,
        onClick = { onTabSelected(MainTab.HOME) },
        icon = {
          Icon(
            imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
            contentDescription = "Home",
            modifier = Modifier.size(22.dp)
          )
        },
        testTag = "tab_home"
      )

      // 2. Practice (Center Elevated Frosted Pill)
      Box(
        modifier = Modifier
          .testTag("tab_practice")
          .clip(RoundedCornerShape(22.dp))
          .background(
            brush = Brush.horizontalGradient(
              listOf(LinguaPrimary, LinguaSecondary)
            )
          )
          .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.35f),
            shape = RoundedCornerShape(22.dp)
          )
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onTabSelected(MainTab.PRACTICE) }
          )
          .padding(horizontal = 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = "Practice",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "Practice",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }

      // 3. Progress
      NavTabItem(
        label = "Progress",
        isSelected = currentTab == MainTab.PROGRESS,
        onClick = { onTabSelected(MainTab.PROGRESS) },
        icon = {
          Icon(
            imageVector = if (currentTab == MainTab.PROGRESS) Icons.Filled.TrendingUp else Icons.Outlined.TrendingUp,
            contentDescription = "Progress",
            modifier = Modifier.size(22.dp)
          )
        },
        testTag = "tab_progress"
      )

      // 4. Profile
      NavTabItem(
        label = "Profile",
        isSelected = currentTab == MainTab.PROFILE,
        onClick = { onTabSelected(MainTab.PROFILE) },
        icon = {
          Icon(
            imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
            contentDescription = "Profile",
            modifier = Modifier.size(22.dp)
          )
        },
        testTag = "tab_profile"
      )
    }
  }
}

@Composable
private fun NavTabItem(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  icon: @Composable () -> Unit,
  testTag: String
) {
  val contentColor = if (isSelected) LinguaTheme.colors.primary else LinguaTheme.colors.textMuted

  Column(
    modifier = Modifier
      .testTag(testTag)
      .clip(RoundedCornerShape(14.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(
          if (isSelected) LinguaPrimary.copy(alpha = 0.10f) else Color.Transparent
        ),
      contentAlignment = Alignment.Center
    ) {
      androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.material3.LocalContentColor provides contentColor
      ) {
        icon()
      }
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
      color = contentColor
    )
  }
}
