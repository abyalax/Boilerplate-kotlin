package com.app.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.navigation.IntentManager
import com.app.ui.screen.LoginScreen

/**
 * LoginActivity as entry point apps
 * - Render LoginScreen composable
 * - Handle navigation ke DashboardActivity with Explicit Intent + Extras
 */
class LoginActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LoginScreen(
              onNavigate = { sensorName, latitude, longitude ->
                // Use IntentManager for navigate with data
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
