package com.example.paryavarankavalu.ai


sealed class WasteAnalysisResult {

    data class Success(

        val category: String,

        val confidence: Int,

        val severity: String,

        val reason: String

    ) : WasteAnalysisResult()

    data class Error(

        val message: String

    ) : WasteAnalysisResult()
}