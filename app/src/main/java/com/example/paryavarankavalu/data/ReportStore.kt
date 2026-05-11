// data/ReportStore.kt
package com.example.paryavarankavalu.data

import androidx.compose.runtime.mutableStateListOf
import com.example.paryavarankavalu.model.Report
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ReportStore {

    val reports = mutableStateListOf<Report>()

    fun addReport(report: Report) {
        // 1. Save locally first — instant UI update
        reports.add(report)

        // 2. Award karma
        KarmaStore.onReportSubmitted(
            category = report.category,
            severity = report.severity
        )

        // 3. Milestone check
        if (reports.size % 10 == 0) {
            KarmaStore.onMilestone("${reports.size} Reports Milestone! 🎉")
        }

        // 4. Sync to Firestore in background — non-blocking
        CoroutineScope(Dispatchers.IO).launch {
            val success = FirestoreRepository.uploadReport(report)
            if (!success) {
                // TODO: Queue for retry when internet returns
                android.util.Log.e("ReportStore", "Failed to sync report to Firestore")
            }
        }
    }

    fun markCleaned(report: Report) {
        val index = reports.indexOf(report)
        if (index != -1) {
            // 1. Update local state immediately
            reports[index].status = "Cleaned"

            // 2. Award karma
            KarmaStore.onReportCleaned(report.category)

            // 3. Sync to Firestore so it persists across restarts
            if (report.docId.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val success = FirestoreRepository.markAsCleaned(report.docId)
                    if (!success) {
                        android.util.Log.e("ReportStore", "Failed to update cleaned status in Firestore")
                    }
                }
            } else {
                android.util.Log.w("ReportStore", "docId is empty — cannot sync cleaned status")
            }
        }
    }
}