package com.alcopoune.metertronik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alcopoune.metertronik.presentation.navigation.AppNavHost
import com.alcopoune.metertronik.presentation.screen.auth.manage_password.ResetPasswordScreen
import com.alcopoune.metertronik.presentation.screen.auth.manage_password.SearchIdentifierScreen
import com.alcopoune.metertronik.presentation.screen.auth.register.RegisterScreen
import com.alcopoune.metertronik.presentation.screen.auth.verify.VerifyScreen
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetertronikTheme {
//                AppNavHost()
                ResetPasswordScreen()
            }
        }
    }
}