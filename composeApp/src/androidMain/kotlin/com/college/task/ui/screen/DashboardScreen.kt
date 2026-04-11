package com.college.task.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.college.task.utils.formatCoordinates

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
    verticalArrangement = Arrangement.Top
  ) {
    // Title
    Text(
      text = "Dashboard Sensor",
      style = MaterialTheme.typography.headlineMedium,
      modifier = Modifier.padding(bottom = 24.dp)
    )

    // Sensor Info Card
    Card(modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp)) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Informasi Sensor",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        // Sensor Name
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
          Text(
            text = "Nama: ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
          )
          Text(
            text = sensorName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
          )
        }

        // Coordinates
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
          Text(
            text = "Lokasi: ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
          )
          Text(
            text = formatCoordinates(latitude, longitude),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Verification Status Display (jika ada)
    if (verificationStatus != null) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (verificationStatus == "SUCCESS")
            MaterialTheme.colorScheme.surfaceVariant
          else MaterialTheme.colorScheme.errorContainer
        )
      ) {
        Text(
          text = "Status Verifikasi: $verificationStatus",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(16.dp),
          color =
            if (verificationStatus == "SUCCESS")
              MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onErrorContainer
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Buttons Section
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // View Map Button (Implicit Intent)
      Button(
        onClick = onViewMap,
        modifier = Modifier.fillMaxWidth(),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
      ) { Text("Lihat di Peta") }

      // Verify Sensor Button (ActivityResultAPI)
      Button(
        onClick = onVerify,
        modifier = Modifier.fillMaxWidth(),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
          )
      ) { Text("Verifikasi Sensor") }
    }
  }
}
