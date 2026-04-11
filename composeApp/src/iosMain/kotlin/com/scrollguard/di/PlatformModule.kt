package com.scrollguard.di

import com.scrollguard.db.ScrollGuardDatabase
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.koin.dsl.module

actual val platformModule = module {
    single {
        val driver = NativeSqliteDriver(
            ScrollGuardDatabase.Schema,
            "ScrollGuard.db"
        )
        ScrollGuardDatabase(driver)
    }
}
