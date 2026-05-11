package com.example.paryavarankavalu.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.paryavarankavalu.model.KarmaEntry
import java.text.SimpleDateFormat
import java.util.*

object KarmaStore {

    val entries      = mutableStateListOf<KarmaEntry>()
    val totalPoints  = mutableStateOf(0)

    private var lastReportDate: String? = null
    private var prefs: android.content.SharedPreferences? = null

    // ── Call this ONCE from MainActivity on app start ─────────
    fun init(context: Context) {
        prefs = context.getSharedPreferences("karma_prefs", Context.MODE_PRIVATE)
        // Restore saved points on app launch
        totalPoints.value = prefs?.getInt("total_points", 0) ?: 0
        lastReportDate    = prefs?.getString("last_report_date", null)
    }

    fun onReportSubmitted(category: String, severity: String) {
        var points = 10
        if (severity == "HIGH")   points += 15
        if (severity == "MEDIUM") points += 5

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (lastReportDate != today) {
            points += 5
            lastReportDate = today
            prefs?.edit()?.putString("last_report_date", today)?.apply()
        }

        addEntry("$category Waste Reported", formattedNow(), points, iconFor(category))
    }

    fun onReportCleaned(category: String) {
        addEntry("$category Spot Cleaned!", formattedNow(), 25, "✅")
    }

    fun onMilestone(label: String) {
        addEntry(label, formattedNow(), 50, "🏅")
    }

    private fun addEntry(title: String, subtitle: String, points: Int, icon: String) {
        entries.add(0, KarmaEntry(title = title, subtitle = subtitle, points = points, icon = icon))
        totalPoints.value += points

        // ✅ Save to SharedPreferences immediately
        prefs?.edit()?.putInt("total_points", totalPoints.value)?.apply()
    }

    private fun formattedNow(): String =
        SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault()).format(Date())

    private fun iconFor(category: String): String = when (category) {
        "Plastic" -> "♻️"; "Organic" -> "🌿"; "E-Waste" -> "🔌"
        "Paper"   -> "📄"; "Glass"   -> "🪟"; else      -> "🗑️"
    }
}