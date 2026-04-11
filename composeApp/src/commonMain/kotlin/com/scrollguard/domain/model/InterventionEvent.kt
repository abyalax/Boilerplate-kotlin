package com.scrollguard.domain.model

import kotlinx.datetime.Instant

data class InterventionEvent(
    val id: Long? = null,
    val packageName: String,
    val triggeredAt: Instant,
    val interventionType: String,
    val userResponse: String? = null,
    val snoozeCount: Int = 0
)
