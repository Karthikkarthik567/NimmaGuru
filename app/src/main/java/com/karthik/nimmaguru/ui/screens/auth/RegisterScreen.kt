package com.karthik.nimmaguru.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.data.model.UserRole
import com.karthik.nimmaguru.navigation.Routes
import com.karthik.nimmaguru.ui.components.AppButton
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.AuthUiState
import com.karthik.nimmaguru.viewmodel.AuthViewModel

/**
 * 100x Register Screen
 * Handles onboarding for Gurus and Students with localized support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isKn = com.karthik.nimmaguru.core.lang.LanguageManager.isKannada.value

    // Form state
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Navigation logic: Reacting to the ViewModel state
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            navController.navigate(Routes.Home) {
                popUpTo(Routes.Register) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isKn) "ನಮ್ಮ ಸಮುದಾಯಕ್ಕೆ ಸೇರಿ" else "Join the Community", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- ROLE SELECTION ---
            Text(
                text = if (isKn) "ನೀವು ಯಾರು?" else "I am joining as a...",
                modifier = Modifier.align(Alignment.Start),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                RoleToggle(
                    title = if (isKn) "ವಿದ್ಯಾರ್ಥಿ" else "Student",
                    icon = Icons.Rounded.School,
                    isSelected = selectedRole == UserRole.STUDENT,
                    onClick = { selectedRole = UserRole.STUDENT },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                RoleToggle(
                    title = if (isKn) "ಗುರು" else "Guru",
                    icon = Icons.Rounded.WorkspacePremium,
                    isSelected = selectedRole == UserRole.GURU,
                    onClick = { selectedRole = UserRole.GURU },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT FIELDS ---
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = if (isKn) "ಪೂರ್ಣ ಹೆಸರು" else "Full Name",
                icon = Icons.Rounded.Person,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = if (isKn) "ಇಮೇಲ್ ವಿಳಾಸ" else "Email Address",
                icon = Icons.Rounded.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = if (isKn) "ಪಾಸ್‌ವರ್ಡ್" else "Password (min 6 chars)",
                icon = Icons.Rounded.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
            )

            AppTextField(
                value = village,
                onValueChange = { village = it },
                label = if (isKn) "ನಿಮ್ಮ ಊರು" else "Your Village/Town",
                icon = Icons.Rounded.LocationOn,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            // --- ERROR FEEDBACK ---
            AnimatedVisibility(visible = authState is AuthUiState.Error) {
                val errorMessage = (authState as? AuthUiState.Error)?.message ?: ""
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- ACTION BUTTON ---
            AppButton(
                text = if (isKn) "ಖಾತೆ ತೆರೆಯಿರಿ" else "Create My Account",
                isLoading = authState is AuthUiState.Loading,
                onClick = {
                    viewModel.register(name, email, password, selectedRole, village)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RoleToggle(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isSelected) PrimaryGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) PrimaryGreen.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = PrimaryGreen) },
        trailingIcon = {
            if (isPassword && onPasswordToggle != null) {
                IconButton(onClick = onPasswordToggle) {
                    val visibilityIcon = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility
                    Icon(visibilityIcon, contentDescription = null)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.large,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            focusedLabelColor = PrimaryGreen,
            cursorColor = PrimaryGreen
        )
    )
}