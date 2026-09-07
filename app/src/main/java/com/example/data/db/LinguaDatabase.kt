package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ConversationSession
import com.example.data.model.SessionCorrection
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface LinguaDao {
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getUserProfile(): Flow<UserProfile?>

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  suspend fun getUserProfileOnce(): UserProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUserProfile(profile: UserProfile)

  @Query("SELECT * FROM conversation_sessions ORDER BY timestamp DESC")
  fun getAllSessions(): Flow<List<ConversationSession>>

  @Query("SELECT * FROM conversation_sessions ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentSessions(limit: Int = 3): Flow<List<ConversationSession>>

  @Query("SELECT * FROM conversation_sessions WHERE id = :id LIMIT 1")
  suspend fun getSessionById(id: Long): ConversationSession?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: ConversationSession): Long

  @Query("SELECT * FROM session_corrections WHERE sessionId = :sessionId")
  fun getCorrectionsForSession(sessionId: Long): Flow<List<SessionCorrection>>

  @Query("SELECT * FROM session_corrections ORDER BY id DESC LIMIT :limit")
  fun getRecentCorrections(limit: Int = 5): Flow<List<SessionCorrection>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCorrection(correction: SessionCorrection): Long

  @Update
  suspend fun updateCorrection(correction: SessionCorrection)
}

@Database(
  entities = [UserProfile::class, ConversationSession::class, SessionCorrection::class],
  version = 1,
  exportSchema = false
)
abstract class LinguaDatabase : RoomDatabase() {
  abstract fun linguaDao(): LinguaDao

  companion object {
    @Volatile
    private var INSTANCE: LinguaDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): LinguaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          LinguaDatabase::class.java,
          "lingua_ai_database"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.linguaDao())
          }
        }
      }

      suspend fun populateInitialData(dao: LinguaDao) {
        // Initial default user profile (not yet finished onboarding)
        dao.saveUserProfile(
          UserProfile(
            id = 1,
            name = "Alex",
            englishLevel = "Beginner",
            learningGoal = "Speaking",
            isOnboarded = false,
            dailyPracticeMinutes = 10,
            voiceSpeed = 1.0f,
            correctionPreference = "Gentle",
            isDarkMode = false,
            streakDays = 3
          )
        )

        // Seed 2 recent practice sessions for a rich first impression
        val now = System.currentTimeMillis()
        val s1 = dao.insertSession(
          ConversationSession(
            id = 0,
            title = "Daily Conversation",
            topic = "Daily routine & Weekend plans",
            timestamp = now - (8 * 60 * 1000L), // 8 min ago
            durationSeconds = 480, // 8 min
            overallScore = 72,
            speakingScore = 72,
            grammarScore = 68,
            vocabularyScore = 76,
            fluencyScore = 70,
            correctionsCount = 2
          )
        )
        dao.insertCorrection(
          SessionCorrection(
            sessionId = s1,
            originalSentence = "Yesterday I go to market.",
            correctedSentence = "Yesterday I went to the market.",
            explanation = "Because you're talking about the past, use 'went' instead of 'go', and include the article 'the'."
          )
        )
        dao.insertCorrection(
          SessionCorrection(
            sessionId = s1,
            originalSentence = "I very like coffee.",
            correctedSentence = "I really like coffee.",
            explanation = "In English, 'really like' or 'like coffee very much' is more natural than 'very like'."
          )
        )

        val s2 = dao.insertSession(
          ConversationSession(
            id = 0,
            title = "Introducing Yourself",
            topic = "Work, studies & hobbies",
            timestamp = now - (24 * 60 * 60 * 1000L), // Yesterday
            durationSeconds = 360, // 6 min
            overallScore = 70,
            speakingScore = 70,
            grammarScore = 66,
            vocabularyScore = 74,
            fluencyScore = 68,
            correctionsCount = 1
          )
        )
        dao.insertCorrection(
          SessionCorrection(
            sessionId = s2,
            originalSentence = "I am work in tech company.",
            correctedSentence = "I work in a tech company.",
            explanation = "Use simple present 'I work' for your regular occupation, without 'am'."
          )
        )
      }
    }
  }
}
