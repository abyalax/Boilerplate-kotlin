package com.scrollguard.di

import com.scrollguard.data.repository.InterventionRepositoryImpl
import com.scrollguard.data.repository.UsageRepositoryImpl
import com.scrollguard.domain.repository.InterventionRepository
import com.scrollguard.domain.repository.UsageRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule)
    }

val commonModule = module {
    single<UsageRepository> { UsageRepositoryImpl(get()) }
    single<InterventionRepository> { InterventionRepositoryImpl(get()) }
}

expect val platformModule: Module
