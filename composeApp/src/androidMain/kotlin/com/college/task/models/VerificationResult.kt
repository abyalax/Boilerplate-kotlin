package com.college.task.models

data class VerificationResult(val status: String, val timestamp: Long = System.currentTimeMillis())
