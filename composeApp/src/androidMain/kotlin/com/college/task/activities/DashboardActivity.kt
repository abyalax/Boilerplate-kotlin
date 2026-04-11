package com.college.task.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.college.task.common.Constants
import com.college.task.navigation.ImplicitIntentHelper
import com.college.task.ui.screen.DashboardScreen

/**
 * DashboardActivity
 * - Receive data dari LoginActivity via Intent extras
 * - Handle Implicit Intent untuk Google Maps
 * - Handle ActivityResultAPI untuk Verification flow
 */
class DashboardActivity : ComponentActivity() {

  // ActivityResultAPI launcher untuk VerificationActivity
  private val verificationLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      // Tangkap result dari VerificationActivity
      if (result.resultCode == RESULT_OK) {
        val verificationStatus =
          result.data?.getStringExtra(Constants.EXTRA_VERIFICATION_STATUS)
            ?: "UNKNOWN"
        // Update state dengan result
        verificationStatusState.value = verificationStatus
      }
    }

  // State untuk verification status
  private val verificationStatusState = mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Extract extras dari Intent
    val sensorName =
      intent.getStringExtra(Constants.EXTRA_SENSOR_NAME) ?: Constants.DEFAULT_SENSOR_NAME
    val latitude = intent.getStringExtra(Constants.EXTRA_LATITUDE) ?: Constants.DEFAULT_LATITUDE
    val longitude =
      intent.getStringExtra(Constants.EXTRA_LONGITUDE) ?: Constants.DEFAULT_LONGITUDE

    setContent {
      // Observe verification status
      val verificationStatus = verificationStatusState.value

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
          // Launch VerificationActivity dengan ActivityResultAPI
          val verificationIntent =
            android.content.Intent(
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
