package com.scrollguard.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.scrollguard.data.models.AppUsageRecord
import com.scrollguard.data.models.ScrollSession
import com.scrollguard.db.ScrollGuardDatabase
import com.scrollguard.domain.repository.UsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class UsageRepositoryImpl(private val db: ScrollGuardDatabase) : UsageRepository {

    private val queries = db.scrollGuardQueries

    override fun getUsageByDate(date: LocalDate): Flow<List<AppUsageRecord>> {
        return queries.getUsageByDate(date.toString()).asFlow().mapToList(Dispatchers.IO).map { list
            ->
            list.map {
                AppUsageRecord(
                        packageName = it.package_name,
                        date = it.date,
                        durationSeconds = it.duration_seconds,
                        sessionCount = it.session_count.toInt(),
                        lastUsed = Clock.System.now()
                )
            }
        }
    }

    override suspend fun recordSession(record: AppUsageRecord) {
        queries.insertUsage(
                package_name = record.packageName,
                date = record.date,
                duration_seconds = record.durationSeconds,
                session_count = record.sessionCount.toLong()
        )
    }

    override suspend fun deleteOldRecords(beforeDate: LocalDate) {
        queries.deleteOldRecords(beforeDate.toString())
    }

    override suspend fun getCurrentSession(packageName: String): ScrollSession? {
        // For now, return null - will implement with proper database schema
        return null
    }

    override suspend fun updateSession(session: ScrollSession) {
        // Will implement with proper database schema
    }

    override suspend fun endSession(packageName: String) {
        // Will implement with proper database schema
    }
}
