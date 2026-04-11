package com.scrollguard.domain.usecase

import com.scrollguard.data.models.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DoomscrollDetector(
        private val config: DoomscrollConfig,
        private val clock: Clock = Clock.System
) {

    fun evaluate(session: ScrollSession): InterventionTrigger? {
        if (session.endTime != null) return null

        val currentDuration = (clock.now() - session.startTime).inWholeMinutes
        val currentLocalTime = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())

        // Check duration threshold
        if (currentDuration >= config.sessionDurationThresholdMinutes) {
            val severity = calculateSeverity(session, currentDuration, currentLocalTime)

            return InterventionTrigger(
                    type = config.interventionLevel,
                    reason = generateReason(session, currentDuration, currentLocalTime),
                    severity = severity,
                    sessionData = session
            )
        }

        return null
    }

    private fun calculateSeverity(
            session: ScrollSession,
            durationMinutes: Long,
            currentTime: LocalDateTime
    ): Float {
        var severity = 0.3f // Base severity

        // Duration factor (0.0 - 0.4)
        val durationFactor =
                (durationMinutes.toFloat() / config.sessionDurationThresholdMinutes).coerceAtMost(
                        1f
                ) * 0.4f
        severity += durationFactor

        // Scroll velocity factor (0.0 - 0.2)
        if (session.averageScrollVelocity >= config.scrollVelocityThreshold) {
            severity += 0.2f
        }

        // Late night amplification (0.0 - 0.1)
        if (config.lateNightModeEnabled && isLateNight(currentTime)) {
            severity += 0.1f
        }

        return severity.coerceAtMost(1f)
    }

    private fun generateReason(
            session: ScrollSession,
            durationMinutes: Long,
            currentTime: LocalDateTime
    ): String {
        val reasons = mutableListOf<String>()

        if (durationMinutes >= config.sessionDurationThresholdMinutes) {
            reasons.add("Session exceeded ${config.sessionDurationThresholdMinutes} minutes")
        }

        if (session.averageScrollVelocity >= config.scrollVelocityThreshold) {
            reasons.add("High scroll velocity detected")
        }

        if (config.lateNightModeEnabled && isLateNight(currentTime)) {
            reasons.add("Late night usage detected")
        }

        return reasons.joinToString(", ")
    }

    private fun isLateNight(time: LocalDateTime): Boolean {
        val currentTime = time.time.toString().substringBefore(":").toInt()
        val endTime = config.lateNightEnd.substringBefore(":").toInt()
        val startTime = config.lateNightStart.substringBefore(":").toInt()

        return if (startTime > endTime) {
            // Cross midnight case (e.g., 22:00 - 06:00)
            currentTime >= startTime || currentTime < endTime
        } else {
            // Normal case (e.g., 01:00 - 05:00)
            currentTime in startTime until endTime
        }
    }

    fun shouldIntervene(
            session: ScrollSession,
            recentInterventions: List<InterventionEvent>
    ): Boolean {
        val trigger = evaluate(session) ?: return false

        // Check returner window
        val recentDismissals =
                recentInterventions
                        .filter { it.packageName == session.appPackageName }
                        .filter { it.userResponse == UserResponse.DISMISSED }
                        .filter {
                            (clock.now() - it.triggeredAt).inWholeMinutes <
                                    config.returnerWindowMinutes
                        }

        if (recentDismissals.isNotEmpty()) {
            return true // Escalate intervention
        }

        // Check snooze count
        val todaySnoozes =
                recentInterventions
                        .filter { it.packageName == session.appPackageName }
                        .filter { it.userResponse == UserResponse.SNOOZED }
                        .filter {
                            it.triggeredAt.toLocalDateTime(TimeZone.currentSystemDefault()).date ==
                                    clock.now()
                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                                            .date
                        }
                        .sumOf { it.snoozeCount }

        return todaySnoozes < config.snoozeAllowedCount
    }
}
