package com.example.boilerplate_kotlin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform