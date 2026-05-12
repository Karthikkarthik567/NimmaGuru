package com.karthik.nimmaguru.ui.screens.guru

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.ui.components.AppButton
import com.karthik.nimmaguru.ui.components.SkillChip
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.GuruUiState
import com.karthik.nimmaguru.viewmodel.GuruViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GuruProfileScreen(
    navController: NavController,
    guruId: String,
    viewModel: GuruViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isKn = com.karthik.nimmaguru.core.lang.LanguageManager.isKannada.value

    // Form states
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var localError by remember { mutableStateOf<String?>(null) }

    val skillOptions = listOf("Math", "Science", "Carpentry", "Agriculture", "Tailoring", "English")
    val dayOptions = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    // 100x: Initialize data once
    LaunchedEffect(guruId) {
        viewModel.fetchGuruProfile(guruId)
    }

    // Sync UI when data arrives from Firebase
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
                        ReputationBanner(isKn)

                        Spacer(modifier = Modifier.height(24.dp))

                        // 📛 NAME SECTION
                        ProfileSectionHeader(
                            if (isKn) "ನಿಮ್ಮ ಹೆಸರು" else "Your Name",
                            Icons.Rounded.Badge
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; localError = null },
                            placeholder = { Text(if (isKn) "ಹೆಸರನ್ನು ನಮೂದಿಸಿ..." else "Enter your name...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = MaterialTheme.shapes.large,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🧾 BIO SECTION
                        ProfileSectionHeader(
                            if (isKn) "ನಿಮ್ಮ ಬಗ್ಗೆ ತಿಳಿಸಿ" else "About You",
                            Icons.Rounded.HistoryEdu
                        )
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it; localError = null },
                            placeholder = { Text(if (isKn) "ನಿಮ್ಮ ಅನುಭವದ ಬಗ್ಗೆ ಬರೆಯಿರಿ..." else "Tell the village about your experience...") },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🧠 SKILLS SECTION
                        ProfileSectionHeader(
                            if (isKn) "ಕೌಶಲ್ಯಗಳು" else "Skills & Expertise",
                            Icons.Rounded.Psychology
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            skillOptions.forEach { skill ->
                                val isSelected = selectedSkills.contains(skill)
                                SkillChip(
                                    text = skill,
                                    selected = isSelected,
                                    onClick = {
                                        selectedSkills = if (isSelected) selectedSkills - skill else selectedSkills + skill
                                        localError = null
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 📅 AVAILABILITY
                        ProfileSectionHeader(
                            if (isKn) "ಲಭ್ಯತೆ" else "Teaching Availability",
                            Icons.Rounded.EventAvailable
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dayOptions.forEach { day ->
                                val isSelected = selectedDays.contains(day)
                                SkillChip(
                                    text = if (isKn) day.take(3) else day.take(3), // Adjust for local day names if needed
                                    selected = isSelected,
                                    onClick = {
                                        selectedDays = if (isSelected) selectedDays - day else selectedDays + day
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 💾 SAVE ACTION
                        AnimatedVisibility(visible = localError != null || uiState is GuruUiState.Error) {
                            val msg = localError ?: (uiState as? GuruUiState.Error)?.message ?: ""
                            Text(msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 12.dp))
                        }

                        AppButton(
                            text = if (isKn) "ಉಳಿಸಿ" else "Save Profile",
                            isLoading = uiState is GuruUiState.Saving,
                            onClick = {
                                if (bio.length < 10) {
                                    localError = "Please write a slightly longer bio."
                                } else if (selectedSkills.isEmpty()) {
                                    localError = "Please select at least one skill."
                                } else if (name.isBlank()) {
                                    localError = "Please enter your name."
                                } else {
                                    viewModel.saveGuru(name, bio, selectedSkills.toList(), selectedDays.toList())
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Surface(color = PrimaryGreen.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.padding(4.dp).size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ReputationBanner(isKn: Boolean) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = PrimaryGreen)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isKn) "ನಿಮ್ಮ ಪ್ರೊಫೈಲ್ ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ ನಿಮ್ಮನ್ನು ಹುಡುಕಲು ಸಹಾಯ ಮಾಡುತ್ತದೆ." else "Your profile helps students find the right teacher. Keep it updated!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}