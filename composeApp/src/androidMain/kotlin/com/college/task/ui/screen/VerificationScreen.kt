package com.college.task.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * VerificationScreen Composable
 * - Render form verifikasi sensor (optional: bisa checkbox atau input lain)
 * - Button untuk submit verifikasi
 * - Return status ke DashboardActivity via ActivityResult
 */
@Composable
fun VerificationScreen(onSuccess: (String) -> Unit, onCancel: () -> Unit) {
  var isVerifying by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.Center
  ) {
    // Title
    Text(
      text = "Verifikasi Sensor",
      style = MaterialTheme.typography.headlineMedium,
      modifier = Modifier.padding(bottom = 24.dp)
    )

    // Description
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
      Text(
        text = "Lakukan verifikasi untuk memastikan sensor berfungsi dengan baik.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
      )
    }

    // Verification Steps (optional)
    repeat(3) { index ->
      Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(
          text = "✓",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(end = 12.dp)
        )
        Text(
          text =
            when (index) {
              0 -> "Cek koneksi sensor"
              1 -> "Kalibrasi perangkat"
              else -> "Verifikasi data"
            },
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Buttons
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = {
          isVerifying = true
          // Simulate verification delay
          onSuccess("SUCCESS")
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isVerifying
      ) { Text(if (isVerifying) "Memverifikasi..." else "Verifikasi") }

      OutlinedButton(
        onClick = onCancel,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isVerifying
      ) { Text("Batal") }
    }
  }
}
