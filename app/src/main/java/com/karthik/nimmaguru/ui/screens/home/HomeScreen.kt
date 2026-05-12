package com.karthik.nimmaguru.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.karthik.nimmaguru.core.lang.LanguageManager
import com.karthik.nimmaguru.navigation.Routes
import com.karthik.nimmaguru.ui.theme.PrimaryGreen
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val isKn = LanguageManager.isKannada.value

    // 🔒 Auth Guard
    if (user == null) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.Login) {
                popUpTo(Routes.Home) { inclusive = true }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isKn) "ನಮ್ಮ ಗುರು" else "NimmaGuru",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = PrimaryGreen
                    )
                },
                actions = {
                    IconButton(onClick = { LanguageManager.toggleLanguage() }) {
                        Icon(Icons.Rounded.Translate, contentDescription = null, tint = PrimaryGreen)
                    }
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Rounded.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // 👋 DYNAMIC HEADER
            WelcomeHeader(
                userName = user.displayName ?: (if (isKn) "ಬಳಕೆದಾರರು" else "User"),
                isKn = isKn
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = if (isKn) "ನೀವು ಏನು ಮಾಡಲು ಬಯಸುತ್ತೀರಿ?" else "Quick Actions",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📊 NETTY DASHBOARD GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    DashboardCard(
                        title = if (isKn) "ಗುರುಗಳನ್ನು ಹುಡುಕಿ" else "Find Guru",
                        icon = Icons.Rounded.PersonSearch,
                        gradient = Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))),
                        accentColor = Color(0xFF1976D2),
                        onClick = { navController.navigate(Routes.Search) }
                    )
                }

                item {
                    DashboardCard(
                        title = if (isKn) "ನನ್ನ ಪ್ರೊಫೈಲ್" else "My Profile",
                        icon = Icons.Rounded.AccountCircle,
                        gradient = Brush.verticalGradient(listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8))),
                        accentColor = PrimaryGreen,
                        onClick = { navController.navigate(Routes.Profile) }
                    )
                }

                item {
                    DashboardCard(
                        title = if (isKn) "ನೇರ ತರಗತಿಗಳು" else "Live Classes",
                        icon = Icons.Rounded.LiveTv,
                        gradient = Brush.verticalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))),
                        accentColor = Color(0xFFE65100),
                        onClick = { navController.navigate(Routes.Sessions) }
                    )
                }

                item {
                    DashboardCard(
                        title = if (isKn) "ಬೋಧನೆ ಪ್ರಾರಂಭಿಸಿ" else "Teaching",
                        icon = Icons.Rounded.School,
                        gradient = Brush.verticalGradient(listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7))),
                        accentColor = Color(0xFF7B1FA2),
                        onClick = { navController.navigate(Routes.CreateSession) }
                    )
                }

                item(span = { GridItemSpan(2) }) {
                    DashboardCard(
                        title = if (isKn) "ಗೌರವದ ಗೋಡೆ (Wall of Fame)" else "Wall of Fame",
                        icon = Icons.Rounded.EmojiEvents,
                        gradient = Brush.verticalGradient(listOf(Color(0xFFFFF9C4), Color(0xFFFFF176))),
                        accentColor = Color(0xFFFBC02D),
                        isFullWidth = true,
                        onClick = { navController.navigate(Routes.Wall) }
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader(userName: String, isKn: Boolean) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> if (isKn) "ಶುಭೋದಯ" else "Good Morning"
        in 12..16 -> if (isKn) "ಶುಭ ಮಧ್ಯಾಹ್ನ" else "Good Afternoon"
        else -> if (isKn) "ಶುಭ ಸಂಜೆ" else "Good Evening"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGreen.copy(alpha = 0.05f), MaterialTheme.shapes.extraLarge)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = PrimaryGreen
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "$greeting, $userName!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (isKn) "ಇಂದು ಹೊಸದನ್ನು ಕಲಿಯಲು ಸಿದ್ಧರಿದ್ದೀರಾ?" else "Ready to grow your skills today?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    gradient: Brush,
    accentColor: Color,
    isFullWidth: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isFullWidth) 90.dp else 145.dp),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            if (isFullWidth) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(title, fontWeight = FontWeight.ExtraBold, color = accentColor)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(36.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}