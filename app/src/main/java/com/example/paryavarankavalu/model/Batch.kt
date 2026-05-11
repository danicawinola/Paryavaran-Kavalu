package com.example.paryavarankavalu.model


data class Badge(
    val name: String,
    val icon: String,
    val requiredPoints: Int,
    val description: String
)

// ── Badge Definitions ─────────────────────────────────────────
// Points system:
//   +10  Submit any report
//   +15  Report is HIGH severity
//   + 5  First report of the day (streak bonus)
//   +25  Your reported spot gets marked Cleaned
//   +50  One-time: 10 reports milestone
// ─────────────────────────────────────────────────────────────
object BadgeCatalog {
    val all = listOf(
        Badge(
            name            = "Sapling",
            icon            = "🌱",
            requiredPoints  = 0,
            description     = "Welcome! You've joined the movement."
        ),
        Badge(
            name            = "Guardian",
            icon            = "🛡️",
            requiredPoints  = 100,
            description     = "Filed 10+ reports. Your ward is safer."
        ),
        Badge(
            name            = "Warrior",
            icon            = "⚔️",
            requiredPoints  = 500,
            description     = "500 Karma. You're driving real change."
        ),
        Badge(
            name            = "Champion",
            icon            = "🏆",
            requiredPoints  = 1000,
            description     = "1,000 Karma. A true civic champion."
        ),
        Badge(
            name            = "Legend",
            icon            = "🌟",
            requiredPoints  = 2500,
            description     = "2,500 Karma. Steward of the community."
        )
    )

    fun earnedBadges(totalPoints: Int): List<Badge> =
        all.filter { it.requiredPoints <= totalPoints }

    fun nextBadge(totalPoints: Int): Badge? =
        all.firstOrNull { it.requiredPoints > totalPoints }

    fun currentBadge(totalPoints: Int): Badge =
        all.filter { it.requiredPoints <= totalPoints }.last()
}