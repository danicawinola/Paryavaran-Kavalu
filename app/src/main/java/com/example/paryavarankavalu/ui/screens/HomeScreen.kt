package com.example.paryavarankavalu.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.paryavarankavalu.data.FirestoreRepository
import com.example.paryavarankavalu.data.ReportStore
import com.example.paryavarankavalu.model.Report
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// ── Colors ────────────────────────────────────────────────────
private val GreenDark    = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val BgColor      = Color(0xFFF4F1EA)

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(navController: NavHostController) {

    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var currentLocation by remember {
        mutableStateOf(LatLng(12.2958, 76.6394))  // Mysuru fallback
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 14f)
    }

    var selectedFilter by remember { mutableStateOf("All") }

    // ── Permission launcher ───────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(currentLocation, 14f)
                }
            }
        }
    }

    // ── GPS fetch on launch ───────────────────────────────────
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(currentLocation, 14f)
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // ── Firestore real-time listener ──────────────────────────
    // DisposableEffect properly removes the listener when screen
    // leaves composition — prevents memory leaks & ghost updates
    DisposableEffect(Unit) {
        val registration = FirestoreRepository.listenToReports { firestoreDocs ->

            firestoreDocs.forEach { doc ->
                val lat      = doc["lat"]      as? Double ?: 0.0
                val lng      = doc["lng"]      as? Double ?: 0.0
                val category = doc["category"] as? String ?: return@forEach
                val severity = doc["severity"] as? String ?: "LOW"
                val status   = doc["status"]   as? String ?: "Pending"
                val address  = doc["address"]  as? String ?: ""
                val docId    = doc["docId"]    as? String ?: ""  // ✅ needed for markCleaned

                if (lat == 0.0 || lng == 0.0) return@forEach

                // Check if this report already exists locally
                val existingIndex = ReportStore.reports.indexOfFirst { local ->
                    local.lat == lat &&
                            local.lng == lng &&
                            local.category == category
                }

                if (existingIndex == -1) {
                    // ✅ New report — add it with docId
                    val report = Report(
                        imageUri = null,
                        category = category,
                        severity = severity,
                        lat      = lat,
                        lng      = lng,
                        address  = address,
                        docId    = docId        // ✅ stored so markCleaned can update Firestore
                    ).apply { this.status = status }

                    ReportStore.reports.add(report)

                } else {
                    // ✅ Report exists — sync status from Firestore
                    // This is what makes "Cleaned" persist after restart
                    if (ReportStore.reports[existingIndex].status != status) {
                        ReportStore.reports[existingIndex].status = status
                    }
                }
            }
        }

        // Remove Firestore listener when screen leaves composition
        onDispose { registration.remove() }
    }

    // ── Live report data ──────────────────────────────────────
    val allReports = ReportStore.reports

    val filteredReports = remember(allReports.size, selectedFilter) {
        when (selectedFilter) {
            "Pending" -> allReports.filter { it.status == "Pending" }
            "Cleaned" -> allReports.filter { it.status == "Cleaned" }
            else      -> allReports.toList()
        }
    }

    val pendingCount = allReports.count { it.status == "Pending" }
    val cleanedCount = allReports.count { it.status == "Cleaned" }

    // ─────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        // ── Top bar ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Text(
                    "🌿 Paryavaran-Kavalu",
                    modifier   = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Surface(shape = CircleShape, color = Color.White) {
                Text("👤", modifier = Modifier.padding(14.dp))
            }
        }

        // ── Filter chips ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            FilterButton(
                text     = "All",
                count    = allReports.size,
                selected = selectedFilter == "All"
            ) { selectedFilter = "All" }

            FilterButton(
                text     = "Pending",
                count    = pendingCount,
                selected = selectedFilter == "Pending"
            ) { selectedFilter = "Pending" }

            FilterButton(
                text     = "Cleaned",
                count    = cleanedCount,
                selected = selectedFilter == "Cleaned"
            ) { selectedFilter = "Cleaned" }
        }

        Spacer(Modifier.height(12.dp))

        // ── Map ───────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {

            GoogleMap(
                modifier            = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties          = MapProperties(isMyLocationEnabled = true)
            ) {
                filteredReports.forEach { report ->
                    if (report.lat != 0.0 && report.lng != 0.0) {

                        // 🔴 Pending = RED  |  🟢 Cleaned = GREEN
                        val pinColor = when (report.status) {
                            "Cleaned" -> BitmapDescriptorFactory.HUE_GREEN
                            else      -> BitmapDescriptorFactory.HUE_RED
                        }

                        val severityEmoji = when (report.severity) {
                            "HIGH"   -> "🔴"
                            "MEDIUM" -> "🟡"
                            else     -> "🟢"
                        }

                        Marker(
                            state   = MarkerState(LatLng(report.lat, report.lng)),
                            title   = "${report.category} Waste",
                            snippet = "$severityEmoji ${report.severity} · ${report.status}",
                            icon    = BitmapDescriptorFactory.defaultMarker(pinColor)
                        )
                    }
                }
            }

            // ── Legend card ───────────────────────────────────
            if (allReports.isNotEmpty()) {
                Card(
                    modifier  = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🔴", fontSize = 12.sp)
                            Text(
                                "$pendingCount Pending",
                                fontSize   = 12.sp,
                                color      = Color(0xFFB71C1C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🟢", fontSize = 12.sp)
                            Text(
                                "$cleanedCount Cleaned",
                                fontSize   = 12.sp,
                                color      = GreenPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // ── + Report FAB ──────────────────────────────────
            Button(
                onClick  = { navController.navigate("report") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                shape  = RoundedCornerShape(50)
            ) {
                Text("+ Report", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Filter Button with count badge
// ─────────────────────────────────────────────────────────────
@Composable
fun FilterButton(
    text: String,
    count: Int = -1,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape   = RoundedCornerShape(14.dp),
        color   = if (selected) GreenDark else Color.White,
        border  = BorderStroke(1.dp, if (selected) GreenDark else Color.Gray)
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text,
                color      = if (selected) Color.White else Color.Black,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (selected)
                                Color.White.copy(alpha = 0.25f)
                            else
                                Color(0xFFE8F5E9),
                            shape = CircleShape
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        "$count",
                        fontSize   = 11.sp,
                        color      = if (selected) Color.White else GreenDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}