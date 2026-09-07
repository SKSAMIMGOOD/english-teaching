package com.example.data.repository

import com.example.data.db.LinguaDao
import com.example.data.model.ConversationSession
import com.example.data.model.SessionCorrection
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class LinguaRepository(private val dao: LinguaDao) {

  val userProfile: Flow<UserProfile?> = dao.getUserProfile()
  val recentSessions: Flow<List<ConversationSession>> = dao.getRecentSessions(5)
  val allSessions: Flow<List<ConversationSession>> = dao.getAllSessions()
  val recentCorrections: Flow<List<SessionCorrection>> = dao.getRecentCorrections(10)

  suspend fun getUserProfileOnce(): UserProfile? = dao.getUserProfileOnce()

  suspend fun saveUserProfile(profile: UserProfile) {
    dao.saveUserProfile(profile)
  }

  suspend fun insertSession(session: ConversationSession): Long {
    return dao.insertSession(session)
  }

  suspend fun getSessionById(id: Long): ConversationSession? {
    return dao.getSessionById(id)
  }

  fun getCorrectionsForSession(sessionId: Long): Flow<List<SessionCorrection>> {
    return dao.getCorrectionsForSession(sessionId)
  }

  suspend fun insertCorrection(correction: SessionCorrection): Long {
    return dao.insertCorrection(correction)
  }

  suspend fun updateCorrection(correction: SessionCorrection) {
    dao.updateCorrection(correction)
  }
}
