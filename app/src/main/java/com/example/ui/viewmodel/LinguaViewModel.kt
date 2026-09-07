package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiTeacherService
import com.example.data.db.LinguaDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.ConversationSession
import com.example.data.model.SessionCorrection
import com.example.data.model.UserProfile
import com.example.data.repository.LinguaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
  SPLASH,
  ONBOARDING,
  PROFILE_SETUP,
  MAIN,
  VOICE_MODE,
  CONVERSATION_SUMMARY
}

enum class MainTab {
  HOME,
  PRACTICE,
  PROGRESS,
  PROFILE
}

class LinguaViewModel(application: Application) : AndroidViewModel(application) {

  private val database = LinguaDatabase.getDatabase(application, viewModelScope)
  val repository = LinguaRepository(database.linguaDao())
  val aiTeacherService = AiTeacherService(application)

  val userProfile: StateFlow<UserProfile?> = repository.userProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val recentSessions: StateFlow<List<ConversationSession>> = repository.recentSessions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allSessions: StateFlow<List<ConversationSession>> = repository.allSessions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recentCorrections: StateFlow<List<SessionCorrection>> = repository.recentCorrections
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _currentTab = MutableStateFlow(MainTab.HOME)
  val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

  // Chat & Conversation state
  private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

  private val _isAiThinking = MutableStateFlow(false)
  val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

  private val _activeCorrection = MutableStateFlow<SessionCorrection?>(null)
  val activeCorrection: StateFlow<SessionCorrection?> = _activeCorrection.asStateFlow()

  private val _sessionCorrections = MutableStateFlow<List<SessionCorrection>>(emptyList())
  val sessionCorrections: StateFlow<List<SessionCorrection>> = _sessionCorrections.asStateFlow()

  private val _sessionStartTime = MutableStateFlow(0L)
  val sessionStartTime: StateFlow<Long> = _sessionStartTime.asStateFlow()

  private val _lastFinishedSession = MutableStateFlow<ConversationSession?>(null)
  val lastFinishedSession: StateFlow<ConversationSession?> = _lastFinishedSession.asStateFlow()

  val isAiSpeaking: StateFlow<Boolean> = aiTeacherService.isAiSpeaking
  val isListening: StateFlow<Boolean> = aiTeacherService.isListening
  val speechRms: StateFlow<Float> = aiTeacherService.speechRms
  val recognizedLiveText: StateFlow<String> = aiTeacherService.recognizedText

  init {
    viewModelScope.launch {
      delay(1600) // Brief splash display
      val profile = repository.getUserProfileOnce()
      if (profile == null || !profile.isOnboarded) {
        _currentScreen.value = AppScreen.ONBOARDING
      } else {
        _currentScreen.value = AppScreen.MAIN
      }
    }
  }

  fun completeOnboarding() {
    _currentScreen.value = AppScreen.PROFILE_SETUP
  }

  fun skipOnboarding() {
    _currentScreen.value = AppScreen.PROFILE_SETUP
  }

  fun completeProfileSetup(name: String, level: String, goal: String) {
    viewModelScope.launch {
      val existing = userProfile.value
      val updated = (existing ?: UserProfile()).copy(
        name = name.ifBlank { "Alex" },
        englishLevel = level,
        learningGoal = goal,
        isOnboarded = true
      )
      repository.saveUserProfile(updated)
      _currentScreen.value = AppScreen.MAIN
      _currentTab.value = MainTab.HOME
    }
  }

  fun navigateToTab(tab: MainTab) {
    _currentTab.value = tab
    if (tab == MainTab.PRACTICE && _messages.value.isEmpty()) {
      startNewConversation()
    }
  }

  fun startNewConversation() {
    _sessionStartTime.value = System.currentTimeMillis()
    _sessionCorrections.value = emptyList()
    _activeCorrection.value = null
    aiTeacherService.stopSpeaking()

    val profile = userProfile.value
    val name = profile?.name ?: "there"
    val initialAiMsg = ChatMessage(
      isUser = false,
      text = "Hi $name! How was your day today?"
    )
    _messages.value = listOf(initialAiMsg)

    // Play greeting audio
    viewModelScope.launch {
      delay(400)
      aiTeacherService.speak(initialAiMsg.text, profile?.voiceSpeed ?: 1.0f)
    }
  }

  fun startVoiceMode() {
    _currentScreen.value = AppScreen.VOICE_MODE
    if (_messages.value.isEmpty()) {
      startNewConversation()
    }
  }

  fun exitVoiceMode() {
    aiTeacherService.stopListening()
    aiTeacherService.stopSpeaking()
    _currentScreen.value = AppScreen.MAIN
  }

  fun toggleListening() {
    if (isListening.value) {
      aiTeacherService.stopListening()
    } else {
      aiTeacherService.stopSpeaking()
      aiTeacherService.startListening { transcribedText ->
        if (transcribedText.isNotBlank()) {
          sendUserMessage(transcribedText)
        }
      }
    }
  }

  fun sendUserMessage(text: String) {
    if (text.isBlank()) return
    aiTeacherService.stopSpeaking()
    val userMsg = ChatMessage(isUser = true, text = text.trim())
    _messages.value = _messages.value + userMsg

    _isAiThinking.value = true
    viewModelScope.launch {
      val profile = userProfile.value
      val response = aiTeacherService.generateTeacherResponse(
        userMessage = userMsg.text,
        history = _messages.value,
        userLevel = profile?.englishLevel ?: "Beginner",
        userGoal = profile?.learningGoal ?: "Daily conversation",
        userName = profile?.name ?: "Friend",
        correctionPreference = profile?.correctionPreference ?: "Gentle"
      )

      _isAiThinking.value = false

      if (response.correction != null) {
        _activeCorrection.value = response.correction
        _sessionCorrections.value = _sessionCorrections.value + response.correction
      }

      val aiMsg = ChatMessage(
        isUser = false,
        text = response.replyText,
        correction = response.correction
      )
      _messages.value = _messages.value + aiMsg

      // Speak AI reply
      aiTeacherService.speak(aiMsg.text, profile?.voiceSpeed ?: 1.0f)
    }
  }

  fun dismissCorrection() {
    _activeCorrection.value = null
  }

  fun retryCorrection(correction: SessionCorrection) {
    // Fill the corrected sentence into user focus or encourage user to repeat it aloud
    _activeCorrection.value = null
    viewModelScope.launch {
      val prompt = "Great! Say this sentence aloud: \"${correction.correctedSentence}\""
      aiTeacherService.speak(prompt, userProfile.value?.voiceSpeed ?: 1.0f)
    }
  }

  fun replayAiAudio(text: String) {
    aiTeacherService.stopSpeaking()
    aiTeacherService.speak(text, userProfile.value?.voiceSpeed ?: 1.0f)
  }

  fun endConversation() {
    aiTeacherService.stopSpeaking()
    aiTeacherService.stopListening()

    val startTime = _sessionStartTime.value
    val durationSecs = if (startTime > 0) {
      ((System.currentTimeMillis() - startTime) / 1000).toInt().coerceAtLeast(30)
    } else 480

    val corrections = _sessionCorrections.value
    val grammarScore = (85 - (corrections.size * 5)).coerceIn(60, 95)
    val vocabScore = (78 + (if (_messages.value.size > 6) 5 else 0)).coerceIn(65, 95)
    val speakingScore = 74
    val fluencyScore = 72
    val overallScore = ((grammarScore + vocabScore + speakingScore + fluencyScore) / 4)

    val session = ConversationSession(
      title = "Conversation Practice",
      topic = "Daily Conversation",
      timestamp = System.currentTimeMillis(),
      durationSeconds = durationSecs,
      overallScore = overallScore,
      speakingScore = speakingScore,
      grammarScore = grammarScore,
      vocabularyScore = vocabScore,
      fluencyScore = fluencyScore,
      correctionsCount = corrections.size
    )

    viewModelScope.launch {
      val sessionId = repository.insertSession(session)
      for (corr in corrections) {
        repository.insertCorrection(corr.copy(sessionId = sessionId))
      }
      _lastFinishedSession.value = session.copy(id = sessionId)
      _currentScreen.value = AppScreen.CONVERSATION_SUMMARY
    }
  }

  fun backToHomeFromSummary() {
    _currentScreen.value = AppScreen.MAIN
    _currentTab.value = MainTab.HOME
    _messages.value = emptyList()
  }

  fun practiceAgainFromSummary() {
    _currentScreen.value = AppScreen.MAIN
    _currentTab.value = MainTab.PRACTICE
    startNewConversation()
  }

  fun updateVoiceSpeed(speed: Float) {
    viewModelScope.launch {
      userProfile.value?.let { current ->
        repository.saveUserProfile(current.copy(voiceSpeed = speed))
      }
    }
  }

  fun updateCorrectionPreference(pref: String) {
    viewModelScope.launch {
      userProfile.value?.let { current ->
        repository.saveUserProfile(current.copy(correctionPreference = pref))
      }
    }
  }

  fun updateLevelAndGoal(level: String, goal: String) {
    viewModelScope.launch {
      userProfile.value?.let { current ->
        repository.saveUserProfile(current.copy(englishLevel = level, learningGoal = goal))
      }
    }
  }

  fun toggleDarkMode() {
    viewModelScope.launch {
      userProfile.value?.let { current ->
        repository.saveUserProfile(current.copy(isDarkMode = !current.isDarkMode))
      }
    }
  }

  fun resetUserData() {
    viewModelScope.launch {
      val defaultProfile = UserProfile(
        id = 1,
        name = "Alex",
        englishLevel = "Beginner",
        learningGoal = "Speaking",
        isOnboarded = true,
        dailyPracticeMinutes = 10,
        voiceSpeed = 1.0f,
        correctionPreference = "Gentle",
        isDarkMode = false,
        streakDays = 3
      )
      repository.saveUserProfile(defaultProfile)
      _messages.value = emptyList()
      _currentScreen.value = AppScreen.MAIN
      _currentTab.value = MainTab.HOME
    }
  }

  override fun onCleared() {
    super.onCleared()
    aiTeacherService.cleanup()
  }
}
