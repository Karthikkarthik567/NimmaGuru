package com.karthik.nimmaguru.ui.screens.guru

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.core.lang.LanguageManager
import com.karthik.nimmaguru.ui.components.AppButton
import com.karthik.nimmaguru.ui.components.SkillChip
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.GuruUiState
import com.karthik.nimmaguru.viewmodel.GuruViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditGuruProfileScreen(
    navController: NavController,
    viewModel: GuruViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isKn = LanguageManager.isKannada.value

    // Local form state
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var isEditing by remember { mutableStateOf(false) }

    val skillOptions = listOf("Math", "Science", "Carpentry", "Agriculture", "Tailoring", "English")
    val dayOptions = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    // Sync local state when data loads
    LaunchedEffect(uiState) {
        if (uiState is GuruUiState.Success) {
            val guru = (uiState as GuruUiState.Success).guru
            name = guru.name
            bio = guru.bio
            selectedSkills = guru.skills.toSet()
            selectedDays = guru.availableDays.toSet()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(AppText.profile, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { isEditing = !isEditing }) {
                        Text(
                            text = if (isEditing) (if (isKn) "ರದ್ದುಮಾಡಿ" else "Cancel")
                            else (if (isKn) "ತಿದ್ದುಪಡಿ" else "Edit"),
                            color = if (isEditing) MaterialTheme.colorScheme.error else PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (uiState) {
                is GuruUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryGreen)
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 👤 IDENTITY CARD
                        ProfileStatusCard(isEditing = isEditing, isKn = isKn)

                        Spacer(modifier = Modifier.height(24.dp))

                        // 📛 NAME SECTION
                        SectionHeader(
                            title = if (isKn) "ಹೆಸರು" else "Full Name",
                            icon = Icons.Rounded.Badge
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text(if (isKn) "ನಿಮ್ಮ ಹೆಸರನ್ನು ನಮೂದಿಸಿ..." else "Enter your name...") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            singleLine = true,
                            shape = MaterialTheme.shapes.large,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGreen,
                                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🧾 BIO SECTION
                        SectionHeader(
                            title = if (isKn) "ನನ್ನ ಬಗ್ಗೆ" else "About Me",
                            icon = Icons.Rounded.Description
                        )
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            placeholder = { Text(if (isKn) "ನಿಮ್ಮ ಅನುಭವವನ್ನು ಹಂಚಿಕೊಳ್ಳಿ..." else "Share your expertise...") },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                            enabled = isEditing,
                            shape = MaterialTheme.shapes.large,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGreen,
                                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🧠 EXPERTISE SECTION
                        SectionHeader(
                            title = if (isKn) "ನನ್ನ ಕೌಶಲ್ಯಗಳು" else "My Expertise",
                            icon = Icons.Rounded.Psychology
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            skillOptions.forEach { skill ->
                                SkillChip(
                                    text = skill,
                                    selected = selectedSkills.contains(skill),
                                    onClick = {
                                        if (isEditing) {
                                            selectedSkills = if (selectedSkills.contains(skill))
                                                selectedSkills - skill else selectedSkills + skill
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 📅 AVAILABILITY
                        SectionHeader(
                            title = if (isKn) "ಲಭ್ಯವಿರುವ ದಿನಗಳು" else "Available Days",
                            icon = Icons.Rounded.EventAvailable
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dayOptions.forEach { day ->
                                SkillChip(
                                    text = day.take(3),
                                    selected = selectedDays.contains(day),
                                    onClick = {
                                        if (isEditing) {
                                            selectedDays = if (selectedDays.contains(day))
                                                selectedDays - day else selectedDays + day
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // 💾 SAVE BUTTON
                        AnimatedVisibility(
                            visible = isEditing,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            AppButton(
                                text = if (isKn) "ಬದಲಾವಣೆಗಳನ್ನು ಉಳಿಸಿ" else "Save Changes",
                                isLoading = uiState is GuruUiState.Saving,
                                onClick = {
                                    viewModel.saveGuru(name, bio, selectedSkills.toList(), selectedDays.toList())
                                    isEditing = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProfileStatusCard(isEditing: Boolean, isKn: Boolean) {
    val bgColor = if (isEditing) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else PrimaryGreen.copy(alpha = 0.05f)

    Surface(
        color = bgColor,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (isEditing) PrimaryGreen else Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Rounded.EditNote else Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (isEditing) Color.White else PrimaryGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (isEditing) (if (isKn) "ಪ್ರೊಫೈಲ್ ತಿದ್ದುಪಡಿ" else "Editing Profile")
                    else (if (isKn) "ನಿಮ್ಮ ಗುರು ಗುರುತು" else "Your Guru Identity"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (isEditing) (if (isKn) "ವಿವರಗಳನ್ನು ಬದಲಾಯಿಸಿ ಮತ್ತು ಉಳಿಸಿ" else "Modify details and tap save")
                    else (if (isKn) "ಸಮುದಾಯಕ್ಕೆ ನಿಮ್ಮ ವಿವರಗಳು ಲಭ್ಯವಿದೆ" else "Visible to the community"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}