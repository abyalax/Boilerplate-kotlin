package com.college.task.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.college.task.utils.generateGeoUri

/** Helper object untuk Implicit Intent Digunakan untuk launch aplikasi eksternal (Google Maps) */
object ImplicitIntentHelper {

    /**
     * Buka Google Maps dengan koordinat tertentu Safe check: cek apakah Maps app tersedia sebelum
     * launch
     */
    fun openMaps(context: Context, latitude: String, longitude: String) {
        val geoUri = generateGeoUri(latitude, longitude)
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))

        // Check apakah ada aplikasi yang bisa handle geo intent
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback: buka Google Maps web
            val webIntent =
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://maps.google.com/?q=$latitude,$longitude")
                    )
            context.startActivity(webIntent)
        }
    }
}
