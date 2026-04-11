package com.scrollguard.domain.repository

import com.scrollguard.data.models.AppUsageRecord
import com.scrollguard.data.models.ScrollSession
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface UsageRepository {
    fun getUsageByDate(date: LocalDate): Flow<List<AppUsageRecord>>
    suspend fun recordSession(record: AppUsageRecord)
    suspend fun deleteOldRecords(beforeDate: LocalDate)
    suspend fun getCurrentSession(packageName: String): ScrollSession?
    suspend fun updateSession(session: ScrollSession)
    suspend fun endSession(packageName: String)
}
