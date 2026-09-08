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

  fun startListening(onError: ((String) -> Unit)? = null, onResult: (String) -> Unit) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      Log.w("AiTeacherService", "Speech recognition not available")
      onError?.invoke("Speech recognition is not available on this device.")
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
          val errorMsg = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Speak clearly into the microphone."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timed out. Tap mic when you're ready."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your mic."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network issue for voice recognition."
            else -> "Voice recognition paused. Tap mic to try again."
          }
          onError?.invoke(errorMsg)
        }

        override fun onResults(results: Bundle?) {
          _isListening.value = false
          _speechRms.value = 0f
          val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          val text = matches?.firstOrNull() ?: ""
          if (text.isNotBlank()) {
            _recognizedText.value = text
            onResult(text)
          } else {
            onError?.invoke("No words detected. Tap mic to speak again.")
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
      onError?.invoke("Couldn't start voice recognition.")
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
    correctionPreference: String,
    practiceMode: com.example.data.model.PracticeMode = com.example.data.model.PracticeMode.FREE_CONVERSATION,
    roleplayScenario: com.example.data.model.RoleplayScenario? = null
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
          correctionPreference = correctionPreference,
          practiceMode = practiceMode,
          roleplayScenario = roleplayScenario
        )
        if (geminiResult != null) {
          return@withContext geminiResult
        }
      } catch (e: Exception) {
        Log.e("AiTeacherService", "Gemini API failed, using intelligent local engine", e)
      }
    }

    // High quality conversational AI teacher fallback
    return@withContext generateSmartFallbackResponse(
      userMessage = userMessage,
      level = userLevel,
      goal = userGoal,
      name = userName,
      practiceMode = practiceMode,
      roleplayScenario = roleplayScenario,
      history = history
    )
  }

  private fun callGeminiApi(
    apiKey: String,
    userMessage: String,
    history: List<ChatMessage>,
    userLevel: String,
    userGoal: String,
    userName: String,
    correctionPreference: String,
    practiceMode: com.example.data.model.PracticeMode,
    roleplayScenario: com.example.data.model.RoleplayScenario?
  ): AiTeacherResponse? {
    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

    val modeInstructions = when (practiceMode) {
      com.example.data.model.PracticeMode.FREE_CONVERSATION -> """
        MODE: Free Conversation.
        Be a warm, encouraging English teacher chatting with a friend.
        Acknowledge the user's specific statements, stories, or feelings directly.
        Keep the conversation flowing naturally with an engaging follow-up question.
      """.trimIndent()
      com.example.data.model.PracticeMode.AI_QUESTIONS -> """
        MODE: AI Question Practice.
        1. Evaluate the user's answer (Quality, Grammar, Vocabulary).
        2. In your reply, praise their effort and offer a brief natural phrasing tip (e.g. "Try saying it more naturally: ...").
        3. Then ask the next level-appropriate dynamic question to keep practicing.
      """.trimIndent()
      com.example.data.model.PracticeMode.REAL_LIFE_SITUATIONS -> """
        MODE: Real-Life Roleplay Scenario.
        Scenario: ${roleplayScenario?.title ?: "Everyday Situation"}
        Context: ${roleplayScenario?.roleplayContext ?: "Act in character for this scenario."}
        Stay strictly in character! Respond authentically to what the customer/candidate/student says.
        If they use unnatural phrasing, gently model the native way to say it in your dialogue.
      """.trimIndent()
      com.example.data.model.PracticeMode.SPEAK_AND_CORRECT -> """
        MODE: Speak & Correct.
        Focus on speech evaluation: vocabulary variety, sentence connectors, and fluency tips.
        Provide structured constructive feedback and encourage them to expand on their thoughts.
      """.trimIndent()
    }

    val levelGuidelines = when (userLevel.lowercase()) {
      "advanced" -> "Advanced English: Use rich vocabulary, nuanced follow-up questions, and only correct subtle grammatical or stylistic errors."
      "intermediate" -> "Intermediate English: Use natural conversational English, common idioms, 2-3 sentences, and correct noticeable grammar/preposition errors."
      else -> "Beginner English: Keep sentences short (1-2 sentences), vocabulary simple and clear, slow and easy follow-up questions, and provide very gentle, encouraging corrections."
    }

    val systemPrompt = """
      You are LinguaAI, an expert, warm, and highly adaptive English teacher conversing 1-on-1 with $userName.
      Learner English level: $userLevel. Goal: $userGoal. Correction style: $correctionPreference.
      
      $modeInstructions
      
      $levelGuidelines
      
      CRITICAL ANTI-REPETITION RULES:
      - NEVER use repetitive filler lines such as "That's a really interesting point! What happened next?" or generic scripted praises.
      - Directly mention the specific words, sports, accomplishments, people, or events the user mentioned (e.g., if they won a cricket match, congratulate them on winning the cricket match and ask about the score or their role).
      - Make every response uniquely tailored to the user's exact words.
      - Inspect the user's message for grammar or vocabulary mistakes. If there is a mistake, provide a helpful correction with a 1-sentence friendly explanation.
      
      You MUST reply ONLY with valid JSON in this exact structure:
      {
        "reply": "Contextual conversational response acknowledging user words and asking a follow-up question",
        "hasCorrection": true/false,
        "originalSentence": "exact user mistake or empty",
        "correctedSentence": "natural correct sentence or empty",
        "explanation": "friendly short 1-sentence reason why"
      }
    """.trimIndent()

    // Build properly formatted alternating conversation turns
    val turns = mutableListOf<Pair<String, String>>()
    for (msg in history.takeLast(8)) {
      val role = if (msg.isUser) "user" else "model"
      if (msg.text.isNotBlank()) {
        turns.add(role to msg.text)
      }
    }

    // Ensure the current user message is included as the final turn
    if (turns.isEmpty() || turns.last().first != "user" || turns.last().second != userMessage) {
      turns.add("user" to userMessage)
    }

    // Collapse adjacent same-role turns to strictly satisfy Gemini alternating schema
    val alternatingTurns = mutableListOf<Pair<String, String>>()
    for (turn in turns) {
      if (alternatingTurns.isNotEmpty() && alternatingTurns.last().first == turn.first) {
        val prev = alternatingTurns.removeAt(alternatingTurns.size - 1)
        alternatingTurns.add(turn.first to "${prev.second} ${turn.second}")
      } else {
        alternatingTurns.add(turn)
      }
    }

    // Gemini multi-turn conversation must begin with 'user'
    while (alternatingTurns.isNotEmpty() && alternatingTurns.first().first != "user") {
      alternatingTurns.removeAt(0)
    }
    if (alternatingTurns.isEmpty()) {
      alternatingTurns.add("user" to userMessage)
    }

    val contentsArray = JSONArray()
    for ((role, text) in alternatingTurns) {
      val partObj = JSONObject().put("text", text)
      val contentObj = JSONObject()
        .put("role", role)
        .put("parts", JSONArray().put(partObj))
      contentsArray.put(contentObj)
    }

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
    var reply = parsed.optString("reply", "").trim()
    if (reply.isBlank() || reply.contains("That's a really interesting point! What happened next?")) {
      reply = generateSmartFallbackResponse(
        userMessage = userMessage,
        level = userLevel,
        goal = userGoal,
        name = userName,
        practiceMode = practiceMode,
        roleplayScenario = roleplayScenario,
        history = history
      ).replyText
    }

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
    name: String,
    practiceMode: com.example.data.model.PracticeMode = com.example.data.model.PracticeMode.FREE_CONVERSATION,
    roleplayScenario: com.example.data.model.RoleplayScenario? = null,
    history: List<ChatMessage> = emptyList()
  ): AiTeacherResponse {
    val clean = userMessage.trim().lowercase()

    // 1. Grammar mistake detection and correction
    var correction: SessionCorrection? = null

    when {
      clean.contains("yesterday i go") || clean.contains("last week i go") || clean.contains("last year i go") || clean.contains("last night i go") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("i go", "I went", ignoreCase = true).replace("I go", "I went"),
          explanation = "Because you are describing an action completed in the past, use the past tense 'went' instead of 'go'."
        )
      }
      clean.contains("i am agree") || clean.contains("i am not agree") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("i am agree", "I agree", ignoreCase = true)
            .replace("i am not agree", "I don't agree", ignoreCase = true),
          explanation = "In English, 'agree' is already a verb, so say 'I agree' rather than 'I am agree'."
        )
      }
      clean.contains("i very like") || clean.contains("i very much like") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("very like", "really like", ignoreCase = true),
          explanation = "In English, we say 'I really like' or 'I like ... very much' instead of 'I very like'."
        )
      }
      clean.contains("he don't") || clean.contains("she don't") || clean.contains("it don't") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("don't", "doesn't", ignoreCase = true),
          explanation = "With third-person singular subjects (he, she, it), use 'doesn't' instead of 'don't'."
        )
      }
      clean.contains("people is") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("people is", "people are", ignoreCase = true),
          explanation = "'People' is a plural noun, so it takes the plural verb 'are'."
        )
      }
      clean.contains("can you explain me") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("explain me", "explain to me", ignoreCase = true),
          explanation = "The verb 'explain' requires the preposition 'to' before the person: 'explain to me'."
        )
      }
      clean.contains("look forward to see") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("look forward to see", "look forward to seeing", ignoreCase = true),
          explanation = "After the phrasal expression 'look forward to', always use the gerund (-ing) form: 'seeing'."
        )
      }
      clean.contains("i didn't saw") || clean.contains("i didn't went") -> {
        val corr = userMessage.replace("didn't saw", "didn't see", ignoreCase = true)
          .replace("didn't went", "didn't go", ignoreCase = true)
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = corr,
          explanation = "After the auxiliary verb 'didn't', always use the base form of the verb: 'didn't see' or 'didn't go'."
        )
      }
      clean.contains("more better") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("more better", "much better", ignoreCase = true),
          explanation = "'Better' is already comparative, so say 'much better' instead of 'more better'."
        )
      }
      clean.contains("good in english") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("good in english", "good at English", ignoreCase = true),
          explanation = "In English, we say someone is 'good at' a subject, language, or activity, rather than 'good in'."
        )
      }
      clean.contains("discuss about") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("discuss about", "discuss", ignoreCase = true),
          explanation = "'Discuss' is a transitive verb that directly takes an object, so do not add 'about'."
        )
      }
      clean.contains("i eat rice yesterday") || clean.contains("i eat yesterday") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("eat", "ate", ignoreCase = true),
          explanation = "Use the past tense 'ate' when talking about actions that happened yesterday."
        )
      }
      clean.contains("i am work in") || clean.contains("i am work at") -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = userMessage.replace("i am work", "I work", ignoreCase = true),
          explanation = "For your regular occupation, use the simple present: 'I work', without 'am'."
        )
      }
      practiceMode == com.example.data.model.PracticeMode.REAL_LIFE_SITUATIONS &&
        roleplayScenario?.id == "ordering_food" && (clean.contains("i want one coffee") || clean.contains("i want a coffee")) -> {
        correction = SessionCorrection(
          sessionId = 0,
          originalSentence = userMessage,
          correctedSentence = "I'd like a coffee, please.",
          explanation = "In cafés and restaurants, 'I'd like a coffee, please' is much more polite and natural than 'I want one coffee'."
        )
      }
    }

    // 2. Formulate highly contextual, non-repetitive reply based on Practice Mode and content
    val isBeginner = level.equals("beginner", ignoreCase = true)
    val isAdvanced = level.equals("advanced", ignoreCase = true)

    val reply = when {
      // Roleplay Scenario: Ordering Food
      practiceMode == com.example.data.model.PracticeMode.REAL_LIFE_SITUATIONS && roleplayScenario?.id == "ordering_food" -> {
        when {
          clean.contains("coffee") || clean.contains("tea") || clean.contains("latte") || clean.contains("cappuccino") -> {
            "Sure thing! Would you like that hot or iced, and what size can I get for you?"
          }
          clean.contains("hot") || clean.contains("iced") || clean.contains("small") || clean.contains("medium") || clean.contains("large") -> {
            "Got it! Would you like any pastries or snacks to go with that today, like a croissant or blueberry muffin?"
          }
          clean.contains("croissant") || clean.contains("muffin") || clean.contains("cake") || clean.contains("pastry") || clean.contains("sandwich") -> {
            "Excellent choice! That'll be ready in just a moment. Would you like to pay with cash or card?"
          }
          clean.contains("card") || clean.contains("cash") || clean.contains("bill") || clean.contains("check") -> {
            "All set! Here is your receipt and order. Have a wonderful rest of your day!"
          }
          clean.contains("no thanks") || clean.contains("that's all") || clean.contains("nothing else") -> {
            "Sounds good! Your total comes to $4.75. Will that be card or cash today?"
          }
          else -> {
            "Welcome to Central Café! What can I get started for you today?"
          }
        }
      }

      // Roleplay Scenario: Airport
      practiceMode == com.example.data.model.PracticeMode.REAL_LIFE_SITUATIONS && roleplayScenario?.id == "airport" -> {
        when {
          clean.contains("passport") || clean.contains("ticket") || clean.contains("here") -> {
            "Thank you! I see your reservation. Do you have any bags or luggage to check in today, or just carry-on?"
          }
          clean.contains("bag") || clean.contains("luggage") || clean.contains("carry on") || clean.contains("suitcase") || clean.contains("no") -> {
            "Understood. Would you prefer a window seat or an aisle seat for your flight?"
          }
          clean.contains("window") || clean.contains("aisle") -> {
            "You're all set! Here is your boarding pass for seat 12F. Boarding begins at Gate 14 in 40 minutes. Have a pleasant flight!"
          }
          else -> {
            "Good day! Welcome to Star Airlines check-in. May I please see your passport and flight booking?"
          }
        }
      }

      // Roleplay Scenario: Job Interview
      practiceMode == com.example.data.model.PracticeMode.REAL_LIFE_SITUATIONS && roleplayScenario?.id == "job_interview" -> {
        when {
          clean.length > 50 && (clean.contains("experience") || clean.contains("work") || clean.contains("background") || clean.contains("graduated") || clean.contains("student")) -> {
            "That's a very solid background! What would you say is your greatest professional strength when working with a team?"
          }
          clean.contains("strength") || clean.contains("team") || clean.contains("communication") || clean.contains("problem") -> {
            "That's exactly what we look for in our team culture. Could you describe a time when you faced a difficult challenge and how you overcame it?"
          }
          clean.contains("challenge") || clean.contains("difficult") || clean.contains("solved") -> {
            "Impressive problem-solving skills! Do you have any questions for me about the company or the team?"
          }
          else -> {
            "It's great to meet you! To get started, could you briefly introduce yourself and share your key background?"
          }
        }
      }

      // AI Question Practice Mode
      practiceMode == com.example.data.model.PracticeMode.AI_QUESTIONS -> {
        val nextQuestion = if (isBeginner) {
          listOf(
            "What is your favorite food, and why do you like it?",
            "What do you usually enjoy doing on a relaxing Sunday?",
            "Could you describe your best friend in two sentences?",
            "What is your favorite season of the year, and why?"
          )
        } else if (isAdvanced) {
          listOf(
            "How do you believe artificial intelligence will influence career opportunities over the next decade?",
            "In your view, what distinct traits differentiate a good manager from a truly inspirational leader?",
            "Do you believe remote work fosters greater productivity, or does it compromise creative collaboration?"
          )
        } else {
          listOf(
            "If you could travel anywhere in the world tomorrow, where would you go and what would you do first?",
            "Describe a skill you would love to master this year and how you plan to practice it.",
            "What is a memorable book, movie, or talk that recently changed your perspective on life?"
          )
        }
        val q = nextQuestion[(clean.hashCode().mod(nextQuestion.size) + nextQuestion.size) % nextQuestion.size]
        "Well answered! You expressed your thoughts clearly.\n\nHere is your next question:\n$q"
      }

      // Speak and Correct Mode
      practiceMode == com.example.data.model.PracticeMode.SPEAK_AND_CORRECT -> {
        if (clean.length < 20) {
          "Good start! Try expanding your response into 2 or 3 sentences so we can analyze your vocabulary and sentence flow. What else can you add?"
        } else {
          "Great speaking flow! You used good sentence rhythm. To sound even more natural, try connecting your ideas with transition words like 'Furthermore', 'For instance', or 'On the other hand'. What else would you like to practice speaking about?"
        }
      }

      // Cricket & Sports Context
      clean.contains("cricket") || clean.contains("football") || clean.contains("match") || clean.contains("tournament") || clean.contains("game") -> {
        when {
          clean.contains("won") || clean.contains("win") || clean.contains("victory") || clean.contains("champion") -> {
            if (clean.contains("reward") || clean.contains("prize") || clean.contains("medal") || clean.contains("trophy")) {
              "Congratulations on winning the match and getting rewards! 🏆 What kind of rewards or trophies did you receive, and how did your team celebrate?"
            } else if (clean.contains("run") || clean.contains("runs") || clean.contains("wicket") || clean.contains("goal")) {
              "What a thrilling victory! Winning a close match is so exciting. Were you batting, bowling, or playing defense during the crucial moments?"
            } else {
              "Congratulations on the victory! Winning a match feels amazing. How close was the score, and what was your team's celebration like?"
            }
          }
          clean.contains("reward") || clean.contains("rewards") || clean.contains("trophy") || clean.contains("medal") -> {
            "That's an awesome accomplishment! Earning awards takes real effort. What did you receive, and who was the first person you told?"
          }
          clean.contains("lost") || clean.contains("lose") -> {
            "Close games can be tough, but they teach so much. What was the most exciting highlight of the match despite the result?"
          }
          else -> {
            "Sports are such an energizing topic! Do you play matches regularly with school, college, or neighborhood friends?"
          }
        }
      }

      // Rewards & Accomplishments
      clean.contains("reward") || clean.contains("rewards") || clean.contains("prize") || clean.contains("trophy") || clean.contains("medal") || clean.contains("award") -> {
        "Earning rewards is such a proud milestone! Could you describe what the award was for and how it felt when your name was called?"
      }

      // Everyday Greetings & State of Mind
      clean == "i am good" || clean == "i'm good" || clean == "i am fine" || clean == "i'm fine" || clean == "doing well" || clean == "pretty good" -> {
        if (isBeginner) {
          "I'm glad to hear that, $name! Did you do anything fun or relaxing today?"
        } else {
          "I'm really glad to hear you're doing well, $name! What's something noteworthy or interesting that happened in your day so far?"
        }
      }

      clean.contains("how are you") || clean.contains("how about you") -> {
        "I'm doing fantastic, thank you for asking! I'm always energized when helping you practice English. How has the rest of your week been going?"
      }

      clean.contains("nervous") || clean.contains("shy") || clean.contains("scared") || clean.contains("anxious") -> {
        "It is completely normal to feel nervous! Remember that making mistakes is the natural path to fluency. You are in a safe, judgment-free space with me. What would you like to talk about today?"
      }

      clean.contains("tired") || clean.contains("exhausted") || clean.contains("busy") || clean.contains("stressed") -> {
        "Sounds like you've had a demanding schedule! Even doing 5 minutes of practice while tired shows real dedication. Did you have a long day at work or school?"
      }

      clean.contains("weekend") || clean.contains("saturday") || clean.contains("sunday") -> {
        "Weekends are the best time to recharge! Do you usually prefer staying in to relax with movies, or heading out with friends?"
      }

      clean.contains("college") || clean.contains("university") || clean.contains("exam") || clean.contains("study") -> {
        "College life is always packed with activity! What subjects or projects are taking up most of your focus right now?"
      }

      clean.contains("job") || clean.contains("work") || clean.contains("office") || clean.contains("interview") -> {
        "Building strong professional English is a huge superpower for your career! Could you tell me about the kind of role or company you work in or are aiming for?"
      }

      clean.contains("movie") || clean.contains("series") || clean.contains("show") || clean.contains("music") || clean.contains("song") -> {
        "That sounds fascinating! What was the title, and what made you enjoy it so much?"
      }

      clean.contains("food") || clean.contains("cook") || clean.contains("dinner") || clean.contains("lunch") || clean.contains("breakfast") -> {
        "That sounds delicious! Did you cook it at home, or did you eat out at a favorite spot?"
      }

      clean.length < 15 -> {
        if (isBeginner) {
          "That's a nice start! Could you tell me a little bit more about that in one more sentence?"
        } else {
          "Nice start! Could you expand on your thought with a bit more detail or an example?"
        }
      }

      else -> {
        // Dynamic sentence extraction referencing user keywords
        val words = clean.split(Regex("[^a-zA-Z0-9]+")).filter { it.length > 4 && it !in setOf("about", "there", "their", "where", "which", "would", "could", "should", "today", "yesterday") }
        val topicWord = words.lastOrNull() ?: "that"
        if (isBeginner) {
          "You explained that well! What do you like most about $topicWord?"
        } else if (isAdvanced) {
          "That is a compelling insight regarding $topicWord. How has that experience influenced your broader perspective?"
        } else {
          "You expressed that clearly! What was the most interesting part about $topicWord for you?"
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
