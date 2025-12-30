package com.alcopoune.metertronik.presentation.screen.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alcopoune.metertronik.R
import com.alcopoune.metertronik.presentation.components.input.PrimaryButton
import com.alcopoune.metertronik.presentation.components.input.PrimaryTextField
import com.alcopoune.metertronik.presentation.navigation.Routes
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme
import com.alcopoune.metertronik.utils.validator.InputValidator
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val checkIdState by viewModel.checkIdState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val usernameErrorMessage: String? = when {
        formError != null -> formError
        checkIdState is CheckIdState.Error -> (checkIdState as CheckIdState.Error).message
        checkIdState is CheckIdState.Result && !(checkIdState as CheckIdState.Result).available -> "Username sudah digunakan"
        else -> null
    }

    LaunchedEffect(uiState) {
        if (uiState is RegisterState.Success) {
            val result = (uiState as RegisterState.Success).result
            if (!result.status) {
                // Kirim credential ke Verify via SavedStateHandle (tanpa taruh password di route)
                navController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("verify_email", email)
         
                    set("verify_source", "register")
                    set("verify_auto_resend", false)
                }
                navController.navigate(Routes.Verify.route)
                // Penting: consume event success supaya saat user back dari Verify
                // RegisterScreen tidak auto-navigate ke Verify lagi.
                viewModel.resetState()
            } else {
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Register.route) { inclusive = true }
                }
                viewModel.resetState()
            }
        }
    }

    // Ketika back dari verify: email & username tetap, password harus kosong
    LaunchedEffect(navBackStackEntry) {
        val shouldClearPassword = navBackStackEntry?.savedStateHandle?.get<Boolean>("clear_password") == true
        if (shouldClearPassword) {
            password = ""
            confirmPassword = ""
            navBackStackEntry?.savedStateHandle?.set("clear_password", false)
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
                    text = "Already have an Account?",
                    color = MaterialTheme.colorScheme.scrim,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Login Here",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Register.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                    text = "Join with us",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.scrim ,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "And start your Journey !",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                    fontWeight = FontWeight.Bold
                )

            Spacer(modifier = Modifier.height(42.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrimaryTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            viewModel.resetCheckIdState()
                            formError = null
                        },
                        placeholder = "Insert your Username",
                        errorMessage = usernameErrorMessage,
                        trailingIcon = {
                            when (checkIdState) {
                                is CheckIdState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.width(18.dp).height(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                }

                                is CheckIdState.Result -> {
                                    val result = checkIdState as CheckIdState.Result
                                    Icon(
                                        imageVector = if (result.available) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (result.available) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                                    )
                                }

                                is CheckIdState.Error -> {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }

                                else -> Unit
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState !is RegisterState.Loading
                    )

                    PrimaryButton(
                        text = "Check",
                        onClick = { viewModel.checkId(username) },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        fullWidth = false,
                        enabled = uiState !is RegisterState.Loading && checkIdState !is CheckIdState.Loading
                    )
                }

                if (checkIdState is CheckIdState.Result && (checkIdState as CheckIdState.Result).available) {
                    Text(
                        text = (checkIdState as CheckIdState.Result).message,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    )
                }

                PrimaryTextField(
                    value = email,
                    onValueChange = { newValue ->
                        email = newValue
                        emailError = InputValidator.getEmailErrorMessage(newValue)
                    },
                    placeholder = "Insert your Email",
                    errorMessage = emailError,
                    enabled = uiState !is RegisterState.Loading
                )
                PrimaryTextField(
                    value = password,
                    onValueChange = { newValue ->
                        password = newValue
                        passwordError = InputValidator.getPasswordErrorMessage(newValue)
                    },
                    placeholder = "Insert your Password",
                    isPassword = true,
                    errorMessage = passwordError,
                    enabled = uiState !is RegisterState.Loading
                )
                PrimaryTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm your Password",
                    isPassword = true,
                    enabled = uiState !is RegisterState.Loading
                )
            }
            Spacer(modifier = Modifier.height(26.dp))

            // Error dari API register (bukan error per-field)
            if (uiState is RegisterState.Error) {
                Text(
                    text = (uiState as RegisterState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState is RegisterState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(24.dp).height(24.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            } else {
                PrimaryButton(
                    text = "Register",
                    onClick = {
                        formError = null
                        val idAvailable = (checkIdState as? CheckIdState.Result)?.available
                        if (idAvailable == false) {
                            formError = "Username sudah digunakan"
                            return@PrimaryButton
                        }
                        if (idAvailable == null) {
                            formError = "Silakan cek username dulu"
                            return@PrimaryButton
                        }
                        if (emailError != null || passwordError != null) {
                            formError = "Periksa kembali email/password kamu"
                            return@PrimaryButton
                        }
                        viewModel.register(
                            email = email,
                            username = username,
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            }
            Spacer(modifier = Modifier.height(26.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
               horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
}

//@Preview
//@Composable
//private fun PreviewScreenRegister() {
//    MetertronikTheme {
//        RegisterScreen()
//    }
//}