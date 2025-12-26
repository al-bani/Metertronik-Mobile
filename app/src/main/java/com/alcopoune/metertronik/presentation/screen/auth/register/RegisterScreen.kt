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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.alcopoune.metertronik.R
import com.alcopoune.metertronik.presentation.components.input.PrimaryButton
import com.alcopoune.metertronik.presentation.components.input.PrimaryTextField
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme
import com.alcopoune.metertronik.utils.validator.InputValidator
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen() {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
                    fontWeight = FontWeight.SemiBold
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
                        onValueChange = { username = it },
                        placeholder = "Insert your Username",
                        trailingIcon = {

                        },
                        modifier = Modifier.weight(1f)
                    )

                    PrimaryButton(
                        text = "Check",
                        onClick = { },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        fullWidth = false
                    )
                }

                PrimaryTextField(
                    value = email,
                    onValueChange = { newValue ->
                        email = newValue
                        emailError = InputValidator.getEmailErrorMessage(newValue)
                    },
                    placeholder = "Insert your Email",
                    errorMessage = emailError
                )
                PrimaryTextField(
                    value = password,
                    onValueChange = { newValue ->
                        password = newValue
                        passwordError = InputValidator.getPasswordErrorMessage(newValue)
                    },
                    placeholder = "Insert your Password",
                    isPassword = true,
                    errorMessage = passwordError
                )
                PrimaryTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm your Password",
                    isPassword = true
                )
            }
            Spacer(modifier = Modifier.height(26.dp))

            PrimaryButton(
                text = "Register",
                onClick = { },
                containerColor = MaterialTheme.colorScheme.onSecondary
            )
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