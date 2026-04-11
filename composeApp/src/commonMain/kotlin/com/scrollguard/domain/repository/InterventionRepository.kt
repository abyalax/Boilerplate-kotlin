package com.scrollguard.domain.repository

import com.scrollguard.domain.model.InterventionEvent
import kotlinx.coroutines.flow.Flow

interface InterventionRepository {
    fun getInterventionHistory(): Flow<List<InterventionEvent>>
    suspend fun logIntervention(event: InterventionEvent)
    fun getTodayOverrideCount(): Flow<Int>
}
