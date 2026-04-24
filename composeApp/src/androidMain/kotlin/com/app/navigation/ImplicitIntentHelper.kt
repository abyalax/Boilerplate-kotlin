package com.app.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.app.utils.generateGeoUri

/** Helper object for Implicit Intent used for launch external apps (Google Maps) */
object ImplicitIntentHelper {

    /** Open Google Maps */
    fun openMaps(context: Context, latitude: String, longitude: String) {
        val geoUri = generateGeoUri(latitude, longitude)
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))

        // Check if there's an app that can handle this intent
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback: open Google Maps web
            val webIntent =
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://maps.google.com/?q=$latitude,$longitude")
                    )
            context.startActivity(webIntent)
        }
    }
}
