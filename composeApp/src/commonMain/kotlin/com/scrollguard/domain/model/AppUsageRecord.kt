package com.scrollguard.domain.model

import kotlinx.datetime.LocalDate

data class AppUsageRecord(
    val id: Long? = null,
    val packageName: String,
    val date: LocalDate,
    val durationSeconds: Long,
    val sessionCount: Int = 1
)
