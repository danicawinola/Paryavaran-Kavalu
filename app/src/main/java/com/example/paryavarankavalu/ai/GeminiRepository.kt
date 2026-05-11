package com.example.paryavarankavalu.ai


import android.graphics.Bitmap
import com.example.paryavarankavalu.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject

class GeminiRepository {

    private val model = GenerativeModel(

        modelName = "gemini-2.0-flash-lite",

        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun classifyWasteImage(
        imageBitmap: Bitmap
    ): WasteAnalysisResult {

        return try {

            val prompt = """
                Analyze this waste-management image.

                Classify it into EXACTLY one category:
                PLASTIC, ORGANIC, E_WASTE, PAPER, GLASS, MIXED

                Also determine:
                - severity
                - cleanup urgency

                Return ONLY valid JSON:

                {
                  "category": "PLASTIC",
                  "confidence": 92,
                  "severity": "HIGH",
                  "reason": "Large visible garbage accumulation"
                }
            """.trimIndent()

            val response =
                model.generateContent(

                    content {

                        image(imageBitmap)

                        text(prompt)
                    }

                )
            android.util.Log.d(
                "GEMINI_RESPONSE",
                response.text ?: "EMPTY"
            )
            val json =
                response.text
                    ?: throw Exception("Empty response")

            parseGeminiResponse(json)

        } catch (e: Exception) {

            e.printStackTrace()

            WasteAnalysisResult.Error(
                e.message ?: "Analysis failed"
            )
        }
    }

    private fun parseGeminiResponse(
        json: String
    ): WasteAnalysisResult {

        val clean = json.trim()

            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")

            .trim()

        val obj = JSONObject(clean)

        return WasteAnalysisResult.Success(

            category =
                obj.getString("category"),

            confidence =
                obj.getString("confidence")
                    .replace("%", "")
                    .toInt(),

            severity =
                obj.getString("severity"),

            reason =
                obj.getString("reason")
        )
    }
}