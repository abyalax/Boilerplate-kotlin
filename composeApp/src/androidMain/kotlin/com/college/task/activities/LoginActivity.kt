package com.college.task.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.college.task.navigation.IntentManager
import com.college.task.ui.screen.LoginScreen

/**
 * LoginActivity Entry point aplikasi
 * - Render LoginScreen composable
 * - Handle navigation ke DashboardActivity dengan Explicit Intent + Extras
 */
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                    onNavigate = { sensorName, latitude, longitude ->
                        // Gunakan IntentManager untuk navigate dengan data
                        IntentManager.navigateToDashboard(
                                context = this@LoginActivity,
                                sensorName = sensorName,
                                latitude = latitude,
                                longitude = longitude
                        )
                    }
            )
        }
    }
}
