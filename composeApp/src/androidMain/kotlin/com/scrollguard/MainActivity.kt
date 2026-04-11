package com.scrollguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.scrollguard.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (GlobalContext.getOrNull() == null) {
            try {
                initKoin { androidContext(this@MainActivity) }
            } catch (e: Exception) {
                // Koin might already be started in some cases
            }
        }

        setContent { CleanScrollGuardApp() }
    }
}
