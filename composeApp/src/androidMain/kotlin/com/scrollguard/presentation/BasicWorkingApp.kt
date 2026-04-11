package com.scrollguard.presentation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

data class SimpleUiState(
    val isLoading: Boolean = false,
    val permissionStatus: SimplePermissionStatus = SimplePermissionStatus(false, false, false),
    val isProtectionActive: Boolean = false,
    val usageStats: Map<String, Long> = emptyMap(),
    val error: String? = null,
    val isRefreshing: Boolean = false
)

data class SimplePermissionStatus(
    val usageStats: Boolean,
    val accessibility: Boolean,
    val overlay: Boolean
) {
    val allGranted: Boolean
        get() = usageStats && accessibility && overlay
}

class BasicScrollGuardViewModel(
    private val permissionManager: com.scrollguard.service.PermissionManager,
    private val usageStatsManager: com.scrollguard.service.UsageStatsManager?
) {
    private val _uiState = MutableStateFlow(SimpleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        try {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val permissionStatus = permissionManager.getPermissionStatus()
            _uiState.value = _uiState.value.copy(
                permissionStatus = SimplePermissionStatus(
                    usageStats = permissionStatus.usageStats,
                    accessibility = permissionStatus.accessibility,
                    overlay = permissionStatus.overlay
                ),
                isLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Permission check failed: ${e.message}"
            )
        }
    }

    fun loadUsageStats() {
        try {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            if (!permissionManager.isUsageStatsPermissionGranted()) {
                _uiState.value = _uiState.value.copy(
                    usageStats = emptyMap(),
                    error = "Usage Stats permission required",
                    isRefreshing = false
                )
                return
            }

            Log.d("ScrollGuard", "Getting usage stats...")
            val stats = usageStatsManager?.getAppUsageStats() ?: emptyMap()
            Log.d("ScrollGuard", "Got stats: $stats")
            _uiState.value = _uiState.value.copy(
                usageStats = stats,
                error = null,
                isRefreshing = false
            )
        } catch (e: Exception) {
            Log.d("ScrollGuard", "Error loading stats: ${e.message}")
            _uiState.value = _uiState.value.copy(
                error = "Failed to load usage stats: ${e.message}",
                isRefreshing = false
            )
        }
    }

    fun startProtection() {
        try {
            if (!permissionManager.getPermissionStatus().allGranted) {
                _uiState.value = _uiState.value.copy(
                    error = "All permissions must be granted first"
                )
                return
            }

            _uiState.value = _uiState.value.copy(
                isProtectionActive = true,
                error = null
            )
            
            // Start monitoring with real-time updates
            startRealTimeMonitoring()
            
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to start protection: ${e.message}"
            )
        }
    }

    private fun startRealTimeMonitoring() {
        while (_uiState.value.isProtectionActive) {
            try {
                loadUsageStats()
                Thread.sleep(10000) // Check every 10 seconds
            } catch (e: Exception) {
                Log.e("ScrollGuard", "Monitoring error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Monitoring error: ${e.message}"
                )
                Thread.sleep(30000) // Wait longer on error
            }
        }
    }

    fun stopProtection() {
        try {
            _uiState.value = _uiState.value.copy(
                isProtectionActive = false,
                usageStats = emptyMap()
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to stop protection: ${e.message}"
            )
        }
    }

    // Permission request functions
    fun requestUsageStatsPermission(): android.content.Intent {
        return permissionManager.requestUsageStatsPermission()
    }

    fun requestAccessibilityPermission(): android.content.Intent {
        return permissionManager.requestAccessibilityPermission()
    }

    fun requestOverlayPermission(): android.content.Intent {
        return permissionManager.requestOverlayPermission()
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshUsageStats() {
        loadUsageStats()
    }
}

@Composable
@Preview
fun BasicWorkingApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollGuardViewModel: BasicScrollGuardViewModel = viewModel()
    
    Log.d("ScrollGuard", "BasicWorkingApp created")

    val uiState by scrollGuardViewModel.uiState.collectAsStateWithLifecycle()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ScrollGuard Header
            Text(
                text = "ScrollGuard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "TikTok Usage Monitor",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Permission Status
            if (!uiState.permissionStatus.allGranted) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Permissions Required",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Enable permissions to start monitoring:",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Permission buttons
                        if (!uiState.permissionStatus.usageStats) {
                            Button(
                                onClick = { 
                                    Log.d("ScrollGuard", "Usage Stats permission button clicked")
                                    scrollGuardViewModel.requestUsageStatsPermission()
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) { Text("Enable Usage Stats Permission") }
                        }
                        
                        if (!uiState.permissionStatus.accessibility) {
                            Button(
                                onClick = { 
                                    Log.d("ScrollGuard", "Accessibility permission button clicked")
                                    scrollGuardViewModel.requestAccessibilityPermission()
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) { Text("Enable Accessibility Permission") }
                        }
                        
                        if (!uiState.permissionStatus.overlay) {
                            Button(
                                onClick = { 
                                    Log.d("ScrollGuard", "Overlay permission button clicked")
                                    scrollGuardViewModel.requestOverlayPermission()
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) { Text("Enable Overlay Permission") }
                        }
                    }
                }
            }

            // Protection Status and Usage Stats
            if (uiState.permissionStatus.allGranted) {
                // Start/Stop Protection Button
                Button(
                    onClick = { 
                        Log.d("ScrollGuard", "Protection button clicked")
                        if (uiState.isProtectionActive) {
                            scrollGuardViewModel.stopProtection()
                        } else {
                            scrollGuardViewModel.startProtection()
                        }
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(if (uiState.isProtectionActive) "Stop Protection" else "Start Protection")
                }

                // Usage Stats Display
                if (uiState.usageStats.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📊 TikTok Usage Today",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            uiState.usageStats.forEach { (packageName, duration) ->
                                val hours = duration / 3600000
                                val minutes = (duration % 3600000) / 60000
                                Text(
                                    text = "TikTok: ${hours}h ${minutes}m",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            
                            Button(
                                onClick = {
                                    Log.d("ScrollGuard", "Refresh button clicked!")
                                    scrollGuardViewModel.refreshUsageStats()
                                },
                                enabled = !uiState.isRefreshing,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                if (uiState.isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Refresh")
                                }
                            }
                        }
                    }
                }

                // Empty Usage Stats State
                if (uiState.isProtectionActive && uiState.usageStats.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📊 Waiting for Usage Data",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Use TikTok and data will appear here",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    Log.d("ScrollGuard", "Check Now button clicked!")
                                    scrollGuardViewModel.refreshUsageStats()
                                },
                                enabled = !uiState.isRefreshing,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                if (uiState.isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Check Now")
                                }
                            }
                        }
                    }
                }
            }

            // Error Handling
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "❌ Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = {
                                Log.d("ScrollGuard", "Dismiss error button clicked")
                                scrollGuardViewModel.dismissError()
                            },
                            modifier = Modifier.padding(top = 12.dp)
                        ) { Text("Dismiss") }
                    }
                }
            }
        }
    }
}
