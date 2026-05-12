package com.karthik.nimmaguru.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.LanguageManager
import com.karthik.nimmaguru.navigation.Routes
import com.karthik.nimmaguru.ui.components.AppButton
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.AuthUiState
import com.karthik.nimmaguru.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * 100x Login Screen with Password Reset Logic
 * Final version for Nimma-Guru college project.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isKn = LanguageManager.isKannada.value
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Logic for Navigation & Success Messages
    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Success -> {
                navController.navigate(Routes.Home) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
                viewModel.resetState()
            }
            is AuthUiState.Error -> {
                val message = (authState as AuthUiState.Error).message
                // If the "Error" is actually a success message from the Reset function
                if (message.contains("sent", ignoreCase = true) || message.contains("ಕಳುಹಿಸಲಾಗಿದೆ")) {
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 🌿 BRANDING
            Surface(
                color = PrimaryGreen.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.School,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isKn) "ಸ್ವಾಗತ" else "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )

            Text(
                text = if (isKn) "ನಿಮ್ಮ ಕಲಿಕೆಯ ಪ್ರಯಾಣ ಮುಂದುವರಿಸಿ" else "Continue your journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 📧 EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(if (isKn) "ಇಮೇಲ್ ವಿಳಾಸ" else "Email Address") },
                leadingIcon = {
                    Icon(Icons.Rounded.Email, contentDescription = null, modifier = Modifier.size(20.dp), tint = PrimaryGreen)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, focusedLabelColor = PrimaryGreen)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔑 PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if (isKn) "ಪಾಸ್‌ವರ್ಡ್" else "Password") },
                leadingIcon = {
                    Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(20.dp), tint = PrimaryGreen)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        val icon = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility
                        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    viewModel.login(email, password)
                }),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, focusedLabelColor = PrimaryGreen)
            )

            // ⚠️ ERROR/STATUS BOX
            AnimatedVisibility(visible = authState is AuthUiState.Error) {
                val msg = (authState as? AuthUiState.Error)?.message ?: ""
                Text(
                    text = msg,
                    color = if (msg.contains("sent") || msg.contains("ಕಳುಹಿಸಲಾಗಿದೆ")) PrimaryGreen else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🚀 LOGIN BUTTON
            AppButton(
                text = if (isKn) "ಲಾಗಿನ್" else "Login",
                isLoading = authState is AuthUiState.Loading,
                onClick = { viewModel.login(email, password) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🆕 NAVIGATION LINKS
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isKn) "ಖಾತೆ ಇಲ್ಲವೇ?" else "New to Nimma-Guru?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { navController.navigate(Routes.Register) }) {
                        Text(
                            text = if (isKn) "ಈಗಲೇ ನೋಂದಾಯಿಸಿ" else "Register Now",
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // 🔑 FORGOT PASSWORD TRIGGER
                TextButton(
                    onClick = {
                        if (email.isNotEmpty()) {
                            viewModel.resetPassword(email)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (isKn) "ಮೊದಲು ನಿಮ್ಮ ಇಮೇಲ್ ನಮೂದಿಸಿ" else "Enter your email first"
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (isKn) "ಪಾಸ್‌ವರ್ಡ್ ಮರೆತಿರುವಿರಾ?" else "Forgot Password?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}