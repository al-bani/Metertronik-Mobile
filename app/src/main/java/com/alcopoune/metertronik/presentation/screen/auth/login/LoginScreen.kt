package com.alcopoune.metertronik.presentation.screen.auth.login

import android.R.attr.content
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.alcopoune.metertronik.R
import com.alcopoune.metertronik.presentation.components.input.PrimaryButton
import com.alcopoune.metertronik.presentation.components.input.PrimaryTextField
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(32.dp),
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
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                value = username,
                onValueChange = { username = it },
                placeholder = "Insert your Username or Email"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Insert your Password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(8.dp))
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
            PrimaryButton(
                text = "Login",
                onClick = { },
                containerColor = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = "Or Login With",
                color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(26.dp))

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

@Preview
@Composable
private fun PreviewWelcome() {
    MetertronikTheme{
        LoginScreen()
    }
}