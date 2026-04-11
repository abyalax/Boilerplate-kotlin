package com.scrollguard.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class ScrollGuardAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var currentAppPackage: String? = null
    private var sessionStartTime: Long = 0
    private var scrollEventCount: Int = 0
    private var lastScrollTime: Long = 0
    private var scrollVelocitySum: Float = 0f
    private var pauseCount: Int = 0
    private var lastActivityTime: Long = System.currentTimeMillis()

    companion object {
        val scrollEvents = MutableSharedFlow<ScrollEvent>()
        val appSwitchEvents = MutableSharedFlow<AppSwitchEvent>()

        data class ScrollEvent(
                val packageName: String,
                val scrollVelocity: Float,
                val timestamp: Long
        )

        data class AppSwitchEvent(
                val fromPackage: String?,
                val toPackage: String,
                val timestamp: Long
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { handleAccessibilityEvent(it) }
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Service started - can initialize monitoring here
    }

    private fun handleAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChanged(event)
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                handleScrollEvent(event)
            }
        }
    }

    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString()

        if (packageName != currentAppPackage) {
            // App switched
            val previousApp = currentAppPackage
            currentAppPackage = packageName

            serviceScope.launch {
                appSwitchEvents.emit(
                        AppSwitchEvent(
                                fromPackage = previousApp,
                                toPackage = packageName ?: "",
                                timestamp = System.currentTimeMillis()
                        )
                )
            }

            // Reset session tracking for new app
            resetSessionTracking()
        }
    }

    private fun handleScrollEvent(@Suppress("UNUSED_PARAMETER") event: AccessibilityEvent) {
        val currentTime = System.currentTimeMillis()
        val packageName = currentAppPackage ?: return

        // Calculate scroll velocity based on time between scrolls
        val timeDelta = currentTime - lastScrollTime
        val velocity =
                if (lastScrollTime > 0 && timeDelta > 0) {
                    // Simple velocity calculation (inverse of time delta)
                    1000f / timeDelta
                } else {
                    0f
                }

        // Update tracking variables
        scrollEventCount++
        scrollVelocitySum += velocity
        lastScrollTime = currentTime
        lastActivityTime = currentTime

        // Check for pause (no activity for 2+ seconds)
        if (currentTime - lastActivityTime > 2000) {
            pauseCount++
        }

        serviceScope.launch {
            scrollEvents.emit(
                    ScrollEvent(
                            packageName = packageName,
                            scrollVelocity = velocity,
                            timestamp = currentTime
                    )
            )
        }
    }

    private fun resetSessionTracking() {
        sessionStartTime = System.currentTimeMillis()
        scrollEventCount = 0
        scrollVelocitySum = 0f
        pauseCount = 0
        lastScrollTime = 0
        lastActivityTime = System.currentTimeMillis()
    }

    fun getCurrentSessionData(): SessionData? {
        return currentAppPackage?.let { pkg ->
            val currentTime = System.currentTimeMillis()
            val duration = currentTime - sessionStartTime
            val avgVelocity = if (scrollEventCount > 0) scrollVelocitySum / scrollEventCount else 0f

            SessionData(
                    packageName = pkg,
                    duration = duration,
                    scrollCount = scrollEventCount,
                    averageVelocity = avgVelocity,
                    pauseCount = pauseCount
            )
        }
    }

    data class SessionData(
            val packageName: String,
            val duration: Long,
            val scrollCount: Int,
            val averageVelocity: Float,
            val pauseCount: Int
    )

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
