package com.karthik.nimmaguru.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.navigation.Routes
import com.karthik.nimmaguru.ui.components.GuruCard
import com.karthik.nimmaguru.ui.components.SkillChip
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.SearchUiState
import com.karthik.nimmaguru.viewmodel.SearchViewModel

/**
 * 100x Search & Discovery Screen
 * Optimized for performance, localization, and type-safe navigation.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    // Collecting state from ViewModel (Single Source of Truth)
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSkill by viewModel.selectedSkill.collectAsState()

    val skills = listOf("Math", "Science", "Carpentry", "Agriculture", "Tailoring", "English")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppText.findGuru, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // 🔍 SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text(AppText.searchPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = PrimaryGreen)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🏷️ SKILL FILTERS
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.forEach { skill ->
                    SkillChip(
                        text = skill,
                        selected = selectedSkill == skill,
                        onClick = { viewModel.toggleSkillFilter(skill) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📜 DYNAMIC RESULTS
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is SearchUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PrimaryGreen
                        )
                    }
                    is SearchUiState.Empty -> {
                        EmptySearchResults()
                    }
                    is SearchUiState.Error -> {
                        ErrorMessage(state.message)
                    }
                    is SearchUiState.Success -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            item {
                                Text(
                                    text = "${state.gurus.size} Gurus found nearby",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(state.gurus, key = { it.userId }) { guru ->
                                GuruCard(
                                    guru = guru,
                                    onClick = {
                                        // Type-safe navigation to Guru Profile
                                        navController.navigate(Routes.GuruProfile(guru.userId))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchResults() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Gurus found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Try a different skill or village name.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}