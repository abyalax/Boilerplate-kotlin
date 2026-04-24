package com.app.features.auth.login.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * SensorForm Component Reusable form for input Nama Sensor and Coordinate (Latitude, Longitude)
 * Used at LoginScreen and VerificationScreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorForm(
        sensorName: String,
        onNameChange: (String) -> Unit,
        latitude: String,
        onLatChange: (String) -> Unit,
        longitude: String,
        onLonChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth().padding(top = 16.dp)) {
    // Sensor Name OutlinedTextField
    OutlinedTextField(
            label = { Text("Nama Sensor") },
            value = sensorName,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            placeholder = { Text("Example: Sensor A") },
            singleLine = true
    )

    OutlinedTextField(
            label = { Text("Latitude") },
            value = latitude,
            onValueChange = onLatChange,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            placeholder = { Text("Example: 1.23") },
            singleLine = true
    )

    OutlinedTextField(
            label = { Text("Longitude") },
            value = longitude,
            onValueChange = onLonChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Example: 4.56") },
            singleLine = true
    )
  }
}

@Preview(showBackground = true)
@Composable
fun SensorFormPreview() {
  SensorForm(
          sensorName = "Sensor A",
          onNameChange = {},
          latitude = "-6.200000",
          onLatChange = {},
          longitude = "106.816666",
          onLonChange = {}
  )
}
