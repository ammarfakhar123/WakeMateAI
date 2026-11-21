package com.wakemate.ai.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun getAiResponse(
        prompt: String,
        apiKey: String,
        personality: String,
        pendingTaskSummary: String
    ): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            val systemInstruction = "You are WakeMate AI, a smart alarm clock and daily productivity assistant. " +
                    "Your personality is $personality. The user's current pending tasks are: $pendingTaskSummary. " +
                    "Communicate naturally and support English, Urdu, and Roman Urdu. Keep responses concise (under 3 sentences) and action-oriented."

            val jsonBody = JsonObject().apply {
                val contents = com.google.gson.JsonArray().apply {
                    val contentObj = JsonObject().apply {
                        val parts = com.google.gson.JsonArray().apply {
                            val systemPart = JsonObject().apply {
                                addProperty("text", "$systemInstruction\n\nUser: $prompt")
                            }
                            add(systemPart)
                        }
                        add("parts", parts)
                    }
                    add(contentObj)
                }
                add("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val responseBody = response.body?.string() ?: return@withContext null
                val root = gson.fromJson(responseBody, JsonObject::class.java)
                val candidates = root.getAsJsonArray("candidates")
                if (candidates != null && candidates.size() > 0) {
                    val candidate = candidates[0].asJsonObject
                    val content = candidate.getAsJsonObject("content")
                    val parts = content.getAsJsonArray("parts")
                    if (parts != null && parts.size() > 0) {
                        return@withContext parts[0].asJsonObject.get("text").asString.trim()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }
}
