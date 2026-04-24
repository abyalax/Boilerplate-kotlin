package com.app.features.dashboard.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.core.common.Constants
import com.app.features.dashboard.ui.screen.VerificationScreen

/**
 * VerificationActivity
 * - Render VerificationScreen composable
 * - Return verification status to DashboardActivity via setResult()
 * - Launched from DashboardActivity with ActivityResultAPI
 */
class VerificationActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      VerificationScreen(
              onSuccess = { status ->
                // Set result and finish activity
                setResult(
                        RESULT_OK,
                        Intent().apply { putExtra(Constants.EXTRA_VERIFICATION_STATUS, status) }
                )
                finish()
              },
              onCancel = {
                // Cancel: set result with RESULT_CANCELED
                setResult(RESULT_CANCELED)
                finish()
              }
      )
    }
  }
}
