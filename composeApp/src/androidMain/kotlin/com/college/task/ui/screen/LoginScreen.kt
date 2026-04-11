package com.college.task.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.college.task.ui.components.SensorForm

@Composable
fun LoginScreen(onNavigate: (String, String, String) -> Unit) {
  var sensorName by remember { mutableStateOf("") }
  var latitude by remember { mutableStateOf("") }
  var longitude by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text("Login Sensor", style = MaterialTheme.typography.headlineSmall)

    SensorForm(
      sensorName = sensorName,
      onNameChange = { sensorName = it },
      latitude = latitude,
      onLatChange = { latitude = it },
      longitude = longitude,
      onLonChange = { longitude = it }
    )

    Button(onClick = { onNavigate(sensorName, latitude, longitude) }) { Text("Lanjut") }
  }
}

@Preview
@Composable
fun LoginScreenPreview() {
  MaterialTheme {
    LoginScreen(onNavigate = { _, _, _ -> })
  }
}
