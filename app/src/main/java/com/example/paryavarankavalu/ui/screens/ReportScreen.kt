package com.example.paryavarankavalu.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.paryavarankavalu.data.ReportStore
import com.example.paryavarankavalu.model.Report
import com.google.android.gms.location.LocationServices
import java.util.Locale

// ── Colors ────────────────────────────────────────────────────
private val GreenDark    = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val BgColor      = Color(0xFFF5EFE6)
private val CardBg       = Color(0xFFEDE4DA)

@Composable
fun ReportScreen(navController: NavHostController) {

    val context = LocalContext.current

    // ── Local UI state ────────────────────────────────────────
    var imageUri         by remember { mutableStateOf<Uri?>(null) }
    var locationText     by remember { mutableStateOf("Fetching location...") }
    var reportLat        by remember { mutableStateOf(0.0) }    // ✅ GPS lat
    var reportLng        by remember { mutableStateOf(0.0) }    // ✅ GPS lng
    var selectedCategory by remember { mutableStateOf("") }     // single selection
    var selectedSeverity by remember { mutableStateOf("MEDIUM") }

    // ── Location helper ───────────────────────────────────────
    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        LocationServices
            .getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    reportLat = location.latitude    // ✅ saved for map pin
                    reportLng = location.longitude   // ✅ saved for map pin

                    val geocoder  = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1
                    )
                    if (!addresses.isNullOrEmpty()) {
                        val city  = addresses[0].locality  ?: "Unknown"
                        val state = addresses[0].adminArea ?: ""
                        locationText = "📍 $city, $state"
                    } else {
                        locationText = "📍 Location unavailable"
                    }
                } else {
                    locationText = "📍 Location unavailable"
                }
            }
    }

    // ── Permission launcher ───────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) fetchLocation()
        else locationText = "📍 Permission denied"
    }

    // ── Image picker ──────────────────────────────────────────
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    // ── Auto-fetch location on launch ─────────────────────────
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) fetchLocation()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // ─────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(horizontal = 16.dp)
                .padding(bottom = 90.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Top bar ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("← Back", color = Color.Black)
                }
                Text(
                    "New Report",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("👤", fontSize = 20.sp)
            }

            // ── Step indicator ────────────────────────────────
            StepIndicator(
                photoActive    = imageUri != null,
                categoryActive = selectedCategory.isNotEmpty(),
                locationActive = reportLat != 0.0
            )

            Spacer(Modifier.height(20.dp))

            // ── Photo card ────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                onClick = { imageLauncher.launch("image/*") }
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", fontSize = 36.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Capture Waste", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Tap to take or choose a photo",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    } else {
                        AsyncImage(
                            model              = imageUri,
                            contentDescription = "Selected waste image",
                            modifier           = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Category ──────────────────────────────────────
            Text(
                "Select Waste Category",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Tap one category that best describes the waste",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(Modifier.height(10.dp))

            val categories = listOf(
                "Plastic", "Organic", "E-Waste", "Paper", "Glass", "Others"
            )

            categories.chunked(2).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    rowItems.forEach { item ->
                        val isSelected = item == selectedCategory
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.dp, GreenDark, RoundedCornerShape(12.dp)
                                    ) else Modifier
                                ),
                            shape  = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) GreenPrimary else Color.White
                            ),
                            onClick = { selectedCategory = item }   // ✅ single tap selects
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    item,
                                    color      = if (isSelected) Color.White else Color.Black,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Severity ──────────────────────────────────────
            Text(
                "Severity Level",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "How serious is the waste situation?",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(Modifier.height(10.dp))

            data class SeverityOption(
                val label: String,
                val emoji: String,
                val activeColor: Color,
                val activeBg: Color
            )

            val severityOptions = listOf(
                SeverityOption("LOW",    "🟢", Color(0xFF2E7D32), Color(0xFFE8F5E9)),
                SeverityOption("MEDIUM", "🟡", Color(0xFFE65100), Color(0xFFFFF3E0)),
                SeverityOption("HIGH",   "🔴", Color(0xFFB71C1C), Color(0xFFFFEBEE))
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                severityOptions.forEach { option ->
                    val isSelected = selectedSeverity == option.label
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .then(
                                if (isSelected) Modifier.border(
                                    2.dp, option.activeColor, RoundedCornerShape(12.dp)
                                ) else Modifier
                            ),
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) option.activeBg else Color.White
                        ),
                        onClick = { selectedSeverity = option.label }
                    ) {
                        Column(
                            modifier            = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(option.emoji, fontSize = 18.sp)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                option.label,
                                fontSize   = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color      = if (isSelected) option.activeColor else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Location card ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
            ) {
                Row(
                    modifier              = Modifier.padding(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(locationText, color = Color.DarkGray, modifier = Modifier.weight(1f))
                    if (reportLat != 0.0) {
                        Text(
                            "%.4f, %.4f".format(reportLat, reportLng),
                            fontSize = 10.sp,
                            color    = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(100.dp))
        }

        // ── Submit button ─────────────────────────────────────
        Button(
            onClick = {
                val report = Report(
                    imageUri = imageUri,
                    category = selectedCategory,
                    severity = selectedSeverity,   // ✅ real severity (not "PENDING")
                    lat      = reportLat,           // ✅ GPS lat → shows on map
                    lng      = reportLng,           // ✅ GPS lng → shows on map
                    address  = locationText
                )
                ReportStore.addReport(report)       // ✅ triggers karma points
                navController.navigate("success")
            },
            enabled = imageUri != null && selectedCategory.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = GreenPrimary,
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                "Confirm & Submit →",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 16.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Step Indicator
// ─────────────────────────────────────────────────────────────
@Composable
fun StepIndicator(
    photoActive: Boolean,
    categoryActive: Boolean,
    locationActive: Boolean
) {
    val green = Color(0xFF2E7D32)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        StepNode("📷", "Photo",    photoActive)
        HorizontalDivider(
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            color    = if (photoActive) green else Color.LightGray
        )
        StepNode("♻️", "Category", categoryActive)
        HorizontalDivider(
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            color    = if (categoryActive) green else Color.LightGray
        )
        StepNode("📍", "Location", locationActive)
    }
}

// ─────────────────────────────────────────────────────────────
// Step Node
// ─────────────────────────────────────────────────────────────
@Composable
fun StepNode(icon: String, label: String, active: Boolean) {
    val green = Color(0xFF2E7D32)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = if (active) green else Color.LightGray,
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 18.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style      = MaterialTheme.typography.labelSmall,
            color      = if (active) green else Color.Gray,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}