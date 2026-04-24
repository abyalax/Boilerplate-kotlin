package com.app.core.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.app.core.utils.generateGeoUri

object ImplicitIntentHelper {

    fun openMaps(context: Context, latitude: String, longitude: String) {
        val geoUri = generateGeoUri(latitude, longitude)
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val webIntent =
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://maps.google.com/?q=$latitude,$longitude")
                    )
            context.startActivity(webIntent)
        }
    }
}
