package com.app.features.dashboard.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.app.core.common.Constants
import com.app.core.navigation.ImplicitIntentHelper
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.ui.screen.DashboardScreen

/**
 * DashboardActivity
 * - Receive data from LoginActivity via Intent extras
 * - Handle Implicit Intent for Google Maps
 * - Handle ActivityResultAPI for Verification flow
 */
class DashboardActivity : ComponentActivity() {

  // ActivityResultAPI launcher for VerificationActivity
  private val verificationLauncher =
          registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Catch result from VerificationActivity
            if (result.resultCode == RESULT_OK) {
              val verificationStatus =
                      result.data?.getStringExtra(Constants.EXTRA_VERIFICATION_STATUS) ?: "UNKNOWN"
              // Update state with result
              verificationStatusState.value = verificationStatus
            }
          }

  // State for verification status
  private val verificationStatusState = mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Extract extras from Intent
    val sensorName =
            intent.getStringExtra(Constants.EXTRA_SENSOR_NAME) ?: Constants.DEFAULT_SENSOR_NAME
    val latitude = intent.getStringExtra(Constants.EXTRA_LATITUDE) ?: Constants.DEFAULT_LATITUDE
    val longitude = intent.getStringExtra(Constants.EXTRA_LONGITUDE) ?: Constants.DEFAULT_LONGITUDE

    setContent {
      // Observe verification status
      val verificationStatus = verificationStatusState.value

      ApplicationTheme {
          DashboardScreen(
                  sensorName = sensorName,
                  latitude = latitude,
                  longitude = longitude,
                  onViewMap = {
                    // Implicit Intent → Google Maps
                    ImplicitIntentHelper.openMaps(
                            context = this@DashboardActivity,
                            latitude = latitude,
                            longitude = longitude
                    )
                  },
                  onVerify = {
                    // Launch VerificationActivity with ActivityResultAPI
                    val verificationIntent =
                        Intent(
                                this@DashboardActivity,
                                VerificationActivity::class.java
                        )
                    verificationLauncher.launch(verificationIntent)
                  },
                  verificationStatus = verificationStatus
          )
      }
    }
  }
}
