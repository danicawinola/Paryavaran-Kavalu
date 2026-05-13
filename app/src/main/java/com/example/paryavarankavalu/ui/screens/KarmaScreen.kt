package com.example.paryavarankavalu.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paryavarankavalu.data.KarmaStore
import com.example.paryavarankavalu.model.Badge

// ── Colors matching app theme ─────────────────────────────────
private val GreenDark    = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenMid     = Color(0xFF43A047)
private val GreenLight   = Color(0xFFE8F5E9)
private val GreenAccent  = Color(0xFF69F0AE)
private val BgColor      = Color(0xFFF5EFE6)
private val CardBg       = Color(0xFFFFFFFF)
private val TextGray     = Color(0xFF757575)
private val LockGray     = Color(0xFFBDBDBD)

// ─────────────────────────────────────────────────────────────
//  KarmaScreen
// ─────────────────────────────────────────────────────────────
@Composable
fun KarmaScreen() {

    val viewModel: KarmaViewModel = viewModel()

    // Refresh whenever KarmaStore changes
    val totalFromStore by KarmaStore.totalPoints
    LaunchedEffect(totalFromStore) { viewModel.refresh() }

    val state by viewModel.uiState.collectAsState()

    // Animate progress bar on first load
    var progressVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { progressVisible = true }
    val animatedProgress by animateFloatAsState(
        targetValue    = if (progressVisible) state.progressPercent else 0f,
        animationSpec  = tween(durationMillis = 1000),
        label          = "karma_progress"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(WindowInsets.statusBars.asPaddingValues()),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {

        // ── Header ─────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🌿", fontSize = 20.sp)
                    Text(
                        "Paryavaran-Kavalu",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        color      = GreenDark
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("👤", fontSize = 16.sp) }
            }
        }

        // ── Karma Card ─────────────────────────────────────────
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                shape  = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GreenDark, GreenMid)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Shield icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    Color.White.copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                state.currentBadge.icon,
                                fontSize = 28.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Points
                        // ✅ REPLACE WITH THIS
                        Text(
                            "%,d Eco-Karma Points".format(state.totalPoints),
                            color      = Color.White,
                            fontSize   = 22.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        // Badge status label
                        Text(
                            "${state.currentBadge.name} Status",
                            color    = GreenAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(16.dp))

                        // Progress bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Next Level${
                                        state.nextBadge?.let {
                                            ": ${"%,d".format(it.requiredPoints)}"
                                        } ?: " — Max Reached"
                                    }",
                                    color    = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    "${(animatedProgress * 100).toInt()}% Complete",
                                    color    = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress       = { animatedProgress },
                                modifier       = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50)),
                                color          = GreenAccent,
                                trackColor     = Color.White.copy(alpha = 0.25f),
                                strokeCap      = StrokeCap.Round,
                            )
                        }
                    }
                }
            }
        }

        // ── Badges Section ─────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Badges Earned",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = GreenDark
                )
                TextButton(onClick = {}) {
                    Text(
                        "View All",
                        color    = GreenPrimary,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Badge row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.allBadges.take(5).forEach { badge ->
                    val isEarned = state.earnedBadges.contains(badge)
                    BadgeChip(badge = badge, isEarned = isEarned)
                }
            }
        }

        // ── Points System Info Card ────────────────────────────
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenLight)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "How to Earn Points",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        color      = GreenDark
                    )
                    Spacer(Modifier.height(10.dp))
                    PointsInfoRow("📍", "Submit any report",          "+10 pts")
                    PointsInfoRow("🔺", "Report is HIGH severity",    "+15 bonus")
                    PointsInfoRow("⭐", "First report of the day",    "+5 streak")
                    PointsInfoRow("♻️", "Your spot gets cleaned",     "+25 pts")
                    PointsInfoRow("🏆", "10 reports milestone",       "+50 pts")
                }
            }
        }

        // ── Activity History ───────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Activity History",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = GreenDark
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // Empty state
        if (state.recentEntries.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌱", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No activity yet",
                        color      = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Submit your first report to start earning Karma!",
                        color     = TextGray,
                        fontSize  = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Activity items
        items(state.recentEntries) { entry ->
            ActivityRow(entry = entry)
        }

        // "View Full History" button
        if (state.recentEntries.size >= 5) {
            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick  = { /* navigate to full history */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = GreenPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, GreenPrimary
                    )
                ) {
                    Text("View Full History", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Badge Chip — earned = colored, locked = greyed + strikethrough X
// ─────────────────────────────────────────────────────────────
@Composable
fun RowScope.BadgeChip(badge: Badge, isEarned: Boolean) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = if (isEarned) GreenPrimary else LockGray,
                    shape = CircleShape
                )
                .then(
                    if (isEarned) Modifier.border(
                        2.dp, GreenAccent, CircleShape
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isEarned) {
                Text(badge.icon, fontSize = 22.sp)
            } else {
                // Locked — show X overlay
                Text("✕", fontSize = 18.sp, color = Color.White)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            badge.name,
            fontSize   = 11.sp,
            fontWeight = if (isEarned) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (isEarned) GreenDark else LockGray,
            textAlign  = TextAlign.Center,
            maxLines   = 1
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Activity Row — each karma earning event
// ─────────────────────────────────────────────────────────────
@Composable
fun ActivityRow(entry: com.example.paryavarankavalu.model.KarmaEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GreenLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.icon, fontSize = 20.sp)
            }

            Spacer(Modifier.width(12.dp))

            // Title + subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = Color(0xFF212121)
                )
                Text(
                    entry.subtitle,
                    fontSize = 12.sp,
                    color    = TextGray
                )
            }

            // Points
            Text(
                "+${entry.points}",
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = GreenPrimary
            )
            Text(
                "\nKarma",
                fontSize  = 10.sp,
                color     = TextGray,
                textAlign = TextAlign.End
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Points Info Row — used in the info card
// ─────────────────────────────────────────────────────────────
@Composable
fun PointsInfoRow(icon: String, label: String, points: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 16.sp)
        Spacer(Modifier.width(10.dp))
        Text(
            label,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color    = Color(0xFF424242)
        )
        Text(
            points,
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp,
            color      = GreenPrimary
        )
    }
}