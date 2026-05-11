package com.example.paryavarankavalu.ViewModel

// ui/screens/ReportViewModel.kt

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paryavarankavalu.ai.GeminiRepository
import com.example.paryavarankavalu.ai.WasteAnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val geminiRepository = GeminiRepository()

    private val _aiResult = MutableStateFlow<WasteAnalysisResult?>(null)
    val aiResult = _aiResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)

    private var lastAnalyzedUri: String? = null

    fun analyzeImage(bitmap: Bitmap, uriString: String) {
        if (uriString == lastAnalyzedUri) return   // ← prevents duplicate calls
        lastAnalyzedUri = uriString

        viewModelScope.launch {
            _isAnalyzing.value = true
            val result = geminiRepository.classifyWasteImage(bitmap)
            _aiResult.value = result

            if (result is WasteAnalysisResult.Success) {
                _selectedCategory.value = when (result.category) {
                    "PLASTIC"  -> "Plastic"
                    "ORGANIC"  -> "Organic"
                    "E_WASTE"  -> "E-Waste"
                    "PAPER"    -> "Paper"
                    "GLASS"    -> "Glass"
                    else       -> "Others"
                }
                _severity.value = result.severity
            }
            _isAnalyzing.value = false
        }
    }
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Plastic")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _severity = MutableStateFlow("LOW")
    val severity = _severity.asStateFlow()

    fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            val result = geminiRepository.classifyWasteImage(bitmap)
            _aiResult.value = result

            if (result is WasteAnalysisResult.Success) {
                // ← This is the auto-select logic
                _selectedCategory.value = when (result.category) {
                    "PLASTIC"  -> "Plastic"
                    "ORGANIC"  -> "Organic"
                    "E_WASTE"  -> "E-Waste"
                    "PAPER"    -> "Paper"
                    "GLASS"    -> "Glass"
                    else       -> "Others"
                }
                _severity.value = result.severity
            }
            _isAnalyzing.value = false
        }
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}