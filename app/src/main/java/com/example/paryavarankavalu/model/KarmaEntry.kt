package com.example.paryavarankavalu.model


data class KarmaEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,       // e.g. "Yesterday, 4:30 PM"
    val points: Int,            // positive = earned
    val icon: String,           // emoji icon
    val timestamp: Long = System.currentTimeMillis()
)