package com.karthik.nimmaguru.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karthik.nimmaguru.data.model.Appreciation

/**
 * 100x Gratitude Card
 * Features: High-impact featured states, Dynamic backgrounds, and Kannada-ready time.
 */
@Composable
fun AppreciationCard(
    appreciation: Appreciation,
    isKannada: Boolean = false,
    modifier: Modifier = Modifier
) {
    // 100x Logic: Featured cards get a slightly different border/glow to stand out
    val cardColor = try {
        Color(android.graphics.Color.parseColor(appreciation.highlightColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = if (appreciation.isFeatured) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(0.dp),
        border = if (appreciation.isFeatured) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Decorative Quote
            Icon(
                imageVector = Icons.Rounded.FormatQuote,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .alpha(0.07f)
                    .offset(x = 10.dp, y = 10.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // Header with Badge for High Impact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StudentAvatar(appreciation.fromUserName)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = appreciation.fromUserName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = appreciation.getRelativeTime(isKannada),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        }
                    }

                    if (appreciation.isFeatured) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Featured",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Message Block
                Text(
                    text = appreciation.message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp,
                        fontStyle = FontStyle.Normal
                    ),
                    fontWeight = if (appreciation.isFeatured) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Footer
                Divider(modifier = Modifier.alpha(0.1f))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isKannada) "${appreciation.toGuruName} ಅವರಿಗೆ" else "To: ${appreciation.toGuruName}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StudentAvatar(name: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
    }
}