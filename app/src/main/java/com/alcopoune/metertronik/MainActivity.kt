package com.alcopoune.metertronik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alcopoune.metertronik.presentation.MainScaffold
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetertronikTheme {
                MainScaffold()
            }
        }
    }
}