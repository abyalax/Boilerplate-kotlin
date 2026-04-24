package com.app.features.auth.login.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.core.navigation.IntentManager
import com.app.core.theme.ApplicationTheme
import com.app.features.auth.login.ui.screen.LoginScreen

class LoginActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
        ApplicationTheme {
            LoginScreen(
                onNavigate = { sensorName, latitude, longitude ->
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
}