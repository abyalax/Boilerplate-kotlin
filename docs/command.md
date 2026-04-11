# ScrollGuard Development Commands

## Overview

Documentation of essential commands for ScrollGuard Kotlin Multiplatform development, debugging, deployment, and app distribution.

## Prerequisites

- Android SDK installed
- Android Emulator configured (Medium_Phone_API_36.1)
- ADB available in PATH
- Gradle wrapper configured

---

## 🐛 Debug Commands

### Clean Build

```bash
./gradlew clean
```

Remove all build artifacts and start fresh.

### Compile Check

```bash
./gradlew compileDebugKotlinAndroid
```

Check for compilation errors without full build.

### Build Debug APK

```bash
./gradlew composeApp:assembleDebug
```

Build debug APK for testing.

### Full Build with Stacktrace

```bash
./gradlew build --stacktrace
```

Full build with detailed error information.

---

## 📱 Emulator Commands

### Start Emulator

```bash
emulator -avd Medium_Phone_API_36.1
```

Start the Android emulator.

### Clean Start Emulator

```bash
emulator -avd Medium_Phone_API_36.1 -no-snapshot-load
```

Clean Start the Android emulator.

### Check Running Emulators

```bash
adb devices
```

List all connected devices/emulators.

### Kill Emulator

```bash
adb -s emulator-5554 emu kill
```

Kill specific emulator (replace emulator-5554 with actual device ID).

### Kill All Emulators

```bash
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done
```

Stop all running emulators.

---

## 🚀 Deploy Commands

### Install to Emulator

```bash
./gradlew composeApp:installDebug
```

Build and install debug APK to connected device/emulator.

### Uninstall App

```bash
adb uninstall com.scrollguard
```

Uninstall ScrollGuard from device/emulator.

### Reinstall Fresh

```bash
# Bash/Linux/macOS
adb uninstall com.scrollguard && ./gradlew composeApp:installDebug

# PowerShell (Windows)
adb uninstall com.scrollguard; ./gradlew composeApp:installDebug
```

Complete reinstall (uninstall + install).

---

## ▶️ Run Commands

### Start App via ADB

```bash
adb shell am start -n com.scrollguard/.MainActivity
```

Launch ScrollGuard app directly.

### Force Stop App

```bash
adb shell am force-stop com.scrollguard
```

Stop the ScrollGuard app.

### Clear App Data

```bash
adb shell pm clear com.scrollguard
```

Reset app to fresh state.

---

## �️ Shell Compatibility

### PowerShell (Windows) vs Bash Syntax

```powershell
# PowerShell uses semicolon instead of &&
adb uninstall com.scrollguard; ./gradlew composeApp:installDebug

# PowerShell uses different syntax for some operations
# Copy file
Copy-Item "composeApp\build\outputs\apk\debug\composeApp-debug.apk" ".\ScrollGuard-debug.apk"

# Remove directory
Remove-Item -Recurse -Force ".gradle"

# Check if file exists
if (Test-Path "keystore.jks") { Write-Host "Keystore exists" }
```

### Cross-Platform Aliases

```bash
# Use these commands for compatibility
# Instead of &&, run commands separately or use platform-specific syntax
```

---

### Gradle Sync

```bash
./gradlew --refresh-dependencies
```

Refresh and download all dependencies.

### Clean Sync

```bash
./gradlew clean build --refresh-dependencies
```

Full clean build with dependency refresh.

### Configuration Cache Reset

```bash
./gradlew clean build --no-configuration-cache
```

Build without configuration cache.

---

## 📦 Export & Distribution Commands

### Generate Release APK

```bash
./gradlew composeApp:assembleRelease
```

Build production-ready APK.

### Generate Signed APK

```bash
./gradlew composeApp:assembleRelease -Pandroid.injected.signing.file.name=keystore.jks -Pandroid.injected.signing.key.alias=release -Pandroid.injected.signing.key.password=yourpassword -Pandroid.injected.signing.store.password=yourstorepassword
```

Build signed APK with keystore.

### Generate App Bundle (Play Store)

```bash
./gradlew composeApp:bundleRelease
```

Create Android App Bundle for Play Store distribution.

### Export APK to Specific Location

```bash
cp composeApp/build/outputs/apk/debug/composeApp-debug.apk ./ScrollGuard-debug.apk
```

Copy APK to current directory.

---

## 🔍 Development Workflow Commands

### Full Development Cycle

```bash
# 1. Clean and build
./gradlew clean && ./gradlew composeApp:assembleDebug

# 2. Install to emulator
./gradlew composeApp:installDebug

# 3. Launch app
adb shell am start -n com.scrollguard/.MainActivity

# 4. Monitor logs
adb logcat -s ScrollGuard
```

### Debug Workflow

```bash
# 1. Check compilation
./gradlew compileDebugKotlinAndroid

# 2. If errors, check with stacktrace
./gradlew compileDebugKotlinAndroid --stacktrace

# 3. Fix and rebuild
./gradlew composeApp:assembleDebug

# 4. Install and test
./gradlew composeApp:installDebug
```

### Production Release Workflow

```bash
# 1. Clean build
./gradlew clean

# 2. Generate release APK
./gradlew composeApp:assembleRelease

# 3. Test release APK
./gradlew composeApp:installRelease

# 4. Export for distribution
cp composeApp/build/outputs/apk/release/composeApp-release.apk ./ScrollGuard-release.apk
```

---

## 📋 Quick Reference

| Command                                               | Purpose                |
| ----------------------------------------------------- | ---------------------- |
| `./gradlew composeApp:assembleDebug`                  | Build debug APK        |
| `./gradlew composeApp:installDebug`                   | Install to device      |
| `adb shell am start -n com.scrollguard/.MainActivity` | Launch app             |
| `adb uninstall com.scrollguard`                       | Uninstall app          |
| `./gradlew composeApp:assembleRelease`                | Build release APK      |
| `adb devices`                                         | List connected devices |
| `adb logcat -s ScrollGuard`                           | Monitor app logs       |

---

## 🛠️ Troubleshooting

### Build Issues

```bash
# Clear Gradle cache
./gradlew clean && rm -rf .gradle

# Reset project
./gradlew clean build --no-configuration-cache --refresh-dependencies
```

### Emulator Issues

```bash
# Reset ADB
adb kill-server && adb start-server

# Check emulator status
adb devices -l
```

### Installation Issues

```bash
# Force reinstall
adb uninstall com.scrollguard
./gradlew composeApp:installDebug

# Check available space
adb shell df /data/local/tmp
```

---

## 📱 Device Information

- **Package Name**: `com.scrollguard`
- **Main Activity**: `com.scrollguard.MainActivity`
- **Debug APK Location**: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
- **Release APK Location**: `composeApp/build/outputs/apk/release/composeApp-release.apk`

---

## 🔐 Signing Configuration

For release builds, ensure you have:

1. Keystore file (`keystore.jks`)
2. Key alias and passwords
3. Signing configuration in `build.gradle.kts`

Example signing setup:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore.jks")
            storePassword = "yourstorepassword"
            keyAlias = "release"
            keyPassword = "yourpassword"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

_Last updated: ScrollGuard Development v1.0_
