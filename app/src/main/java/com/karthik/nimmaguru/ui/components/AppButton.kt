package com.karthik.nimmaguru.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 100x AppButton: Optimized for Elderly Gurus & Rural Students.
 * Features: High-contrast support, Large touch targets, and Animated Loading States.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,           // Added icon support for visual cues
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    isLoading: Boolean = false,
    isSecondary: Boolean = false         // Toggle for outline vs solid
) {
    // 100x Success Metric: Elderly-friendly height (min 56dp)
    val buttonHeight = 58.dp

    if (isSecondary) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(buttonHeight),
            enabled = enabled && !isLoading,
            border = BorderStroke(
                width = 2.dp,
                color = containerColor
            ),
            shape = MaterialTheme.shapes.large // More rounded = more modern & friendly
        ) {
            ButtonContent(text, icon, isLoading, containerColor)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(buttonHeight),
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = Color.LightGray.copy(alpha = 0.4f)
            ),
            shape = MaterialTheme.shapes.large
        ) {
            ButtonContent(text, icon, isLoading, contentColor)
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    isLoading: Boolean,
    color: Color
) {
    // AnimatedContent makes the transition from Text to Loader feel professional
    AnimatedContent(targetState = isLoading, label = "button_state") { loading ->
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = color,
                strokeWidth = 3.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 18.sp, // Slightly larger for better legibility
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}