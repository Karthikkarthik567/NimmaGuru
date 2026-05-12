package com.karthik.nimmaguru.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.karthik.nimmaguru.core.lang.AppText
import com.karthik.nimmaguru.core.lang.LanguageManager
import com.karthik.nimmaguru.navigation.Routes
import com.karthik.nimmaguru.ui.components.AppButton
import com.karthik.nimmaguru.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // 100x: Ideally, collect these from a UserViewModel
    val name by remember { mutableStateOf("Karthik N.") }
    val role by remember { mutableStateOf("GURU") }
    val village by remember { mutableStateOf("Mandya") }
    val isKn by LanguageManager.isKannada

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(AppText.profile, fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 🎨 AVATAR & IDENTITY
            ProfileAvatar(name = name, role = role)

            Spacer(modifier = Modifier.height(32.dp))

            // 📋 USER DETAILS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileDetailItem(
                        label = if (isKn) "ಹೆಸರು" else "Name",
                        value = name,
                        icon = Icons.Rounded.Badge
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)
                    ProfileDetailItem(
                        label = if (isKn) "ಪಾತ್ರ" else "Role",
                        value = role,
                        icon = Icons.Rounded.VerifiedUser
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)
                    ProfileDetailItem(
                        label = if (isKn) "ಸ್ಥಳ" else "Location",
                        value = village,
                        icon = Icons.Rounded.LocationOn
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🌍 ACCESSIBILITY (Language Toggle)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                onClick = { LanguageManager.toggleLanguage() },
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Translate, contentDescription = null, tint = PrimaryGreen)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (isKn) "ಭಾಷೆಯನ್ನು ಬದಲಿಸಿ" else "Change Language",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (isKn) "ಪ್ರಸ್ತುತ: ಕನ್ನಡ" else "Current: English",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = isKn,
                        onCheckedChange = { LanguageManager.toggleLanguage() },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🚀 AUTH ACTIONS
            AppButton(
                text = if (isKn) "ಮಾಹಿತಿ ತಿದ್ದುಪಡಿ" else "Edit Profile",
                onClick = { navController.navigate(Routes.EditProfile) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.Login) {
                        popUpTo<Routes.Home> { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(AppText.logout, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileAvatar(name: String, role: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(PrimaryGreen.copy(alpha = 0.1f), CircleShape)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryGreen
                )
            }
            // Trust Badge
            Icon(
                imageVector = if (role.uppercase() == "GURU") Icons.Rounded.Verified else Icons.Rounded.School,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.White, CircleShape)
                    .padding(4.dp)
                    .border(1.dp, PrimaryGreen, CircleShape)
                    .padding(2.dp),
                tint = PrimaryGreen
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Surface(
            color = PrimaryGreen,
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "  $role  ",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}