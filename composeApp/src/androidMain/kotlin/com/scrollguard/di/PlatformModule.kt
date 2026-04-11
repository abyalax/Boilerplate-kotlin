package com.scrollguard.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.scrollguard.db.ScrollGuardDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single {
        val driver = AndroidSqliteDriver(ScrollGuardDatabase.Schema, get(), "ScrollGuard.db")
        ScrollGuardDatabase(driver)
    }
}
