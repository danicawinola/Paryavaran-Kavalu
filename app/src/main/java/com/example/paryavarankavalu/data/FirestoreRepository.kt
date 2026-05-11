package com.example.paryavarankavalu.data

import com.example.paryavarankavalu.model.Report
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirestoreRepository {

    private val db         = Firebase.firestore
    private val reportsRef = db.collection("reports")

    // ── Upload a new report ───────────────────────────────────
    suspend fun uploadReport(report: Report): Boolean {
        return try {
            val data = hashMapOf(
                "category"  to report.category,
                "severity"  to report.severity,
                "status"    to report.status,
                "lat"       to report.lat,
                "lng"       to report.lng,
                "address"   to report.address,
                "timestamp" to System.currentTimeMillis(),
                "imageUri"  to (report.imageUri?.toString() ?: "")
            )
            val docRef = reportsRef.add(data).await()
            // Store docId back into the document for future updates
            reportsRef.document(docRef.id)
                .update("docId", docRef.id)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ── Mark a report as Cleaned ──────────────────────────────
    suspend fun markAsCleaned(documentId: String): Boolean {
        return try {
            reportsRef.document(documentId)
                .update("status", "Cleaned")
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ── Real-time listener — returns registration so caller
    //    can detach it via DisposableEffect.onDispose { }
    // ─────────────────────────────────────────────────────────
    fun listenToReports(
        onUpdate: (List<Map<String, Any>>) -> Unit
    ): ListenerRegistration {                           // ← returns registration
        return reportsRef
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("Firestore", "Listen error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener

                val docs = snapshot.documents.mapNotNull { doc ->
                    doc.data?.toMutableMap()?.also { map ->
                        map["docId"] = doc.id   // inject docId for updates
                    }
                }
                onUpdate(docs)
            }
    }
}