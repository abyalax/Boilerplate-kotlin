package com.college.task

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
