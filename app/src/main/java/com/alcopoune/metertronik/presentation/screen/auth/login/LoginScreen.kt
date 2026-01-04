package com.alcopoune.metertronik.presentation.screen.auth.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.alcopoune.metertronik.R
import com.alcopoune.metertronik.presentation.components.input.PrimaryButton
import com.alcopoune.metertronik.presentation.components.input.PrimaryTextField
import com.alcopoune.metertronik.presentation.navigation.Routes
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var identifier by remember { mutableStateOf("") } // username or email
    var password by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // Prefill email setelah verify sukses (bukan data sensitif)
        val prefill = navController.currentBackStackEntry?.savedStateHandle?.get<String>("prefill_email")
        if (!prefill.isNullOrBlank()) {
            identifier = prefill
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("prefill_email")
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is LoginState.Success) {
            val result = (uiState as LoginState.Success).result
            when {
                !result.user.verified -> {
                    // User belum verified -> masuk ke Verify, kirim email dari userDto (domain model)
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("verify_email", result.user.email)
                        set("verify_source", "login")
                        set("verify_auto_resend", true)
                    }
                    navController.navigate(Routes.Verify.route)
                    // consume event biar gak loop navigate
                    viewModel.resetState()
                }

                result.userPaired -> {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                    viewModel.resetState()
                }

                else -> {
                    navController.navigate(Routes.Pairing.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                    viewModel.resetState()
                }
            }
        }
    }
    
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 56.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an Account?",
                    color = MaterialTheme.colorScheme.scrim,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Register Here",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = {
                        navController.navigate(Routes.Register.route)
                    })
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.scrim ,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Good to See you, Again ! ",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(46.dp))
            PrimaryTextField(
                value = identifier,
                onValueChange = { identifier = it },
                placeholder = "Insert your Username or Email",
                enabled = uiState !is LoginState.Loading
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Insert your Password",
                isPassword = true,
                enabled = uiState !is LoginState.Loading
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Error message
            if (uiState is LoginState.Error) {
                Text(
                    text = (uiState as LoginState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.scrim.copy(0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            if (uiState is LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(24.dp).height(24.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            } else {
                PrimaryButton(
                    text = "Login",
                    onClick = { 
                        // Determine if identifier is email or username
                        val isEmail = identifier.contains("@")
                        viewModel.login(
                            email = if (isEmail) identifier else "",
                            username = if (!isEmail) identifier else "",
                            password = password
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            }
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = "Continue With",
                color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.clickable {  },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.scrim.copy(0.4f)
                ),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_google),
                        contentDescription = null,
                        modifier = Modifier.width(28.dp)
                    )
                }
            }
        }
    }
}
