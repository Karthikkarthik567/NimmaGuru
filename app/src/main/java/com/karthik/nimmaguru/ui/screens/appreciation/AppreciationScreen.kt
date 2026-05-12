package com.karthik.nimmaguru.ui.screens.appreciation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.AppreciationViewModel
import com.karthik.nimmaguru.viewmodel.SendUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppreciationScreen(
    navController: NavController,
    guruId: String,
    guruName: String,
    viewModel: AppreciationViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val sendState by viewModel.sendState.collectAsState()

    LaunchedEffect(sendState) {
        if (sendState is SendUiState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appreciate $guruName", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            Text(
                "Show your gratitude",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("Write a thank you note to $guruName...") },
                enabled = sendState !is SendUiState.Loading
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (sendState is SendUiState.Error) {
                Text(
                    text = (sendState as SendUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    viewModel.sendAppreciation(
                        toGuruId = guruId,
                        guruName = guruName,
                        message = messageText,
                        studentName = "Student" // Should be fetched from Auth/Profile
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = messageText.length >= 5 && sendState !is SendUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                if (sendState is SendUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Gratitude")
                }
            }
        }
    }
}
