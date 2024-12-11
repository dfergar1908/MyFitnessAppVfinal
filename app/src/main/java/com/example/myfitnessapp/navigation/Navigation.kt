package com.example.myfitnessapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfitnessapp.repository.RoutineRepository
import com.example.myfitnessapp.screens.home.Home
import com.example.myfitnessapp.screens.login.Login_Screen

import com.example.myfitnessapp.screens.home.RoutineScreen
import com.example.myfitnessapp.screens.splash.SplashScreen
import com.example.myfitnessapp.training.TrainingDetailsScreen
import com.example.myfitnessapp.training.TrainingScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name) {
            Login_Screen(navController = navController)
        }
        composable(Screens.HomeScreen.name) {
            Home(navController = navController)
        }
        composable(Screens.RoutineScreen.name) {
            RoutineScreen(navController = navController)
        }
        composable(Screens.TrainingScreen.name) {
            TrainingScreen(navController = navController) // Correcto aquí, cambiamos a TrainingScreen
        }
        composable(Screens.TrainingDetailScreen.name + "/{routineId}") { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")
            // Aquí puedes cargar la rutina específica usando el `routineId`
            TrainingDetailsScreen(navController = navController)
        }
    }
}




