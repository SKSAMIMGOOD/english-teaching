package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.LinguaBottomNav
import com.example.ui.screens.AiConversationScreen
import com.example.ui.screens.ConversationSummaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VoiceConversationScreen
import com.example.ui.theme.LinguaTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LinguaViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LinguaApp()
    }
  }
}

@Composable
fun LinguaApp(viewModel: LinguaViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val currentTab by viewModel.currentTab.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val recentSessions by viewModel.recentSessions.collectAsState()
  val messages by viewModel.messages.collectAsState()
  val isAiThinking by viewModel.isAiThinking.collectAsState()
  val isAiSpeaking by viewModel.isAiSpeaking.collectAsState()
  val isListening by viewModel.isListening.collectAsState()
  val speechRms by viewModel.speechRms.collectAsState()
  val activeCorrection by viewModel.activeCorrection.collectAsState()
  val lastFinishedSession by viewModel.lastFinishedSession.collectAsState()
  val sessionCorrections by viewModel.sessionCorrections.collectAsState()

  val isDark = userProfile?.isDarkMode ?: isSystemInDarkTheme()

  LinguaTheme(darkTheme = isDark) {
    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
      when (screen) {
        AppScreen.SPLASH -> {
          SplashScreen()
        }

        AppScreen.ONBOARDING -> {
          OnboardingScreen(
            onGetStarted = { viewModel.completeOnboarding() },
            onSkip = { viewModel.skipOnboarding() }
          )
        }

        AppScreen.PROFILE_SETUP -> {
          ProfileSetupScreen(
            initialName = userProfile?.name ?: "Alex",
            onStartLearning = { name, level, goal ->
              viewModel.completeProfileSetup(name, level, goal)
            }
          )
        }

        AppScreen.VOICE_MODE -> {
          VoiceConversationScreen(
            messages = messages,
            isListening = isListening,
            isAiSpeaking = isAiSpeaking,
            speechRms = speechRms,
            onToggleListening = { viewModel.toggleListening() },
            onStopSpeaking = { viewModel.aiTeacherService.stopSpeaking() },
            onBackToChat = { viewModel.exitVoiceMode() },
            onEndConversation = { viewModel.endConversation() }
          )
        }

        AppScreen.CONVERSATION_SUMMARY -> {
          ConversationSummaryScreen(
            session = lastFinishedSession,
            corrections = sessionCorrections,
            onPracticeAgain = { viewModel.practiceAgainFromSummary() },
            onBackToHome = { viewModel.backToHomeFromSummary() }
          )
        }

        AppScreen.MAIN -> {
          Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
              LinguaBottomNav(
                currentTab = currentTab,
                onTabSelected = { tab -> viewModel.navigateToTab(tab) }
              )
            }
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
              when (currentTab) {
                MainTab.HOME -> {
                  HomeScreen(
                    userProfile = userProfile,
                    recentSessions = recentSessions,
                    onStartSpeaking = {
                      viewModel.navigateToTab(MainTab.PRACTICE)
                    },
                    onStartTextChat = {
                      viewModel.navigateToTab(MainTab.PRACTICE)
                    },
                    onViewProgress = {
                      viewModel.navigateToTab(MainTab.PROGRESS)
                    }
                  )
                }

                MainTab.PRACTICE -> {
                  AiConversationScreen(
                    messages = messages,
                    isAiThinking = isAiThinking,
                    isAiSpeaking = isAiSpeaking,
                    isListening = isListening,
                    speechRms = speechRms,
                    activeCorrection = activeCorrection,
                    onSendMessage = { text -> viewModel.sendUserMessage(text) },
                    onToggleListening = { viewModel.toggleListening() },
                    onPlayAudio = { text -> viewModel.replayAiAudio(text) },
                    onOpenVoiceMode = { viewModel.startVoiceMode() },
                    onEndConversation = { viewModel.endConversation() },
                    onRetryCorrection = { corr -> viewModel.retryCorrection(corr) },
                    onDismissCorrection = { viewModel.dismissCorrection() }
                  )
                }

                MainTab.PROGRESS -> {
                  ProgressScreen(userProfile = userProfile)
                }

                MainTab.PROFILE -> {
                  ProfileScreen(
                    userProfile = userProfile,
                    onUpdateVoiceSpeed = { speed -> viewModel.updateVoiceSpeed(speed) },
                    onUpdateCorrectionPreference = { pref -> viewModel.updateCorrectionPreference(pref) },
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onResetData = { viewModel.resetUserData() },
                    onEditProfile = { viewModel.completeOnboarding() }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

// Kept for backward compatibility with existing unit/screenshot tests
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}
