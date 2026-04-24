package com.app.core.utils

fun formatCoordinates(latitude: String, longitude: String): String {
    return "$latitude, $longitude"
}

fun generateGeoUri(latitude: String, longitude: String): String {
    return "geo:$latitude,$longitude"
}
