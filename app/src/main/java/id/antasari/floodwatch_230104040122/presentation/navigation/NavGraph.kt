package id.antasari.floodwatch_230104040122.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType // <-- TAMBAHAN PENTING
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // <-- TAMBAHAN PENTING

// --- IMPORT SEMUA HALAMAN ---
import id.antasari.floodwatch_230104040122.presentation.auth.LoginScreen
import id.antasari.floodwatch_230104040122.presentation.auth.RegisterScreen
import id.antasari.floodwatch_230104040122.presentation.home.HomeScreen
import id.antasari.floodwatch_230104040122.presentation.notification.NotificationScreen
import id.antasari.floodwatch_230104040122.presentation.profile.AboutAppScreen
import id.antasari.floodwatch_230104040122.presentation.profile.AccountSettingsScreen
import id.antasari.floodwatch_230104040122.presentation.profile.HelpSupportScreen
import id.antasari.floodwatch_230104040122.presentation.profile.ProfileScreen
import id.antasari.floodwatch_230104040122.presentation.sensor.AddSensorScreen
import id.antasari.floodwatch_230104040122.presentation.sensor.SensorListScreen
// Import halaman Detail yang baru kita buat
import id.antasari.floodwatch_230104040122.presentation.detail.SensorDetailScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Rute 1: Login
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Rute 2: Home Dashboard
        composable("home") {
            HomeScreen(navController = navController)
        }

        // Rute 3: Tambah Sensor (Form Input)
        composable("add_sensor") {
            AddSensorScreen(navController = navController)
        }

        // Rute 4: Daftar Semua Sensor (List)
        composable("sensor_list") {
            SensorListScreen(navController = navController)
        }

        // Rute 5: Profil
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // Rute 6: Register (Daftar Akun)
        composable("register") {
            RegisterScreen(navController = navController)
        }

        // --- SUB-MENU PROFILE ---

        composable("account_settings") {
            AccountSettingsScreen(navController = navController)
        }

        composable("about_app") {
            AboutAppScreen(navController = navController)
        }

        composable("help_support") {
            HelpSupportScreen(navController = navController)
        }

        composable("notification") {
            NotificationScreen(navController = navController)
        }

        // --- RUTE BARU: DETAIL SENSOR (KOMENTAR & FOTO) ---
        // Rute ini menerima parameter dinamis: {sensorId} dan {sensorName}
        composable(
            route = "detail/{sensorId}/{sensorName}",
            arguments = listOf(
                navArgument("sensorId") { type = NavType.StringType },
                navArgument("sensorName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Menangkap data yang dikirim dari halaman List
            val sensorId = backStackEntry.arguments?.getString("sensorId") ?: ""
            val sensorName = backStackEntry.arguments?.getString("sensorName") ?: ""

            // Memanggil layar Detail
            SensorDetailScreen(
                navController = navController,
                sensorId = sensorId,
                sensorName = sensorName
            )
        }
    }
}