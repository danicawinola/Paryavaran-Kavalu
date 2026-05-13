package com.example.paryavarankavalu.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.paryavarankavalu.data.KarmaStore

// ── Colors ────────────────────────────────────────────────────
private val GreenDark      = Color(0xFF1B5E20)
private val GreenPrimary   = Color(0xFF2E7D32)
private val GreenMid       = Color(0xFF43A047)
private val GreenLight     = Color(0xFF81C784)
private val GreenPale      = Color(0xFFE8F5E9)
private val BgTop          = Color(0xFFF0F7F0)
private val BgBottom       = Color(0xFFFFFFFF)

@Composable
fun SubmissionSuccessScreen(navController: NavHostController) {

    // ── Karma points earned this session ──────────────────────
    // Show current total — points were already added in ReportStore
    val karmaTotal = KarmaStore.totalPoints.value

    // ── Pulse animation for the check circle ──────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // ── Entry scale animation ─────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    val contentScale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0.85f,
        animationSpec = tween(500, easing = EaseOutBack),
        label         = "content_scale"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgTop, BgBottom)
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .scale(contentScale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── App logo pill ─────────────────────────────────
            Surface(
                shape  = RoundedCornerShape(20.dp),
                color  = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🌿", fontSize = 16.sp)
                    Text(
                        "Paryavaran-Kavalu",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 13.sp,
                        color      = GreenDark
                    )
                }
            }

            // ── Animated check circle ─────────────────────────
            Box(
                modifier         = Modifier
                    .size(140.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GreenLight.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Middle ring
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GreenMid.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Inner filled circle
                Box(
                    modifier         = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GreenMid, GreenPrimary)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Headline ──────────────────────────────────────
            Text(
                "Great Job,",
                fontSize   = 32.sp,
                fontWeight = FontWeight.Bold,
                color      = GreenDark,
                textAlign  = TextAlign.Center
            )
            Text(
                "Eco-Guardian!",
                fontSize   = 32.sp,
                fontWeight = FontWeight.Bold,
                color      = GreenPrimary,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Your report has been received.\nTogether, we're making the planet\ncleaner one step at a time.",
                fontSize   = 14.sp,
                color      = Color(0xFF757575),
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Karma earned card ─────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Icon badge
                        Box(
                            modifier         = Modifier
                                .size(40.dp)
                                .background(GreenPale, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌱", fontSize = 18.sp)
                        }
                        Text(
                            "Karma Earned",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp,
                            color      = Color(0xFF212121)
                        )
                    }
                    Text(
                        "+10",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 22.sp,
                        color      = GreenPrimary
                    )
                }
            }

            // Total karma row
            Spacer(Modifier.height(8.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Total Eco-Karma: ",
                    fontSize = 13.sp,
                    color    = Color.Gray
                )
                Text(
                    "$karmaTotal pts",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GreenPrimary
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Back to Map button ────────────────────────────
            Button(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("report") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                shape  = RoundedCornerShape(50)
            ) {
                Text(
                    "🗺  Back to Map",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp,
                    color      = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── View report history link ───────────────────────
            TextButton(
                onClick = {
                    navController.navigate("karma") {
                        popUpTo("report") { inclusive = true }
                    }
                }
            ) {
                Text(
                    "View Report History",
                    color      = GreenPrimary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}