package com.alcopoune.metertronik.presentation.screen.auth.manage_password

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alcopoune.metertronik.presentation.components.input.PrimaryButton
import com.alcopoune.metertronik.presentation.components.input.PrimaryTextField
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchIdentifierScreen(
) {
    var identifier by remember { mutableStateOf("") }
    var requestSent by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.scrim
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            if (!requestSent) {
                // Initial state: Input form
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.scrim,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter your username or email to reset your password",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                    )
                }

                Spacer(modifier = Modifier.height(46.dp))

                PrimaryTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    placeholder = "Insert your Username or Email"
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "Submit",
                    onClick = { requestSent = true },
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    enabled = identifier.isNotBlank()
                )
            } else {
              SuccessSentRequest()
            }
        }
    }
}

@Composable
fun SuccessSentRequest() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Request has been sent",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.scrim,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Check your email",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.scrim.copy(0.7f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun PreviewSent() {
    MetertronikTheme {
        SuccessSentRequest()
    }
}

@Preview
@Composable
private fun PreviewReq() {
    MetertronikTheme {
        SearchIdentifierScreen()
    }
}