package com.scrollguard.presentation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun SimpleTestApp() {
    val context = LocalContext.current
    val scrollGuardViewModel: ScrollGuardViewModel = viewModel()

    Log.d("ScrollGuard", "SimpleTestApp created")

    var clickCount by remember { mutableStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ScrollGuard Debug Test", style = MaterialTheme.typography.headlineMedium)

            Text(text = "Click count: $clickCount", modifier = Modifier.padding(vertical = 8.dp))

            Button(
                    onClick = {
                        Log.d("ScrollGuard", "Button clicked in UI!")
                        clickCount++
                        scrollGuardViewModel.refreshUsageStats()
                    }
            ) { Text("Test Button") }
        }
    }
}
