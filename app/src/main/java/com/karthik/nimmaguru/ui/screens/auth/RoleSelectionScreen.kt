package com.karthik.nimmaguru.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.core.lang.LanguageManager
import com.karthik.nimmaguru.data.model.UserRole
import com.karthik.nimmaguru.navigation.Routes
import com.karthik.nimmaguru.ui.components.AppButton
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.AuthUiState
import com.karthik.nimmaguru.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isKn = LanguageManager.isKannada.value

    // Form State
    var currentStep by remember { mutableIntStateOf(1) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    // Navigation on Success
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
                title = {
                    Text(
                        text = if (currentStep == 1) (if (isKn) "ಪಾತ್ರವನ್ನು ಆರಿಸಿ" else "Choose Your Role")
                        else (if (isKn) "ನಿಮ್ಮ ವಿವರಗಳು" else "Setup Profile"),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (currentStep > 1) {
                        IconButton(onClick = { currentStep = 1 }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
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

            // 100x Progress Bar
            LinearProgressIndicator(
                progress = { if (currentStep == 1) 0.5f else 1f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                color = PrimaryGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- STEP 1: CHOOSE ROLE ---
            AnimatedVisibility(
                visible = currentStep == 1,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
            ) {
                Column {
                    Text(
                        text = if (isKn) "ನಿಮ್ಮ ಗುರಿ ಏನು?" else "How will you use Nimma-Guru?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (isKn) "ನೀವು ಕಲಿಯಲು ಅಥವಾ ಕಲಿಸಲು ಬಯಸುತ್ತೀರಾ?" else "Choose the role that fits you best.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        RoleToggle(
                            title = if (isKn) "ಕಲಿಯಲು" else "Learn",
                            subtitle = if (isKn) "ವಿದ್ಯಾರ್ಥಿ" else "Student",
                            icon = Icons.Rounded.School,
                            isSelected = selectedRole == UserRole.STUDENT,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = UserRole.STUDENT }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        RoleToggle(
                            title = if (isKn) "ಕಲಿಸಲು" else "Teach",
                            subtitle = if (isKn) "ಗುರು" else "Guru",
                            icon = Icons.Rounded.AutoAwesome,
                            isSelected = selectedRole == UserRole.GURU,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = UserRole.GURU }
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    AppButton(
                        text = if (isKn) "ಮುಂದುವರಿಯಿರಿ" else "Continue",
                        onClick = { currentStep = 2 }
                    )
                }
            }

            // --- STEP 2: USER DETAILS ---
            AnimatedVisibility(
                visible = currentStep == 2,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                Column {
                    Text(
                        text = if (isKn) "ನಿಮ್ಮ ಬಗ್ಗೆ ತಿಳಿಸಿ" else "Tell us about yourself",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    AppTextField(value = name, onValueChange = { name = it }, label = if (isKn) "ಹೆಸರು" else "Full Name", icon = Icons.Rounded.Badge)
                    AppTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Rounded.Email)
                    AppTextField(value = password, onValueChange = { password = it }, label = "Password", icon = Icons.Rounded.Lock, isPassword = true)
                    AppTextField(value = village, onValueChange = { village = it }, label = if (isKn) "ಊರು" else "Your Village", icon = Icons.Rounded.LocationCity)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (authState is AuthUiState.Error) {
                        Text(
                            text = (authState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    AppButton(
                        text = if (isKn) "ಖಾತೆ ತೆರೆಯಿರಿ" else "Complete Registration",
                        isLoading = authState is AuthUiState.Loading,
                        onClick = { viewModel.register(name, email, password, selectedRole, village) }
                    )
                }
            }
        }
    }
}