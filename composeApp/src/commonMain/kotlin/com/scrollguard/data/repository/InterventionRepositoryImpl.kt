package com.scrollguard.data.repository

import com.scrollguard.db.ScrollGuardDatabase
import com.scrollguard.domain.model.InterventionEvent
import com.scrollguard.domain.repository.InterventionRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class InterventionRepositoryImpl(
    private val db: ScrollGuardDatabase
) : InterventionRepository {

    private val queries = db.scrollGuardQueries

    override fun getInterventionHistory(): Flow<List<InterventionEvent>> {
        return queries.getInterventionHistory()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map { 
                    InterventionEvent(
                        id = it.id,
                        packageName = it.package_name,
                        triggeredAt = Instant.parse(it.triggered_at),
                        interventionType = it.intervention_type,
                        userResponse = it.user_response,
                        snoozeCount = it.snooze_count?.toInt() ?: 0
                    )
                }
            }
    }

    override suspend fun logIntervention(event: InterventionEvent) {
        queries.insertIntervention(
            package_name = event.packageName,
            triggered_at = event.triggeredAt.toString(),
            intervention_type = event.interventionType,
            user_response = event.userResponse,
            snooze_count = event.snoozeCount.toLong()
        )
    }

    override fun getTodayOverrideCount(): Flow<Int> {
        // Implementation for today's override count
        return queries.getTodayOverrideCount()
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.toInt() }
    }
}
