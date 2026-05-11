package com.example.paryavarankavalu.model

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Report(
    val imageUri: Uri?,
    val category: String,
    val severity: String = "MEDIUM",
    val lat: Double = 0.0,       // ← GPS latitude saved on submit
    val lng: Double = 0.0,       // ← GPS longitude saved on submit
    val address: String = "" ,    // ← reverse-geocoded address
    val docId:    String = ""
) {
    var status by mutableStateOf("Pending")   // "Pending" | "Cleaned"
}