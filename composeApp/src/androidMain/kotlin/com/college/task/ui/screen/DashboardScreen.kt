package com.college.task.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.college.task.utils.formatCoordinates
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

/**
 * DashboardScreen Composable
 * - Display Nama Sensor dari Intent extras
 * - Button "Lihat di Peta" (Implicit Intent → Google Maps)
 * - Button "Verifikasi Sensor" (ActivityResultAPI)
 */
@Composable
fun DashboardScreen(
  sensorName: String,
  latitude: String,
  longitude: String,
  onViewMap: () -> Unit,
  onVerify: () -> Unit,
  verificationStatus: String? = null
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    Column(
      modifier = Modifier
        .fillMaxWidth(0.85f), // biar gak full lebar
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(
        text = "Dashboard Sensor",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 24.dp)
      )

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally // ini tambahan
        ) {
          Text(
            text = "Informasi Sensor",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          Text("Nama: $sensorName")
          Text("Lokasi: ${formatCoordinates(latitude, longitude)}")
        }
      }

      if (verificationStatus != null) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
          Text(
            text = "Status Verifikasi: $verificationStatus",
            modifier = Modifier.padding(16.dp)
          )
        }
      }

      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = onViewMap,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue,
            contentColor = Color.White
          )
        ) { Text("Lihat di Peta") }

        Button(
          onClick = onVerify,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray,
            contentColor = Color.White
          )
        ) { Text("Verifikasi Sensor") }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
  MaterialTheme {
    DashboardScreen(
      sensorName = "Sensor DHT11",
      latitude = "-6.200000",
      longitude = "106.816666",
      onViewMap = {},
      onVerify = {},
      verificationStatus = null
    )
  }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenVerifiedPreview() {
  MaterialTheme {
    DashboardScreen(
      sensorName = "Sensor DHT11",
      latitude = "-6.200000",
      longitude = "106.816666",
      onViewMap = {},
      onVerify = {},
      verificationStatus = "Verified by Admin"
    )
  }
}

