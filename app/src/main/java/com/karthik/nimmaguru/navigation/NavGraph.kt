package com.karthik.nimmaguru.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// UI Screen Imports
import com.karthik.nimmaguru.ui.screens.auth.*
import com.karthik.nimmaguru.ui.screens.home.*
import com.karthik.nimmaguru.ui.screens.splash.*
import com.karthik.nimmaguru.ui.screens.search.*
import com.karthik.nimmaguru.ui.screens.guru.*
import com.karthik.nimmaguru.ui.screens.session.*
import com.karthik.nimmaguru.ui.screens.appreciation.*
import com.karthik.nimmaguru.ui.screens.profile.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // Pro-Tip: Always start at Splash to allow the app to initialize
        // Firebase and Hilt properly before jumping to Home or Login.
        startDestination = Routes.Splash,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) + fadeIn()
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) + fadeOut()
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) + fadeIn()
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) + fadeOut()
        }
    ) {
        // --- Onboarding & Auth ---
        composable<Routes.Splash> {
            SplashScreen(navController)
        }

        composable<Routes.Login> {
            LoginScreen(navController)
        }

        composable<Routes.Register> {
            RegisterScreen(navController)
        }

        // --- Main Feature Screens ---
        composable<Routes.Home> {
            HomeScreen(navController)
        }

        composable<Routes.Search> {
            SearchScreen(navController)
        }

        composable<Routes.Profile> {
            ProfileScreen(navController)
        }

        composable<Routes.EditProfile> {
            EditGuruProfileScreen(navController)
        }

        composable<Routes.Sessions> {
            SessionListScreen(navController)
        }

        composable<Routes.CreateSession> {
            CreateSessionScreen(navController)
        }

        composable<Routes.Wall> {
            WallOfFameScreen(navController)
        }

        // --- Dynamic Content with Type-Safe Arguments ---

        // Guru Detail View
        composable<Routes.GuruProfile> { backStackEntry ->
            val profileArgs = backStackEntry.toRoute<Routes.GuruProfile>()
            GuruProfileScreen(
                navController = navController,
                guruId = profileArgs.guruId
            )
        }

        // Appreciation/Review View
        composable<Routes.Appreciation> { backStackEntry ->
            val appreciationArgs = backStackEntry.toRoute<Routes.Appreciation>()
            AppreciationScreen(
                navController = navController,
                guruId = appreciationArgs.guruId,
                guruName = appreciationArgs.guruName
            )
        }
    }
}