package com.karthik.nimmaguru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karthik.nimmaguru.navigation.NavGraph
import com.karthik.nimmaguru.ui.theme.NimmaGuruTheme
import dagger.hilt.android.AndroidEntryPoint // Add this import

@AndroidEntryPoint // <--- ADD THIS LINE
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NimmaGuruTheme {
                NavGraph()
            }
        }
    }
}