package com.karthik.nimmaguru.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karthik.nimmaguru.data.model.Session
import com.karthik.nimmaguru.data.model.SessionStatus

/**
 * 100x Session Card
 * Features: Real-time seat tracking, Live badges, and high-contrast scheduling.
 */
@Composable
fun SessionCard(
    session: Session,
    isKannada: Boolean = false,
    onBookClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val isFull = session.isFull
    val isLive = session.status == SessionStatus.ONGOING // Or your isCurrentlyLive logic

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = if (isLive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // --- HEADER: Title & Live Badge ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "by ${session.guruName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (isLive) {
                    Badge(containerColor = Color.Red) {
                        Text("LIVE", color = Color.White, modifier = Modifier.padding(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- INFO ROW: Location & Time ---
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoItem(Icons.Rounded.LocationOn, session.location, Modifier.weight(1f))
                InfoItem(Icons.Rounded.Schedule, session.getFormattedDate(), Modifier.weight(1.2f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAPACITY & BOOKING ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Seat Indicator
                Column {
                    Text(
                        text = if (isFull) (if (isKannada) "ಸೀಟುಗಳು ಭರ್ತಿಯಾಗಿವೆ" else "Full House")
                        else (if (isKannada) "${session.seatsLeft} ಸೀಟುಗಳು ಬಾಕಿ" else "${session.seatsLeft} seats left"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isFull) Color.Red else Color(0xFF4CAF50)
                    )
                    // Visual progress bar for capacity
                    LinearProgressIndicator(
                        progress = session.currentStudentsCount.toFloat() / session.maxStudents.toFloat(),
                        modifier = Modifier.width(100.dp).padding(top = 4.dp),
                        color = if (isFull) Color.Red else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                if (session.canJoin) {
                    AppButton(
                        text = if (isKannada) "ನೋಂದಾಯಿಸಿ" else "Join Class",
                        onClick = onBookClick,
                        modifier = Modifier.width(140.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
    }
}