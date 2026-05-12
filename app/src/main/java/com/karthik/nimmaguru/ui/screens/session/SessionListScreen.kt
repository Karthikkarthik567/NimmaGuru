package com.karthik.nimmaguru.ui.screens.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.navigation.Routes
import com.karthik.nimmaguru.ui.components.SessionCard
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.SessionViewModel

/**
 * 100x Session Hub
 * Handles the display of upcoming and historical community workshops.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionListScreen(
    navController: NavController,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val allSessions by viewModel.sessions.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    // 100x: These labels will switch based on LanguageManager
    val tabs = listOf(
        if (com.karthik.nimmaguru.core.lang.LanguageManager.isKannada.value) "ಮುಂಬರುವ" else "Upcoming",
        if (com.karthik.nimmaguru.core.lang.LanguageManager.isKannada.value) "ಹಿಂದಿನ" else "Past"
    )

    // Derived state for performance: Filter only when allSessions or selectedTab changes
    val filteredSessions = remember(allSessions, selectedTab) {
        val currentTime = System.currentTimeMillis()
        if (selectedTab == 0) {
            allSessions.filter { it.dateTime.toDate().time >= currentTime }
                .sortedBy { it.dateTime }
        } else {
            allSessions.filter { it.dateTime.toDate().time < currentTime }
                .sortedByDescending { it.dateTime }
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(AppText.sessions, fontWeight = FontWeight.ExtraBold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryGreen,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = PrimaryGreen
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // 100x: In a real app, check user.role == "GURU"
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Routes.CreateSession) },
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text(AppText.createSession) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (filteredSessions.isEmpty()) {
                EmptySessionState(isUpcoming = selectedTab == 0)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSessions, key = { it.sessionId }) { session ->
                        SessionCard(
                            session = session,
                            onClick = {
                                // Navigate to Details or Guru Profile
                                navController.navigate(Routes.GuruProfile(session.guruId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySessionState(isUpcoming: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isUpcoming) "No classes scheduled yet" else "No past history found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = if (isUpcoming)
                "Be the first to share knowledge or check back later!"
            else "Once you attend a session, it will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}