package com.app.features.dashboard.model

data class VerificationResult(val status: String, val timestamp: Long = System.currentTimeMillis())
