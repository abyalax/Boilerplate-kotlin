package com.app.core.navigation

import android.content.Context
import android.content.Intent
import com.app.features.dashboard.activities.DashboardActivity
import com.app.core.common.Constants

object IntentManager {

    fun navigateToDashboard(
            context: Context,
            sensorName: String,
            latitude: String,
            longitude: String
    ) {
        val intent =
                Intent(context, DashboardActivity::class.java).apply {
                    putExtra(Constants.EXTRA_SENSOR_NAME, sensorName)
                    putExtra(Constants.EXTRA_LATITUDE, latitude)
                    putExtra(Constants.EXTRA_LONGITUDE, longitude)
                }
        context.startActivity(intent)
    }
}
