package com.college.task.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * VerificationScreen Composable
 * - Render form verification sensor (optional: can checkbox or another input)
 * - Button for submit verification
 * - Return status ke DashboardActivity via ActivityResult
 */
@Composable
fun VerificationScreen(onSuccess: (String) -> Unit, onCancel: () -> Unit) {
  var isVerifying by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    Column(
      modifier = Modifier.fillMaxWidth(0.85f),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(
        text = "Verifikasi Sensor",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 24.dp)
      )

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 24.dp)
      ) {
        Text(
          text = "Lakukan verifikasi untuk memastikan sensor berfungsi dengan baik",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(16.dp)
        )
      }

      repeat(3) { index ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "✓",
            modifier = Modifier.padding(end = 12.dp)
          )
          Text(
            text = when (index) {
              0 -> "Cek koneksi sensor"
              1 -> "Kalibrasi perangkat"
              else -> "Verifikasi data"
            }
          )
        }
      }

      Column(
        modifier = Modifier.padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = {
            isVerifying = true
            onSuccess("SUCCESS")
            isVerifying = false
          },
          modifier = Modifier.fillMaxWidth(),
          enabled = !isVerifying,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue,
            contentColor = Color.White
          )
        ) {
          Text(if (isVerifying) "Memverifikasi..." else "Verifikasi")
        }

        OutlinedButton(
          onClick = onCancel,
          modifier = Modifier.fillMaxWidth(),
          enabled = !isVerifying
        ) {
          Text("Batal")
        }
      }
    }
  }
}
