package com.karthik.nimmaguru.ui.screens.appreciation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.ui.components.AppreciationCard
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.AppreciationViewModel

/**
 * 100x Wall of Fame
 * A community-driven gratitude board for village mentors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallOfFameScreen(
    navController: NavController,
    viewModel: AppreciationViewModel = hiltViewModel()
) {
    // Collect the real-time stream of appreciations
    val appreciations by viewModel.allAppreciations.collectAsState(initial = emptyList())
    val isKn = com.karthik.nimmaguru.core.lang.LanguageManager.isKannada.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.VolunteerActivism, contentDescription = null, tint = PrimaryGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(AppText.wallOfFame, fontWeight = FontWeight.ExtraBold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (appreciations.isEmpty()) {
                EmptyWallView(isKn)
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 100x Header: Explaining the Wall
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = if (isKn) "ನಮ್ಮ ಸಮುದಾಯದ ಕೃತಜ್ಞತೆಗಳು" else "Community Gratitude",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            Text(
                                text = if (isKn) "ನಿಮ್ಮ ಕಲಿಕೆಗೆ ದಾರಿದೀಪವಾದ ಗುರುಗಳಿಗೆ ಧನ್ಯವಾದ ತಿಳಿಸಿ." else "Real stories of impact from our village hub.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(appreciations) { note ->
                        AppreciationCard(appreciation = note)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyWallView(isKn: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = PrimaryGreen.copy(alpha = 0.1f),
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = PrimaryGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (isKn) "ಕೃತಜ್ಞತೆ ಗೋಡೆ ಸಿದ್ಧವಿದೆ!" else "The wall is waiting!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isKn)
                "ಒಂದು ತರಗತಿಯ ನಂತರ ಇಲ್ಲಿ ನಿಮ್ಮ ಗುರುಗಳಿಗೆ ಧನ್ಯವಾದ ತಿಳಿಸಿ."
            else "After attending a session, share your thanks here to inspire others.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
