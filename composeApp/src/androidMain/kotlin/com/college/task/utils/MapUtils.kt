package com.college.task.utils

/** Format koordinat untuk display Input: lat="1.23", lon="4.56" Output: "1.23, 4.56" */
fun formatCoordinates(latitude: String, longitude: String): String {
    return "$latitude, $longitude"
}

/** Generate geo URI untuk Maps intent Format: geo:latitude,longitude Contoh: geo:1.23,4.56 */
fun generateGeoUri(latitude: String, longitude: String): String {
    return "geo:$latitude,$longitude"
}
