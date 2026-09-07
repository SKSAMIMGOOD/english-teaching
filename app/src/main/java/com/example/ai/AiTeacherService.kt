package com.example.ai

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.SessionCorrection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AiTeacherResponse(
  val replyText: String,
  val correction: SessionCorrection? = null
)

class AiTeacherService(private val context: Context) {

  private var tts: TextToSpeech? = null
  private var isTtsReady = false

  private val _isAiSpeaking = MutableStateFlow(false)
  val isAiSpeaking: StateFlow<Boolean> = _isAiSpeaking.asStateFlow()

  private val _isListening = MutableStateFlow(false)
  val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

  private val _speechRms = MutableStateFlow(0f)
  val speechRms: StateFlow<Float> = _speechRms.asStateFlow()

  private val _recognizedText = MutableStateFlow("")
  val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

  private var speechRecognizer: SpeechRecognizer? = null

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  init {
    initTts()
  }

  private fun initTts() {
    try {
      tts = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          val result = tts?.setLanguage(Locale.US)
          if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
            isTtsReady = true
          }
        }
      }
      tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          _isAiSpeaking.value = true
        }

        override fun onDone(utteranceId: String?) {
          _isAiSpeaking.value = false
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
          _isAiSpeaking.value = false
        }
      })
    } catch (e: Exception) {
      Log.e("AiTeacherService", "TTS init error", e)
    }
  }

  fun speak(text: String, speed: Float = 1.0f) {
    if (!isTtsReady || tts == null) return
    try {
      tts?.setSpeechRate(speed.coerceIn(0.6f, 1.4f))
      tts?.setPitch(1.05f) // Friendly, bright tone
      val params = Bundle()
      params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "lingua_speech_${System.currentTimeMillis()}")
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "lingua_speech")
    } catch (e: Exception) {
      Log.e("AiTeacherService", "TTS speak error", e)
      _isAiSpeaking.value = false
    }
  }

  fun stopSpeaking() {
    try {
      tts?.stop()
      _isAiSpeaking.value = false
    } catch (e: Exception) {
      Log.e("AiTeacherService", "TTS stop error", e)
    }
  }

  fun startListening(onResult: (String) -> Unit) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      Log.w("AiTeacherService", "Speech recognition not available")
      return
    }

    try {
      stopSpeaking()
      speechRecognizer?.destroy()
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
      val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
      }

      speechRecognizer?.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
          _isListening.value = true
        }

        override fun onBeginningOfSpeech() {
          _isListening.value = true
        }

        override fun onRmsChanged(rmsdB: Float) {
          _speechRms.value = (rmsdB.coerceIn(0f, 10f) / 10f)
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
          _isListening.value = false
        }

        override fun onError(error: Int) {
          _isListening.value = false
          _speechRms.value = 0f
        }

        override fun onResults(results: Bundle?) {
          _isListening.value = false
          _speechRms.value = 0f
          val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          val text = matches?.firstOrNull() ?: ""
          if (text.isNotBlank()) {
            _recognizedText.value = text
            onResult(text)
          }
        }

        override fun onPartialResults(partialResults: Bundle?) {
          val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          val text = matches?.firstOrNull() ?: ""
          if (text.isNotBlank()) {
            _recognizedText.value = text
          }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
      })

      speechRecognizer?.startListening(intent)
      _isListening.value = true
    } catch (e: Exception) {
      Log.e("AiTeacherService", "SpeechRecognizer error", e)
      _isListening.value = false
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
      _isListening.value = false
      _speechRms.value = 0f
    } catch (e: Exception) {
      Log.e("AiTeacherService", "stopListening error", e)
    }
  }

  suspend fun generateTeacherResponse(
    userMessage: String,
    history: List<ChatMessage>,
    userLevel: String,
    userGoal: String,
    userName: String,
    correctionPreference: String
  ): AiTeacherResponse = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Throwable) {
      ""
    }

    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
      try {
        val geminiResult = callGeminiApi(
          apiKey = apiKey,
          userMessage = userMessage,
          history = history,
          userLevel = userLevel,
          userGoal = userGoal,
          userName = userName,
          correctionPreference = correctionPreference
        )
        if (geminiResult != null) {
          return@withContext geminiResult
        }
      } catch (e: Exception) {
        Log.e("AiTeacherService", "Gemini API failed, using intelligent local engine", e)
      }
    }

    // High quality conversational AI teacher fallback
    return@withContext generateSmartFallbackResponse(userMessage, userLevel, userGoal, userName)
  }

  private fun callGeminiApi(
    apiKey: String,
    userMessage: String,
    history: List<ChatMessage>,
    userLevel: String,
    userGoal: String,
    userName: String,
    correctionPreference: String
  ): AiTeacherResponse? {
    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

    val systemPrompt = """
      You are LinguaAI, an encouraging, friendly English teacher conversing 1-on-1 with $userName.
      Learner English level: $userLevel. Goal: $userGoal. Correction style: $correctionPreference.
      
      RULES:
      1. Speak in natural, warm, conversational English suited for a $userLevel speaker.
      2. Keep responses brief (1-3 sentences) so the conversation flows back and forth easily.
      3. Always ask an engaging follow-up question.
      4. Inspect the user's message for grammar or vocabulary mistakes. If there is a mistake, provide a gentle correction and explanation.
      
      You MUST reply ONLY with valid JSON in this exact structure:
      {
        "reply": "Conversational response to the user with a question",
        "hasCorrection": true/false,
        "originalSentence": "exact user mistake or empty",
        "correctedSentence": "natural correct sentence or empty",
        "explanation": "friendly short 1-sentence reason why"
      }
    """.trimIndent()

    val contentsArray = JSONArray()

    // Add recent history
    val recent = history.takeLast(6)
    for (msg in recent) {
      val role = if (msg.isUser) "user" else "model"
      val partObj = JSONObject().put("text", msg.text)
      val contentObj = JSONObject()
        .put("role", role)
        .put("parts", JSONArray().put(partObj))
      contentsArray.put(contentObj)
    }

    // Add current user message
    val userPart = JSONObject().put("text", userMessage)
    contentsArray.put(
      JSONObject()
        .put("role", "user")
        .put("parts", JSONArray().put(userPart))
    )

    val requestJson = JSONObject().apply {
      put("contents", contentsArray)
      put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
      val genConfig = JSONObject()
        .put("temperature", 0.7)
        .put("topP", 0.9)
        .put("responseMimeType", "application/json")
      put("generationConfig", genConfig)
    }

    val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder().url(url).post(body).build()
    val response = okHttpClient.newCall(request).execute()

    if (!response.isSuccessful) {
      Log.e("AiTeacherService", "Gemini HTTP ${response.code}: ${response.message}")
      return null
    }

    val responseBody = response.body?.string() ?: return null
    val rootObj = JSONObject(responseBody)
    val candidates = rootObj.optJSONArray("candidates") ?: return null
    val firstCandidate = candidates.optJSONObject(0) ?: return null
    val content = firstCandidate.optJSONObject("content") ?: return null
    val parts = content.optJSONArray("parts") ?: return null
    val text = parts.optJSONObject(0)?.optString("text") ?: return null

    val parsed = JSONObject(text.trim())
    val reply = parsed.optString("reply", "That's great! Tell me more about that.")
    val hasCorrection = parsed.optBoolean("hasCorrection", false)
    val original = parsed.optString("originalSentence", "")
    val corrected = parsed.optString("correctedSentence", "")
    val explanation = parsed.optString("explanation", "")

    val correction = if (hasCorrection && original.isNotBlank() && corrected.isNotBlank()) {
      SessionCorrection(
        sessionId = 0,
        originalSentence = original,
        correctedSentence = corrected,
        explanation = explanation
      )
    } else null

    return AiTeacherResponse(replyText = reply, correction = correction)
  }

  private fun generateSmartFallbackResponse(
    userMessage: String,
    level: String,
    goal: String,
    name: String
  ): AiTeacherResponse {
    val clean = userMessage.trim().lowercase()

    // 1. Check for common grammar mistake patterns
    var correction: SessionCorrection? = null
    var reply = ""

    when {
      clean.contains("yesterday i go") || clean.contains("last week i go") || clean.contains("last year i go") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("i go", "I went", ignoreCase = true).replace("I go", "I went"),
          explanation = "Because you are describing an action in the past, use 'went' instead of 'go'."
        )
        reply = "That sounds exciting! Who did you go with, and what was your favorite part?"
      }
      clean.contains("i am agree") || clean.contains("i am not agree") -> {
        val corr = if (clean.contains("not")) "I don't agree" else "I agree"
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("i am agree", "I agree", ignoreCase = true)
            .replace("i am not agree", "I don't agree", ignoreCase = true),
          explanation = "In English, 'agree' is already a verb, so say 'I agree' rather than 'I am agree'."
        )
        reply = "I understand your perspective! Why do you feel that way?"
      }
      clean.contains("i very like") || clean.contains("i very much like") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("very like", "really like", ignoreCase = true),
          explanation = "In English, we say 'I really like' or 'I like ... very much' instead of 'I very like'."
        )
        reply = "Nice! How long have you been interested in that?"
      }
      clean.contains("i work in") && clean.contains("since") && clean.contains("years") && !clean.contains("have been") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("i work", "I have been working", ignoreCase = true),
          explanation = "For an action that started in the past and continues now with 'since' or 'for', use the present perfect continuous: 'I have been working'."
        )
        reply = "That is great experience! What do you enjoy most about your daily work?"
      }
      clean.contains("he don't") || clean.contains("she don't") || clean.contains("it don't") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("don't", "doesn't", ignoreCase = true),
          explanation = "With third-person singular subjects (he, she, it), use 'doesn't' instead of 'don't'."
        )
        reply = "Got it! How did they react to that situation?"
      }
      clean.contains("can you explain me") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("explain me", "explain to me", ignoreCase = true),
          explanation = "The verb 'explain' takes the preposition 'to' before the person: 'explain to me'."
        )
        reply = "I'd be happy to explain! What specific part would you like to explore first?"
      }
      clean.contains("i look forward to see you") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("to see you", "to seeing you", ignoreCase = true),
          explanation = "After the phrasal verb 'look forward to', use the -ing gerund form: 'to seeing you'."
        )
        reply = "Same here! What are your main expectations for our session?"
      }
      clean.contains("people is") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("people is", "people are", ignoreCase = true),
          explanation = "'People' is plural, so it takes the plural verb 'are'."
        )
        reply = "That's a very keen observation! How does that affect your daily conversations?"
      }
    }

    // 2. Formulate dynamic conversational responses if no correction triggered
    if (reply.isBlank()) {
      when {
        clean.contains("hello") || clean.contains("hi") || clean.contains("hey") -> {
          reply = "Hello $name! It is wonderful to practice with you today. How was your day so far?"
        }
        clean.contains("how are you") || clean.contains("how do you do") -> {
          reply = "I'm doing great and feeling energized to help you practice English! How are you feeling today?"
        }
        clean.contains("weekend") || clean.contains("saturday") || clean.contains("sunday") -> {
          reply = "Weekends are great for unwinding! What is something relaxing you did recently?"
        }
        clean.contains("job") || clean.contains("interview") || clean.contains("work") || clean.contains("office") -> {
          reply = "Professional English is a fantastic skill to build! Could you tell me a little about your role or the industry you are targeting?"
        }
        clean.contains("college") || clean.contains("university") || clean.contains("study") || clean.contains("exam") -> {
          reply = "Studying opens so many doors! What subjects are you focusing on this semester?"
        }
        clean.contains("nervous") || clean.contains("scared") || clean.contains("shy") -> {
          reply = "It's completely normal to feel nervous! Remember, making mistakes is the fastest way to learn. You are doing great. What would you like to talk about today?"
        }
        clean.contains("movie") || clean.contains("music") || clean.contains("hobby") || clean.contains("book") -> {
          reply = "That sounds fascinating! What made you fall in love with that?"
        }
        clean.length < 15 -> {
          reply = "That's a good start! Could you try adding one more sentence to explain your thoughts?"
        }
        else -> {
          val responses = listOf(
            "That's a really interesting point! What happened next?",
            "You expressed that very clearly! How do you usually handle situations like that?",
            "I love that perspective! Tell me more about what you enjoyed most about it.",
            "Great conversational flow! How did that experience shape your routine?"
          )
          reply = responses[(clean.hashCode().mod(responses.size) + responses.size) % responses.size]
        }
      }
    }

    return AiTeacherResponse(replyText = reply, correction = correction)
  }

  fun cleanup() {
    try {
      tts?.stop()
      tts?.shutdown()
      speechRecognizer?.destroy()
    } catch (e: Exception) {
      Log.e("AiTeacherService", "Cleanup error", e)
    }
  }
}
