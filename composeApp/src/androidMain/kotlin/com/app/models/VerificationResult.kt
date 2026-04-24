package com.app.models

data class VerificationResult(val status: String, val timestamp: Long = System.currentTimeMillis())
