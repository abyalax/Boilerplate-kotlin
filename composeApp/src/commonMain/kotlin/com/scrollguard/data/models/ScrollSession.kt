package com.scrollguard.data.models

import kotlinx.datetime.Instant

data class ScrollSession(
        val appPackageName: String,
        val startTime: Instant,
        val endTime: Instant? = null,
        val scrollEventCount: Int = 0,
        val averageScrollVelocity: Float = 0f,
        val pauseCount: Int = 0,
        val contentCategoryTag: String? = null,
        val durationSeconds: Long = 0L
) {
    val isActive: Boolean
        get() = endTime == null

    val actualDuration: Long
        get() = if (endTime != null) (endTime - startTime).inWholeSeconds else 0L
}

data class AppUsageRecord(
        val packageName: String,
        val date: String, // YYYY-MM-DD format
        val durationSeconds: Long,
        val sessionCount: Int = 1,
        val lastUsed: Instant
)

data class InterventionEvent(
        val id: String? = null,
        val packageName: String,
        val triggeredAt: Instant,
        val interventionType: InterventionType,
        val userResponse: UserResponse? = null,
        val snoozeCount: Int = 0,
        val sessionData: ScrollSession? = null
)

enum class InterventionType {
    SOFT_NUDGE,
    OVERLAY_PAUSE,
    SESSION_END
}

enum class UserResponse {
    DISMISSED,
    SNOOZED,
    ACCEPTED,
    BLOCKED
}

data class DoomscrollConfig(
        val sessionDurationThresholdMinutes: Int = 15,
        val scrollVelocityThreshold: Float = 0.8f,
        val lateNightModeEnabled: Boolean = true,
        val lateNightStart: String = "22:00",
        val lateNightEnd: String = "06:00",
        val returnerWindowMinutes: Int = 5,
        val interventionLevel: InterventionType = InterventionType.SOFT_NUDGE,
        val targetApps: List<String> = defaultSocialApps,
        val snoozeAllowedCount: Int = 2,
        val hardBlockEnabled: Boolean = false
) {
    companion object {
        val defaultSocialApps =
                listOf(
                        "com.instagram.android",
                        "com.zhiliaoapp.musically",
                        "com.twitter.android",
                        "com.reddit.frontpage",
                        "com.google.android.youtube"
                )
    }
}

data class InterventionTrigger(
        val type: InterventionType,
        val reason: String,
        val severity: Float, // 0.0 to 1.0
        val sessionData: ScrollSession
)
