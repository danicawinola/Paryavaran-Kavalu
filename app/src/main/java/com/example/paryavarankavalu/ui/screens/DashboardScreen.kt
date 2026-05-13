package com.example.paryavarankavalu.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.paryavarankavalu.data.ReportStore
import com.example.paryavarankavalu.model.Report

// ── Colors ────────────────────────────────────────────────────
private val GreenDark    = Color(0xFF14532D)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenLight   = Color(0xFFB9F6A5)
private val CreamBg      = Color(0xFFF8F5F0)
private val CardBg       = Color.White
private val RedSoft      = Color(0xFFFFD6D6)
private val YellowSoft   = Color(0xFFFFEDB5)

@Composable
fun DashboardScreen() {

    val allReports   = ReportStore.reports

    // ✅ Fix 1 — only show PENDING reports on dashboard
    // Cleaned reports go to map (green pin) — not shown here
    val pendingReports = allReports.filter { it.status == "Pending" }
    val cleanedCount   = allReports.count  { it.status == "Cleaned" }
    val totalCount     = allReports.size

    // Weekly progress — real number from store
    val weeklyProgress = if (totalCount > 0)
        cleanedCount.toFloat() / totalCount.toFloat()
    else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Header ────────────────────────────────────────────
        item {
            Spacer(Modifier.height(16.dp))
            DashboardHeader()
        }

        // ── Empty state ───────────────────────────────────────
        if (pendingReports.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌿", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "All clear!",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = GreenDark
                        )
                        Text(
                            "No pending reports in your area.",
                            color     = Color.Gray,
                            fontSize  = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // ── Pending report cards ───────────────────────────────
        items(pendingReports) { report ->
            VolunteerReportCard(report = report)
        }

        // ── Weekly progress card ───────────────────────────────
        item {
            WeeklyProgressCard(
                cleanedCount   = cleanedCount,
                weeklyProgress = weeklyProgress
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Dashboard Header
// ─────────────────────────────────────────────────────────────
@Composable
fun DashboardHeader() {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Volunteer Dashboard",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = GreenDark
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Active pending reports in your area",
                color      = Color.Gray,
                fontSize   = 13.sp,
                lineHeight = 18.sp
            )
        }
        Surface(shape = CircleShape, color = Color.White, tonalElevation = 2.dp) {
            Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GreenDark)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Report Card
// ─────────────────────────────────────────────────────────────
@Composable
fun VolunteerReportCard(report: Report) {

    // ✅ Fix 2 — animate button color when marked cleaned
    val isCleaned = report.status == "Cleaned"
    val buttonColor by animateColorAsState(
        targetValue   = if (isCleaned) Color(0xFF9E9E9E) else GreenPrimary,
        animationSpec = tween(400),
        label         = "btn_color"
    )

    val severityColor = when (report.severity.uppercase()) {
        "HIGH"   -> RedSoft
        "MEDIUM" -> YellowSoft
        else     -> GreenLight
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ✅ Fix 3 — photo box with fallback for Firestore reports
                // (imageUri is null for reports loaded from cloud)
                Box(
                    modifier            = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment    = Alignment.Center
                ) {
                    if (report.imageUri != null) {
                        AsyncImage(
                            model              = report.imageUri,
                            contentDescription = null,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        // Fallback for cloud-loaded reports (no local URI)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when (report.category) {
                                    "Plastic"  -> "♻️"
                                    "Organic"  -> "🌿"
                                    "E-Waste"  -> "🔌"
                                    "Paper"    -> "📄"
                                    "Glass"    -> "🪟"
                                    else       -> "🗑️"
                                },
                                fontSize = 28.sp
                            )
                            Text(
                                report.category,
                                fontSize = 9.sp,
                                color    = Color.Gray
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.Top
                    ) {
                        Text(
                            report.category,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 20.sp,
                            color      = GreenDark,
                            modifier   = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = severityColor
                        ) {
                            Text(
                                "${report.severity} Severity",
                                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = GreenDark
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // ✅ Fix 4 — show REAL address from report
                    Text(
                        report.address.ifEmpty { "📍 Location not available" },
                        color    = Color.Gray,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    // ✅ Fix 5 — show real status not hardcoded text
                    Text(
                        if (isCleaned) "✅ Marked as Cleaned" else "🕐 Pending cleanup",
                        color    = if (isCleaned) GreenPrimary else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = if (isCleaned) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ✅ Fix 6 — calls ReportStore.markCleaned() which:
            //   • Updates local status
            //   • Syncs to Firestore (persists across restarts)
            //   • Awards +25 Eco-Karma points
            Button(
                onClick = {
                    if (!isCleaned) {
                        ReportStore.markCleaned(report)
                    }
                },
                enabled  = !isCleaned,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape  = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = buttonColor,
                    disabledContainerColor = Color(0xFFBDBDBD)
                )
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isCleaned) "Cleaned ✓" else "Mark as Cleaned",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp,
                    color      = Color.White
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Weekly Progress Card — driven by real data
// ─────────────────────────────────────────────────────────────
@Composable
fun WeeklyProgressCard(
    cleanedCount: Int,
    weeklyProgress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = GreenLight)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Weekly Progress",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = GreenDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    // ✅ Fix 7 — real cleanup count not hardcoded
                    "$cleanedCount cleanup actions this week",
                    color    = GreenDark.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress      = { weeklyProgress },
                    color         = GreenDark,
                    trackColor    = Color.White.copy(alpha = 0.5f),
                    strokeWidth   = 7.dp,
                    modifier      = Modifier.size(62.dp)
                )
                Text(
                    "${(weeklyProgress * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    color      = GreenDark,
                    fontSize   = 14.sp
                )
            }
        }
    }
}