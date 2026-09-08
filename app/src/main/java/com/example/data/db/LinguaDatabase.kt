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

  @Query("DELETE FROM conversation_sessions")
  suspend fun clearAllSessions()

  @Query("DELETE FROM session_corrections")
  suspend fun clearAllCorrections()

  @Query("DELETE FROM user_profile")
  suspend fun clearUserProfile()
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
        // Initial clean user profile with 0 streak and no pre-filled fake sessions
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
            streakDays = 0
          )
        )
      }
    }
  }
}
