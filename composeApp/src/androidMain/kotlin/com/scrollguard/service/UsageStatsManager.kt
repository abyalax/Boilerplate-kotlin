package com.scrollguard.service

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class UsageStatsManager(private val context: Context) {

    private val usageStatsManager: UsageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    suspend fun getCurrentAppPackage(): String? =
            withContext(Dispatchers.IO) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val stats =
                            usageStatsManager.queryUsageStats(
                                    UsageStatsManager.INTERVAL_DAILY,
                                    currentTime - TimeUnit.MINUTES.toMillis(1),
                                    currentTime
                            )

                    stats?.maxByOrNull { it.lastTimeUsed }?.packageName
                } catch (e: SecurityException) {
                    null // Permission not granted
                } catch (e: Exception) {
                    null
                }
            }

    suspend fun getAppUsageStats(): Map<String, Long> =
            withContext(Dispatchers.IO) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val startTime = currentTime - TimeUnit.DAYS.toMillis(1) // Last 24 hours

                    val stats =
                            usageStatsManager.queryUsageStats(
                                    UsageStatsManager.INTERVAL_DAILY,
                                    startTime,
                                    currentTime
                            )

                    // Filter for only TikTok
                    val tikTokPackage = "com.zhiliaoapp.musically"
                    val tikTokStats = stats?.find { it.packageName == tikTokPackage }

                    if (tikTokStats != null && tikTokStats.totalTimeInForeground > 0) {
                        mapOf(tikTokPackage to tikTokStats.totalTimeInForeground)
                    } else {
                        emptyMap()
                    }
                } catch (e: SecurityException) {
                    emptyMap() // Permission not granted
                } catch (e: Exception) {
                    emptyMap()
                }
            }

    suspend fun getTodayUsageForApp(packageName: String): Long =
            withContext(Dispatchers.IO) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val startOfDay = getStartOfDay(currentTime)

                    val stats =
                            usageStatsManager.queryUsageStats(
                                    UsageStatsManager.INTERVAL_DAILY,
                                    startOfDay,
                                    currentTime
                            )

                    stats?.find { it.packageName == packageName }?.totalTimeInForeground ?: 0L
                } catch (e: SecurityException) {
                    0L // Permission not granted
                } catch (e: Exception) {
                    0L
                }
            }

    suspend fun getWeeklyUsageStats(): Map<String, Long> =
            withContext(Dispatchers.IO) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val weekAgo = currentTime - TimeUnit.DAYS.toMillis(7)

                    val stats =
                            usageStatsManager.queryUsageStats(
                                    UsageStatsManager.INTERVAL_WEEKLY,
                                    weekAgo,
                                    currentTime
                            )

                    stats?.associate { stat -> stat.packageName to stat.totalTimeInForeground }
                            ?: emptyMap()
                } catch (e: SecurityException) {
                    emptyMap() // Permission not granted
                } catch (e: Exception) {
                    emptyMap()
                }
            }

    suspend fun getMostUsedApps(limit: Int = 10): List<AppUsageInfo> =
            withContext(Dispatchers.IO) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val dayAgo = currentTime - TimeUnit.DAYS.toMillis(1)

                    val stats =
                            usageStatsManager.queryUsageStats(
                                    UsageStatsManager.INTERVAL_DAILY,
                                    dayAgo,
                                    currentTime
                            )

                    stats
                            ?.map { stat ->
                                AppUsageInfo(
                                        packageName = stat.packageName,
                                        totalTime = stat.totalTimeInForeground,
                                        lastUsed = stat.lastTimeUsed
                                )
                            }
                            ?.sortedByDescending { it.totalTime }
                            ?.take(limit)
                            ?: emptyList()
                } catch (e: SecurityException) {
                    emptyList() // Permission not granted
                } catch (e: Exception) {
                    emptyList()
                }
            }

    fun isUsageStatsPermissionGranted(): Boolean {
        return try {
            val currentTime = System.currentTimeMillis()
            val stats =
                    usageStatsManager.queryUsageStats(
                            UsageStatsManager.INTERVAL_DAILY,
                            currentTime - TimeUnit.HOURS.toMillis(1),
                            currentTime
                    )
            stats != null && stats.isNotEmpty()
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    data class AppUsageInfo(
            val packageName: String,
            val totalTime: Long,
            val lastUsed: Long,
            val launchCount: Int = 0
    )
}
