package com.scrollguard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.scrollguard.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class ScrollGuardMonitoringService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "scrollguard_monitoring"
        const val ACTION_START_MONITORING = "com.scrollguard.START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.scrollguard.STOP_MONITORING"

        val interventionEvents = MutableSharedFlow<InterventionEvent>()

        data class InterventionEvent(
                val packageName: String,
                val reason: String,
                val timestamp: Long
        )
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MONITORING -> {
                startMonitoring()
                return START_STICKY
            }
            ACTION_STOP_MONITORING -> {
                stopMonitoring()
                return START_NOT_STICKY
            }
            else -> {
                // Default action
                startMonitoring()
                return START_STICKY
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel(
                                    CHANNEL_ID,
                                    "ScrollGuard Monitoring",
                                    NotificationManager.IMPORTANCE_LOW
                            )
                            .apply {
                                description = "Monitors app usage for doomscrolling detection"
                                setShowBadge(false)
                                enableVibration(false)
                                setSound(null, null)
                            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent =
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

        val pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ScrollGuard")
                .setContentText("Monitoring your app usage")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
    }

    private fun startMonitoring() {
        startForeground(NOTIFICATION_ID, createNotification())

        serviceScope.launch { monitorAppUsage() }
    }

    private fun stopMonitoring() {
        serviceScope.cancel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION") stopForeground(true)
        }
        stopSelf()
    }

    private suspend fun monitorAppUsage() {
        while (serviceScope.isActive) {
            try {
                // Get current session data from accessibility service
                val sessionData = getCurrentSessionFromAccessibility()

                if (sessionData != null && isTargetApp(sessionData.packageName)) {
                    val shouldIntervene = evaluateSession(sessionData)

                    if (shouldIntervene) {
                        val interventionReason = generateInterventionReason(sessionData)

                        // Emit intervention event
                        interventionEvents.emit(
                                InterventionEvent(
                                        packageName = sessionData.packageName,
                                        reason = interventionReason,
                                        timestamp = System.currentTimeMillis()
                                )
                        )

                        // Show notification
                        showInterventionNotification(sessionData.packageName, interventionReason)
                    }
                }

                delay(5000) // Check every 5 seconds
            } catch (e: Exception) {
                // Handle error and continue
                delay(10000) // Wait longer on error
            }
        }
    }

    private fun getCurrentSessionFromAccessibility(): ScrollGuardAccessibilityService.SessionData? {
        // This would connect to the accessibility service
        // For now, return null - will be implemented with proper service binding
        return null
    }

    private fun isTargetApp(packageName: String): Boolean {
        val targetApps =
                listOf(
                        "com.zhiliaoapp.musically", // TikTok
                        "com.instagram.android",
                        "com.twitter.android",
                        "com.reddit.frontpage",
                        "com.google.android.youtube"
                )
        return packageName in targetApps
    }

    private fun evaluateSession(session: ScrollGuardAccessibilityService.SessionData): Boolean {
        val durationMinutes = session.duration / 60000
        val avgVelocity = session.averageVelocity

        return when {
            durationMinutes >= 15 -> true // Duration threshold
            avgVelocity > 0.8f && session.pauseCount < 2 -> true // High velocity, low pauses
            durationMinutes >= 10 && isLateNight() -> true // Late night sensitivity
            else -> false
        }
    }

    private fun isLateNight(): Boolean {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return hour >= 22 || hour < 6
    }

    private fun generateInterventionReason(
            session: ScrollGuardAccessibilityService.SessionData
    ): String {
        val reasons = mutableListOf<String>()
        val durationMinutes = session.duration / 60000

        if (durationMinutes >= 15) {
            reasons.add("Session exceeded 15 minutes")
        }

        if (session.averageVelocity > 0.8f) {
            reasons.add("High scroll velocity detected")
        }

        if (session.pauseCount < 2 && durationMinutes > 5) {
            reasons.add("Continuous scrolling without breaks")
        }

        if (isLateNight()) {
            reasons.add("Late night usage")
        }

        return reasons.joinToString(", ")
    }

    private fun showInterventionNotification(packageName: String, reason: String) {
        val intent =
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("intervention_package", packageName)
                    putExtra("intervention_reason", reason)
                }

        val pendingIntent =
                PendingIntent.getActivity(
                        this,
                        System.currentTimeMillis().toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        val appName = getAppDisplayName(packageName)

        val notification =
                NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("ScrollGuard Intervention")
                        .setContentText("Time to take a break from $appName")
                        .setStyle(
                                androidx.core.app.NotificationCompat.BigTextStyle()
                                        .bigText(
                                                "You've been scrolling $appName for a while.\n\nReason: $reason"
                                        )
                        )
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun getAppDisplayName(packageName: String): String {
        return when (packageName) {
            "com.zhiliaoapp.musically" -> "TikTok"
            "com.instagram.android" -> "Instagram"
            "com.twitter.android" -> "Twitter/X"
            "com.reddit.frontpage" -> "Reddit"
            "com.google.android.youtube" -> "YouTube"
            else -> packageName
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
