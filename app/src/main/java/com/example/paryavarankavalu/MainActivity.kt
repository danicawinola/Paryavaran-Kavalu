package com.example.paryavarankavalu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.paryavarankavalu.data.KarmaStore
import com.example.paryavarankavalu.ui.navigation.AppNavigation
import com.example.paryavarankavalu.ui.theme.ParyavaranKavaluTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        KarmaStore.init(this)
        setContent {
            AppNavigation()        }
    }
}



