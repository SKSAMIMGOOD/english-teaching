package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
  @PrimaryKey val id: Int = 1,
  val name: String = "Learner",
  val englishLevel: String = "Beginner", // Beginner, Intermediate, Advanced, Not sure
  val learningGoal: String = "Daily conversation", // Speaking, College, Job, Interview, Daily conversation
  val isOnboarded: Boolean = false,
  val dailyPracticeMinutes: Int = 10,
  val voiceSpeed: Float = 1.0f,
  val correctionPreference: String = "Gentle", // Gentle, Thorough
  val isDarkMode: Boolean = false,
  val streakDays: Int = 3
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
