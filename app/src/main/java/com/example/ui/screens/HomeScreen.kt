package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.ConversationSession
import com.example.data.model.UserProfile
import com.example.ui.components.AiAvatar
import com.example.ui.components.FrostedBackgroundContainer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.MetricProgressBar
import com.example.ui.theme.FrostedFluencyGreen
import com.example.ui.theme.FrostedHeroCyan
import com.example.ui.theme.FrostedHeroIndigoEnd
import com.example.ui.theme.FrostedHeroIndigoStart
import com.example.ui.theme.LinguaPrimary
import com.example.ui.theme.LinguaSecondary

@Composable
fun HomeScreen(
  userProfile: UserProfile?,
  recentSessions: List<ConversationSession>,
  onStartSpeaking: () -> Unit,
  onStartTextChat: () -> Unit,
  onViewProgress: () -> Unit,
  modifier: Modifier = Modifier
) {
  val name = userProfile?.name ?: "Maya"

  FrostedBackgroundContainer(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // 1. Header Greeting with Profile Ring
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Good morning, $name 👋",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Ready to practice English?",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Profile Avatar Ring
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(LinguaPrimary.copy(alpha = 0.12f))
            .border(1.dp, LinguaPrimary.copy(alpha = 0.25f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(LinguaPrimary)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Main Hero Card with Frosted Atmosphere and Internal Luminous Orbs
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(32.dp),
            spotColor = LinguaPrimary.copy(alpha = 0.35f),
            ambientColor = LinguaPrimary.copy(alpha = 0.15f)
          )
          .clip(RoundedCornerShape(32.dp))
          .background(
            brush = Brush.linearGradient(
              colors = listOf(
                FrostedHeroIndigoStart,
                FrostedHeroIndigoEnd
              )
            )
          )
          .testTag("home_hero_card")
      ) {
        // Glowing Orb: Top Right (White diffuse blur)
        Box(
          modifier = Modifier
            .size(160.dp)
            .offset(x = 220.dp, y = (-50).dp)
            .clip(CircleShape)
            .background(
              brush = Brush.radialGradient(
                colors = listOf(
                  Color.White.copy(alpha = 0.14f),
                  Color.Transparent
                )
              )
            )
        )

        // Glowing Orb: Bottom Left (Cyan diffuse blur)
        Box(
          modifier = Modifier
            .size(150.dp)
            .offset(x = (-40).dp, y = 140.dp)
            .clip(CircleShape)
            .background(
              brush = Brush.radialGradient(
                colors = listOf(
                  FrostedHeroCyan.copy(alpha = 0.25f),
                  Color.Transparent
                )
              )
            )
        )

        // Card Content
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp)
        ) {
          // AI Teacher Identity with Frosted Glass Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.size(48.dp)) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.White.copy(alpha = 0.20f))
                  .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(16.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🤖", fontSize = 24.sp)
              }

              // Green online dot
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .align(Alignment.BottomEnd)
                  .offset(x = 2.dp, y = 2.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF4ADE80))
                  .border(2.dp, FrostedHeroIndigoEnd, CircleShape)
              )
            }

            Column {
              Text(
                text = "AI English Teacher",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Online & ready to chat",
                fontSize = 12.sp,
                color = Color(0xFFE0E7FF)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "\"Let's have a quick conversation about your weekend plans!\"",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color(0xFFEEF2FF)
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Action Buttons: Start Speaking (White) & Text Chat (Frosted Glass)
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Primary: Start Speaking
            Button(
              onClick = onStartSpeaking,
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF4338CA)
              ),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x33000000))
                .testTag("hero_start_speaking_button")
            ) {
              Text(text = "🎙️", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Start Speaking",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }

            // Secondary: Text Chat (Frosted Glass Button)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(
                  width = 1.dp,
                  color = Color.White.copy(alpha = 0.25f),
                  shape = RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onStartTextChat)
                .testTag("hero_text_chat_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Text Chat",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Grid of 2 Frosted Cards: Today's Goal & Fluency
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Card 1: Today's Goal
        FrostedGlassCard(
          modifier = Modifier
            .weight(1f)
            .testTag("home_todays_practice_card"),
          shape = RoundedCornerShape(24.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "TODAY'S GOAL",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "2/3",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(LinguaPrimary.copy(alpha = 0.1f))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "66%",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = LinguaPrimary
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar Track
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(0.66f)
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(LinguaPrimary)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "1 conversation to go",
              fontSize = 10.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        // Card 2: Fluency
        FrostedGlassCard(
          modifier = Modifier
            .weight(1f)
            .clickable(onClick = onViewProgress),
          shape = RoundedCornerShape(24.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "FLUENCY",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "B1",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "+12%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = FrostedFluencyGreen
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Intermediate level reached!",
              fontSize = 10.sp,
              lineHeight = 14.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Recent Practice Frosted Glass Card Container
      FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
      ) {
        Column {
          // Card Header with "See all" action
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(0.dp)
              )
              .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Recent Practice",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Text(
              text = "See all",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = LinguaPrimary,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onViewProgress)
                .padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }

          // Practice List Items
          Column(modifier = Modifier.padding(10.dp)) {
            // Item 1: Daily Conversation
            RecentPracticeRow(
              iconEmoji = "📅",
              iconBg = Color(0xFFECFEFF),
              iconTint = Color(0xFF0891B2),
              title = "Daily Conversation",
              subtitle = "8 min ago • 12 mins",
              xpBadge = "+42 XP",
              onClick = onStartSpeaking
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Item 2: Introducing Yourself
            RecentPracticeRow(
              iconEmoji = "👤",
              iconBg = Color(0xFFFFF7ED),
              iconTint = Color(0xFFEA580C),
              title = "Introducing Yourself",
              subtitle = "Yesterday • 5 mins",
              xpBadge = "+18 XP",
              onClick = onStartSpeaking
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 5. Progress Snapshot Frosted Card
      FrostedGlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onViewProgress
          )
          .testTag("home_progress_snapshot_card"),
        shape = RoundedCornerShape(24.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = LinguaSecondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Your Progress",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = "View Details",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          MetricProgressBar(
            label = "Speaking",
            percentage = 72,
            color = LinguaPrimary
          )

          Spacer(modifier = Modifier.height(10.dp))

          MetricProgressBar(
            label = "Grammar",
            percentage = 68,
            color = LinguaSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun RecentPracticeRow(
  iconEmoji: String,
  iconBg: Color,
  iconTint: Color,
  title: String,
  subtitle: String,
  xpBadge: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(iconBg),
      contentAlignment = Alignment.Center
    ) {
      Text(text = iconEmoji, fontSize = 20.sp)
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = Color(0xFF94A3B8)
      )
    }

    Text(
      text = xpBadge,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = FrostedFluencyGreen
    )
  }
}
