package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiTeacherService
import com.example.data.db.LinguaDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.ConversationSession
import com.example.data.model.PracticeMode
import com.example.data.model.RoleplayScenario
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
import java.util.Calendar

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

  // Practice Modes state
  private val _currentPracticeMode = MutableStateFlow(PracticeMode.FREE_CONVERSATION)
  val currentPracticeMode: StateFlow<PracticeMode> = _currentPracticeMode.asStateFlow()

  private val _currentRoleplayScenario = MutableStateFlow<RoleplayScenario?>(null)
  val currentRoleplayScenario: StateFlow<RoleplayScenario?> = _currentRoleplayScenario.asStateFlow()

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

  fun selectPracticeMode(mode: PracticeMode, scenario: RoleplayScenario? = null) {
    _currentPracticeMode.value = mode
    _currentRoleplayScenario.value = scenario
  }

  fun startPractice(mode: PracticeMode = PracticeMode.FREE_CONVERSATION, scenario: RoleplayScenario? = null) {
    _currentPracticeMode.value = mode
    _currentRoleplayScenario.value = scenario
    _currentTab.value = MainTab.PRACTICE
    _currentScreen.value = AppScreen.MAIN
    startNewConversation()
  }

  fun startNewConversation() {
    _sessionStartTime.value = System.currentTimeMillis()
    _sessionCorrections.value = emptyList()
    _activeCorrection.value = null
    aiTeacherService.stopSpeaking()

    val profile = userProfile.value
    val name = profile?.name ?: "there"
    val level = profile?.englishLevel ?: "Beginner"
    val mode = _currentPracticeMode.value
    val scenario = _currentRoleplayScenario.value

    val initialAiMsgText = when (mode) {
      PracticeMode.REAL_LIFE_SITUATIONS -> {
        scenario?.initialAiGreeting ?: "Welcome! Let's practice a real-world scenario together. Where would you like to begin?"
      }
      PracticeMode.AI_QUESTIONS -> {
        when (level.lowercase()) {
          "advanced" -> "Welcome to Question Practice, $name! Let's begin: What do you consider the most significant ethical challenge facing modern technology, and why?"
          "intermediate" -> "Welcome to Question Practice, $name! Here is your first question: If you could travel anywhere in the world tomorrow, where would you choose and why?"
          else -> "Hi $name! Welcome to Question Practice. Let's start with a simple question: What is your favorite food, and why do you like it?"
        }
      }
      PracticeMode.SPEAK_AND_CORRECT -> {
        when (level.lowercase()) {
          "advanced" -> "Welcome to Speak & Correct, $name. Speak in depth about a professional project or challenge you navigated. I'll provide detailed feedback on nuance, grammar, and word choice."
          "intermediate" -> "Welcome to Speak & Correct, $name! Describe a memorable trip or an interesting experience you had recently. Take your time, and I'll help refine your grammar!"
          else -> "Hi $name! Welcome to Speak & Correct. Tell me about your favorite hobby or what you did today. Speak freely, and I will gently help you with corrections!"
        }
      }
      PracticeMode.FREE_CONVERSATION -> {
        when (level.lowercase()) {
          "advanced" -> "Hello $name, wonderful to connect today! What interesting ideas, projects, or books have been keeping you engaged lately?"
          "intermediate" -> "Hi $name! Great to see you. How has your week been going so far? Any highlights?"
          else -> "Hi $name! How was your day today?"
        }
      }
    }

    val initialAiMsg = ChatMessage(
      isUser = false,
      text = initialAiMsgText
    )
    _messages.value = listOf(initialAiMsg)

    // Play greeting audio with level-adjusted speed
    viewModelScope.launch {
      delay(400)
      val baseSpeed = profile?.voiceSpeed ?: 1.0f
      val levelFactor = when (level.lowercase()) {
        "beginner" -> 0.88f
        "advanced" -> 1.08f
        else -> 1.0f
      }
      val effectiveSpeed = (baseSpeed * levelFactor).coerceIn(0.7f, 1.3f)
      aiTeacherService.speak(initialAiMsg.text, effectiveSpeed)
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
        correctionPreference = profile?.correctionPreference ?: "Gentle",
        practiceMode = _currentPracticeMode.value,
        roleplayScenario = _currentRoleplayScenario.value
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

      // Level-adjusted speed for speech response
      val baseSpeed = profile?.voiceSpeed ?: 1.0f
      val levelFactor = when (profile?.englishLevel?.lowercase()) {
        "beginner" -> 0.88f
        "advanced" -> 1.08f
        else -> 1.0f
      }
      val effectiveSpeed = (baseSpeed * levelFactor).coerceIn(0.7f, 1.3f)
      aiTeacherService.speak(aiMsg.text, effectiveSpeed)
    }
  }

  fun dismissCorrection() {
    _activeCorrection.value = null
  }

  fun retryCorrection(correction: SessionCorrection) {
    _activeCorrection.value = null
    viewModelScope.launch {
      val prompt = "Great! Say this sentence aloud: \"${correction.correctedSentence}\""
      aiTeacherService.speak(prompt, userProfile.value?.voiceSpeed ?: 1.0f)
    }
  }

  fun replayAiAudio(text: String) {
    aiTeacherService.stopSpeaking()
    val baseSpeed = userProfile.value?.voiceSpeed ?: 1.0f
    val levelFactor = when (userProfile.value?.englishLevel?.lowercase()) {
      "beginner" -> 0.88f
      "advanced" -> 1.08f
      else -> 1.0f
    }
    aiTeacherService.speak(text, (baseSpeed * levelFactor).coerceIn(0.7f, 1.3f))
  }

  fun endConversation() {
    aiTeacherService.stopSpeaking()
    aiTeacherService.stopListening()

    val startTime = _sessionStartTime.value
    val durationSecs = if (startTime > 0) {
      ((System.currentTimeMillis() - startTime) / 1000).toInt().coerceAtLeast(30)
    } else 360

    val corrections = _sessionCorrections.value
    val userMessages = _messages.value.filter { it.isUser }
    val totalUserWords = userMessages.sumOf { it.text.split("\\s+".toRegex()).size }
    val mistakes = corrections.size

    // Calculate real scores based on conversation data
    val grammarScore = (96 - (mistakes * 6)).coerceIn(55, 98)
    val vocabScore = if (totalUserWords > 35) {
      (80 + (totalUserWords / 8).coerceAtMost(16)).coerceIn(65, 96)
    } else {
      (70 + (totalUserWords / 6).coerceAtMost(18)).coerceIn(58, 90)
    }
    val speakingScore = (72 + (userMessages.size * 4).coerceAtMost(24)).coerceIn(60, 98)
    val cleanTurns = (userMessages.size - mistakes).coerceAtLeast(0)
    val fluencyScore = (68 + (cleanTurns * 5).coerceAtMost(26)).coerceIn(58, 96)
    val overallScore = ((grammarScore + vocabScore + speakingScore + fluencyScore) / 4)

    val mode = _currentPracticeMode.value
    val scenario = _currentRoleplayScenario.value
    val sessionTitle = when (mode) {
      PracticeMode.FREE_CONVERSATION -> "Free Conversation"
      PracticeMode.AI_QUESTIONS -> "Question Practice"
      PracticeMode.REAL_LIFE_SITUATIONS -> scenario?.title ?: "Real-Life Situation"
      PracticeMode.SPEAK_AND_CORRECT -> "Speak & Correct"
    }
    val sessionTopic = scenario?.subtitle ?: when (mode) {
      PracticeMode.FREE_CONVERSATION -> "Daily Conversation"
      PracticeMode.AI_QUESTIONS -> "Targeted Q&A"
      PracticeMode.REAL_LIFE_SITUATIONS -> scenario?.title ?: "Practical English"
      PracticeMode.SPEAK_AND_CORRECT -> "Grammar & Fluency Focus"
    }

    val session = ConversationSession(
      title = sessionTitle,
      topic = sessionTopic,
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

      // Calculate real streak from updated sessions
      val allPastSessions = allSessions.value
      val updatedList = allPastSessions + session
      val newStreak = calculateRealStreak(updatedList)
      userProfile.value?.let { currentProfile ->
        repository.saveUserProfile(currentProfile.copy(streakDays = newStreak))
      }

      _currentScreen.value = AppScreen.CONVERSATION_SUMMARY
    }
  }

  private fun calculateRealStreak(sessions: List<ConversationSession>): Int {
    if (sessions.isEmpty()) return 0
    val sessionDays = sessions.map {
      val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
      c.get(Calendar.YEAR) to c.get(Calendar.DAY_OF_YEAR)
    }.toSet()

    val cal = Calendar.getInstance()
    val todayYear = cal.get(Calendar.YEAR)
    val todayDay = cal.get(Calendar.DAY_OF_YEAR)

    var streak = 0
    // Check if practiced today
    if (sessionDays.contains(todayYear to todayDay)) {
      streak++
      cal.add(Calendar.DAY_OF_YEAR, -1)
    } else {
      // Check if practiced yesterday
      cal.add(Calendar.DAY_OF_YEAR, -1)
      val yYear = cal.get(Calendar.YEAR)
      val yDay = cal.get(Calendar.DAY_OF_YEAR)
      if (!sessionDays.contains(yYear to yDay)) {
        return 0
      }
    }

    while (true) {
      val y = cal.get(Calendar.YEAR)
      val d = cal.get(Calendar.DAY_OF_YEAR)
      if (sessionDays.contains(y to d)) {
        streak++
        cal.add(Calendar.DAY_OF_YEAR, -1)
      } else {
        break
      }
    }
    return streak
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
      database.linguaDao().clearAllSessions()
      database.linguaDao().clearAllCorrections()
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
        streakDays = 0
      )
      repository.saveUserProfile(defaultProfile)
      _messages.value = emptyList()
      _currentPracticeMode.value = PracticeMode.FREE_CONVERSATION
      _currentRoleplayScenario.value = null
      _currentScreen.value = AppScreen.MAIN
      _currentTab.value = MainTab.HOME
    }
  }

  override fun onCleared() {
    super.onCleared()
    aiTeacherService.cleanup()
  }
}
