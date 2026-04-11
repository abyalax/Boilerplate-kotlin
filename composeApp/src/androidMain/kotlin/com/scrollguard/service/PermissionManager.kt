package com.scrollguard.service

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    fun isUsageStatsPermissionGranted(): Boolean {
        return try {
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            } else {
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    fun isAccessibilityPermissionGranted(): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            0
        }

        if (accessibilityEnabled != 1) return false

        val services = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        if (services.isNullOrEmpty()) return false

        val packageName = context.packageName
        val colonSplitter = TextUtils.SimpleStringSplitter(':')

        colonSplitter.setString(services)

        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            val flattenedComponentName = componentName.replace("/", ":")
            
            if (flattenedComponentName.contains(packageName)) {
                return true
            }
        }

        return false
    }

    fun isOverlayPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Overlay permission not required before Marshmallow
        }
    }

    fun requestUsageStatsPermission(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }

    fun requestAccessibilityPermission(): Intent {
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }

    fun requestOverlayPermission(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    }

    fun getPermissionStatus(): PermissionStatus {
        return PermissionStatus(
            usageStats = isUsageStatsPermissionGranted(),
            accessibility = isAccessibilityPermissionGranted(),
            overlay = isOverlayPermissionGranted()
        )
    }

    data class PermissionStatus(
        val usageStats: Boolean,
        val accessibility: Boolean,
        val overlay: Boolean
    ) {
        val allGranted: Boolean
            get() = usageStats && accessibility && overlay
    }
}
