package com.jonathansteele.taskify.screen

import android.content.res.Configuration
import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jonathansteele.taskify.data.repository.FakeAuthRepository
import com.jonathansteele.taskify.data.repository.IAuthRepository
import com.jonathansteele.taskify.ui.theme.TaskifyTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onSwitchToLogin: () -> Unit = {},
    iAuthRepository: IAuthRepository = koinInject<IAuthRepository>(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.filterNot { char -> char.isWhitespace() } },
                label = { Text("Email") },
                singleLine = true,
                isError = email.isNotEmpty() && !isEmailValid(email),
                supportingText = {
                    if (email.isNotEmpty() && !isEmailValid(email)) {
                        Text("Please enter a valid email address")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            val minPasswordLength = 6
            val isPasswordValid = isPasswordValid(password, minPasswordLength)
            val passwordError = password.isNotEmpty() && !isPasswordValid

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                isError = passwordError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val desc = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = desc)
                    }
                },
                supportingText = {
                    if (passwordError) {
                        Text("Password must be at least $minPasswordLength characters")
                    } else {
                        null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            val isFormValid =
                email.isNotBlank() &&
                    isEmailValid(email) &&
                    password.isNotBlank() &&
                    isPasswordValid
            Button(
                onClick = {
                    scope.launch {
                        val result = iAuthRepository.signUp(email, password)
                        if (result.isSuccess) {
                            onRegisterSuccess()
                        } else {
                            snackBarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Registration failed")
                        }
                    }
                },
                enabled = isFormValid,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(4.dp),
            ) {
                Text("Register", style = MaterialTheme.typography.labelLarge)
            }

            Text(
                text = "By signing up, you agree to our Terms and Privacy Policy.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSwitchToLogin) {
                Text(
                    text = "Already have an account? Log in",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

fun isEmailValid(email: String) = EMAIL_ADDRESS.matcher(email).matches()

fun isPasswordValid(
    password: String,
    minLength: Int = 6,
) = password.length >= minLength

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TaskifyTheme {
        RegisterScreen(iAuthRepository = FakeAuthRepository)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RegisterScreenDarkPreview() {
    TaskifyTheme {
        RegisterScreen(iAuthRepository = FakeAuthRepository)
    }
}
