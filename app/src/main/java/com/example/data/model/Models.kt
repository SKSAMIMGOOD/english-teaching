package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
  @PrimaryKey val id: Int = 1,
  val name: String = "Learner",
  val englishLevel: String = "Beginner", // Beginner, Intermediate, Advanced
  val learningGoal: String = "Daily conversation", // Daily conversation, Career & Interview, College & Study, Travel & Social
  val isOnboarded: Boolean = false,
  val dailyPracticeMinutes: Int = 10,
  val voiceSpeed: Float = 1.0f,
  val correctionPreference: String = "Gentle", // Gentle, Thorough
  val isDarkMode: Boolean = false,
  val streakDays: Int = 0
)

enum class PracticeMode(
  val id: String,
  val title: String,
  val description: String,
  val durationBadge: String,
  val levelBadge: String
) {
  FREE_CONVERSATION(
    id = "free_conversation",
    title = "Free Conversation",
    description = "Natural back-and-forth chat on everyday topics with a helpful AI friend.",
    durationBadge = "5–10 min",
    levelBadge = "All Levels"
  ),
  AI_QUESTIONS(
    id = "ai_questions",
    title = "AI Question Practice",
    description = "AI asks targeted questions, evaluates your answers, and gives instant feedback.",
    durationBadge = "~5 min",
    levelBadge = "Adaptive"
  ),
  REAL_LIFE_SITUATIONS(
    id = "real_life_situations",
    title = "Real-Life Situations",
    description = "Roleplay ordering food, airport check-in, job interviews, college & shopping.",
    durationBadge = "5–10 min",
    levelBadge = "Practical"
  ),
  SPEAK_AND_CORRECT(
    id = "speak_and_correct",
    title = "Speak & Correct",
    description = "Speak freely on a given topic to get in-depth grammar and fluency advice.",
    durationBadge = "3–5 min",
    levelBadge = "Focus"
  )
}

data class RoleplayScenario(
  val id: String,
  val title: String,
  val subtitle: String,
  val iconEmoji: String,
  val initialAiGreeting: String,
  val roleplayContext: String
)

val AvailableRoleplayScenarios = listOf(
  RoleplayScenario(
    id = "ordering_food",
    title = "Ordering Food & Drinks",
    subtitle = "Order at a cozy café or restaurant with a barista",
    iconEmoji = "☕",
    initialAiGreeting = "Welcome to Central Café! What would you like to order today?",
    roleplayContext = "You are a friendly café barista taking an order. Help the customer order coffee and food, suggesting items and gently correcting phrases."
  ),
  RoleplayScenario(
    id = "airport",
    title = "Airport & Travel",
    subtitle = "Check-in luggage, passport control & boarding gate",
    iconEmoji = "✈️",
    initialAiGreeting = "Good day! Welcome to Star Airlines check-in. May I please see your passport and ticket?",
    roleplayContext = "You are an airline customer service agent at the check-in desk. Guide the traveler through boarding passes, luggage check, and seat selection."
  ),
  RoleplayScenario(
    id = "job_interview",
    title = "Job Interview",
    subtitle = "Discuss your experience, strengths & career goals",
    iconEmoji = "💼",
    initialAiGreeting = "Hello! Thanks for coming in today. To get started, could you introduce yourself and tell me a bit about your background?",
    roleplayContext = "You are a professional, warm hiring manager interviewing a candidate for a role. Ask insightful questions about projects, strengths, and goals."
  ),
  RoleplayScenario(
    id = "college",
    title = "College Campus",
    subtitle = "Meeting new students, professors & discussing classes",
    iconEmoji = "🎓",
    initialAiGreeting = "Hey there! Are you also in the introductory English seminar this semester?",
    roleplayContext = "You are a friendly college student classmate chatting on campus before class."
  ),
  RoleplayScenario(
    id = "shopping",
    title = "Shopping & Store",
    subtitle = "Asking for sizes, prices, discounts & recommendations",
    iconEmoji = "🛍️",
    initialAiGreeting = "Hi there! Let me know if you need any help finding sizes or trying anything on today.",
    roleplayContext = "You are an attentive retail store assistant helping a shopper choose clothing and find their size."
  )
)

@Entity(tableName = "conversation_sessions")
data class ConversationSession(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val topic: String,
  val timestamp: Long = System.currentTimeMillis(),
  val durationSeconds: Int = 480, // e.g. 8 mins
  val overallScore: Int = 74,
  val speakingScore: Int = 72,
  val grammarScore: Int = 68,
  val vocabularyScore: Int = 76,
  val fluencyScore: Int = 70,
  val correctionsCount: Int = 2
)

@Entity(tableName = "session_corrections")
data class SessionCorrection(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: Long,
  val originalSentence: String,
  val correctedSentence: String,
  val explanation: String,
  val isPracticed: Boolean = false
)

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val isUser: Boolean,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val correction: SessionCorrection? = null
)
