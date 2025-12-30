package com.alcopoune.metertronik.presentation.screen.auth.verify

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alcopoune.metertronik.presentation.components.input.KeypadNumeric
import com.alcopoune.metertronik.presentation.components.input.PrimaryButton
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    email: String,
    autoResendOtp: Boolean,
    onBack: () -> Unit,
    onNavigateLogin: () -> Unit,
    viewModel: VerifyViewModel = hiltViewModel()
) {
    var otpCode by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()

    LaunchedEffect(email, autoResendOtp) {
        viewModel.triggerInitialOtpIfNeeded(email = email, enabled = autoResendOtp)
    }

    LaunchedEffect(otpCode) {
        if (otpCode.length == 6) {
            viewModel.verifyOtp(email = email, otp = otpCode)
        } else {
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is VerifyState.Success) {
            onNavigateLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.scrim
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Verification",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.scrim,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We Just sent you OTP to\n${maskEmail(email)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (i in 0 until 6) {
                            OtpDigitBox(
                                digit = otpCode.getOrNull(i)?.toString() ?: "",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState is VerifyState.Error) {
                        Text(
                            text = (uiState as VerifyState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (remainingSeconds > 0) {
                            Text(
                                text = "Resend Code in",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.scrim.copy(0.8f),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = formatSeconds(remainingSeconds),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        } else {
                            PrimaryButton(
                                text = if (uiState is VerifyState.Resending) "Sending..." else "Resend OTP",
                                onClick = { viewModel.resendOtp(email) },
                                containerColor = MaterialTheme.colorScheme.onSecondary,
                                fullWidth = false,
                                enabled = uiState !is VerifyState.Resending && uiState !is VerifyState.Verifying
                            )
                        }
                    }
                }
            }
            
            // Keypad
            KeypadNumeric(
                onNumberClick = { number ->
                    if (uiState is VerifyState.Verifying) return@KeypadNumeric
                    if (otpCode.length < 6) {
                        otpCode += number.toString()
                    }
                },
                onDeleteClick = {
                    if (uiState is VerifyState.Verifying) return@KeypadNumeric
                    if (otpCode.isNotEmpty()) {
                        otpCode = otpCode.dropLast(1)
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

private fun formatSeconds(totalSeconds: Int): String {
    val seconds = max(0, totalSeconds)
    val minutesPart = seconds / 60
    val secondsPart = seconds % 60
    return "%d:%02d".format(minutesPart, secondsPart)
}

private fun maskEmail(email: String): String {
    val at = email.indexOf('@')
    if (at <= 1) return email
    val name = email.substring(0, at)
    val domain = email.substring(at + 1)
    val maskedName = name.first() + "*".repeat(max(1, name.length - 2)) + name.last()
    val maskedDomain = if (domain.length <= 2) domain else domain.first() + "*".repeat(domain.length - 2) + domain.last()
    return "$maskedName@$maskedDomain"
}

@Composable
private fun OtpDigitBox(
    digit: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.scrim.copy(0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.scrim,
            textAlign = TextAlign.Center
        )
    }
}