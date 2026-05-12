package com.karthik.nimmaguru.ui.screens.session

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.ui.components.AppButton
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import com.karthik.nimmaguru.viewmodel.CreateSessionUiState
import com.karthik.nimmaguru.viewmodel.SessionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionScreen(
    navController: NavController,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val createState by viewModel.createState.collectAsState()

    // Form State
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var maxStudents by remember { mutableStateOf("20") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    val timePickerState = rememberTimePickerState(is24Hour = false)
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var hasSelectedTime by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val isLoading = createState is CreateSessionUiState.Loading

    // Handle Success/Error States from ViewModel
    LaunchedEffect(createState) {
        when (createState) {
            is CreateSessionUiState.Success -> {
                navController.popBackStack()
                viewModel.resetCreateState()
            }
            is CreateSessionUiState.Error -> {
                localError = (createState as CreateSessionUiState.Error).message
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(AppText.createSession, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Details of the class",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT FIELDS ---
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Workshop Title") },
                placeholder = { Text("e.g. Traditional Weaving Basics") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Rounded.Class, contentDescription = null, tint = PrimaryGreen) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Short Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("e.g. Mandya Library Hall") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = PrimaryGreen) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = maxStudents,
                onValueChange = { if (it.all { c -> c.isDigit() }) maxStudents = it },
                label = { Text("Student Limit") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Rounded.Group, contentDescription = null, tint = PrimaryGreen) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- DATE & TIME SLOTS ---
            Row(modifier = Modifier.fillMaxWidth()) {
                val formattedDate = selectedDateMillis?.let {
                    SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(it))
                }
                SchedulePickerBox(
                    label = "Select Date",
                    value = formattedDate,
                    icon = Icons.Rounded.CalendarToday,
                    modifier = Modifier.weight(1f),
                    onClick = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.width(12.dp))

                val formattedTime = if (hasSelectedTime) {
                    val hour = if (timePickerState.hour % 12 == 0) 12 else timePickerState.hour % 12
                    val amPm = if (timePickerState.hour < 12) "AM" else "PM"
                    String.format("%02d:%02d %s", hour, timePickerState.minute, amPm)
                } else null

                SchedulePickerBox(
                    label = "Select Time",
                    value = formattedTime,
                    icon = Icons.Rounded.AccessTime,
                    modifier = Modifier.weight(1f),
                    onClick = { showTimePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SUBMIT ACTION ---
            AnimatedVisibility(visible = localError != null) {
                Text(
                    text = localError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AppButton(
                text = "Publish Gyaan-Daan",
                isLoading = isLoading,
                onClick = {
                    if (title.isBlank() || location.isBlank() || selectedDateMillis == null || !hasSelectedTime) {
                        localError = "Please provide all details."
                        return@AppButton
                    }

                    val sessionCalendar = Calendar.getInstance().apply {
                        timeInMillis = selectedDateMillis!!
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }

                    viewModel.createSession(
                        title = title,
                        description = description,
                        location = location,
                        maxStudents = maxStudents.toIntOrNull() ?: 20,
                        dateTimeMillis = sessionCalendar.timeInMillis
                    )
                }
            )
        }
    }

    // --- DATE PICKER MODAL ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- TIME PICKER MODAL ---
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    hasSelectedTime = true
                    showTimePicker = false
                }) { Text("OK") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun SchedulePickerBox(
    label: String,
    value: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = PrimaryGreen)
                Text(
                    text = value ?: "Select",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}