package com.scrollguard.presentation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scrollguard.service.PermissionManager
import com.scrollguard.service.UsageStatsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class ScrollGuardUiState(
    val isLoading: Boolean = false,
    val permissionStatus: PermissionStatus = PermissionStatus(false, false, false),
    val isProtectionActive: Boolean = false,
    val usageStats: Map<String, Long> = emptyMap(),
    val lastIntervention: String? = null,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

data class PermissionStatus(
    val usageStats: Boolean,
    val accessibility: Boolean,
    val overlay: Boolean
) {
    val allGranted: Boolean
        get() = usageStats && accessibility && overlay
}

class WorkingScrollGuardViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val permissionManager = PermissionManager(context)
    private val usageStatsManager: UsageStatsManager? =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager(context)
            } else null

    private val _uiState = MutableStateFlow(ScrollGuardUiState())
    val uiState: StateFlow<ScrollGuardUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val permissionStatus = permissionManager.getPermissionStatus()
                _uiState.value = _uiState.value.copy(
                    permissionStatus = PermissionStatus(
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
    }

    fun loadUsageStats() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
                
                if (!permissionManager.isUsageStatsPermissionGranted()) {
                    _uiState.value = _uiState.value.copy(
                        usageStats = emptyMap(),
                        error = "Usage Stats permission required",
                        isRefreshing = false
                    )
                    return@launch
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
    }

    fun startProtection() {
        viewModelScope.launch {
            try {
                if (!permissionManager.getPermissionStatus().allGranted) {
                    _uiState.value = _uiState.value.copy(
                        error = "All permissions must be granted first"
                    )
                    return@launch
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
    }

    private fun startRealTimeMonitoring() {
        viewModelScope.launch {
            while (_uiState.value.isProtectionActive) {
                try {
                    loadUsageStats()
                    kotlinx.coroutines.delay(10000) // Check every 10 seconds
                } catch (e: Exception) {
                    Log.e("ScrollGuard", "Monitoring error: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        error = "Monitoring error: ${e.message}"
                    )
                    kotlinx.coroutines.delay(30000) // Wait longer on error
                }
            }
        }
    }

    fun stopProtection() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isProtectionActive = false,
                    usageStats = emptyMap(),
                    lastIntervention = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to stop protection: ${e.message}"
                )
            }
        }
    }

    // Permission request functions
    fun requestUsageStatsPermission(): Intent {
        return permissionManager.requestUsageStatsPermission()
    }

    fun requestAccessibilityPermission(): Intent {
        return permissionManager.requestAccessibilityPermission()
    }

    fun requestOverlayPermission(): Intent {
        return permissionManager.requestOverlayPermission()
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshUsageStats() {
        loadUsageStats()
    }

    override fun onCleared() {
        super.onCleared()
        // Stop monitoring when ViewModel is cleared
        if (_uiState.value.isProtectionActive) {
            stopProtection()
        }
    }
}
