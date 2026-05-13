package com.example.paryavarankavalu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.paryavarankavalu.data.KarmaStore
import com.example.paryavarankavalu.ui.navigation.AppNavigation
import com.example.paryavarankavalu.ui.theme.ParyavaranKavaluTheme

class MainActivity : ComponentActivity() {

    // ── Request permission BEFORE setContent ──────────────────
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permission result handled — app already running, no crash
        // HomeScreen's LaunchedEffect will pick up the result cleanly
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Init karma store (restores saved points)
        KarmaStore.init(this)

        // ✅ Ask permissions FIRST before loading any screen
        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted || !coarseGranted) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        setContent {
            ParyavaranKavaluTheme {
                AppNavigation()
            }
        }
    }
}


