package com.app.core.utils

fun isValidCoordinate(latitude: String, longitude: String): Boolean {
    return try {
        val lat = latitude.toDouble()
        val lon = longitude.toDouble()
        lat in -90.0..90.0 && lon in -180.0..180.0
    } catch (e: NumberFormatException) {
        false
    }
}

fun isValidSensorName(name: String): Boolean {
    return name.isNotBlank() && name.length >= 3
}
