package com.college.task.utils

/** Format coordinate for display Input: lat="1.23", lon="4.56" Output: "1.23, 4.56" */
fun formatCoordinates(latitude: String, longitude: String): String {
    return "$latitude, $longitude"
}

/** Generate geo URI for Maps intent Format: geo:latitude,longitude example: geo:1.23,4.56 */
fun generateGeoUri(latitude: String, longitude: String): String {
    return "geo:$latitude,$longitude"
}
