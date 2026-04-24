# Dokumentasi Struktur Proyek Kotlin Multiplatform

## 📋 Overview

Proyek ini adalah **Kotlin Multiplatform Project** yang menggunakan **Compose Multiplatform** untuk membuat aplikasi mobile yang dapat berjalan di **Android** dan **iOS** dengan satu codebase.

## 🏗️ Struktur Utama Proyek

```
Boilerplate-kotlin/
├── 📁 app/                  # Module utama aplikasi
├── 📁 iosApp/               # Proyek Xcode untuk iOS
├── 📁 gradle/               # Konfigurasi Gradle
├── 📄 build.gradle.kts      # Build script root
├── 📄 gradlew               # Gradle wrapper (Linux/Mac)
├── 📄 gradlew.bat           # Gradle wrapper (Windows)
├── 📄 settings.gradle.kts   # Konfigurasi settings Gradle
├── 📄 gradle.properties     # Properties Gradle
├── 📄 local.properties      # Properties lokal (tidak di-track git)
└── 📄 README.md             # Dokumentasi utama
```

---

## 📱 Module `app` (Aplikasi Utama)

Ini adalah module utama yang berisi semua kode aplikasi dengan struktur yang terorganisir berdasarkan fitur dan arsitektur yang clean.

### Struktur Lengkap `app/`

```
└── 📁app
    └── 📁core
        └── 📁common
            ├── Constant.kt
        └── 📁navigation
            ├── ImplicitIntentHelper.kt
            ├── IntentManager.kt
        └── 📁utils
            ├── MapUtils.kt
            ├── VaildateCoordinate.kt
    └── 📁features
        └── 📁auth
            └── 📁login
                └── 📁activities
                    ├── LoginActivity.kt
                └── 📁ui
                    └── 📁components
                        ├── LoginForm.kt
                    └── 📁screen
                        ├── LoginScreen.kt
            └── 📁register
        └── 📁dashboard
            └── 📁activities
                ├── DashboardActivity.kt
                ├── VerificationActivity.kt
            └── 📁model
                ├── Sensor.kt
                ├── VerificationResult.kt
            └── 📁ui
                └── 📁components
                └── �screen
                    ├── DashboardScreen.kt
                    ├── VerificationScreen.kt
    ├── MainActivity.kt
    └── Platform.android.kt
```

---

### 🏗️ Struktur Arsitektur `app/`

Module `app` mengikuti arsitektur **Clean Architecture** dengan pemisahan yang jelas antara fitur, core logic, dan platform-specific code.

#### 📦 `core/` - Komponen Inti

Berisi komponen-komponen yang digunakan di seluruh aplikasi:

```
core/
└── 📁common/           # Konstanta dan utilitas umum
    └── Constant.kt
└── 📁navigation/       # Navigasi dan intent handling
    ├── ImplicitIntentHelper.kt
    ├── IntentManager.kt
└── 📁utils/           # Utilitas spesifik
    ├── MapUtils.kt
    ├── VaildateCoordinate.kt
```

- **`common/`**: Konstanta dan class yang digunakan secara global
- **`navigation/`**: Logic untuk navigasi antar screen dan handling intent
- **`utils/`**: Fungsi-fungsi helper untuk operasi spesifik (maps, validasi, dll)

#### 🎯 `features/` - Fitur Aplikasi

Berisi semua fitur aplikasi yang diorganisir berdasarkan domain:

```
features/
└── 📁auth/            # Fitur Autentikasi
    └── 📁login/       # Login flow
        ├── 📁activities/    # Activities Android
        │   └── LoginActivity.kt
        └── 📁ui/           # Komponen UI
            ├── 📁components/  # UI components reusable
            │   └── LoginForm.kt
            └── 📁screen/      # Screen utama
                └── LoginScreen.kt
    └── 📁register/     # Registration flow (placeholder)
└── 📁dashboard/       # Fitur Dashboard
    ├── 📁activities/      # Activities Android
    │   ├── DashboardActivity.kt
    │   └── VerificationActivity.kt
    ├── 📁model/          # Data models
    │   ├── Sensor.kt
    │   └── VerificationResult.kt
    └── 📁ui/             # Komponen UI
        ├── 📁components/  # UI components reusable
        └── 📁screen/      # Screen utama
            ├── DashboardScreen.kt
            └── VerificationScreen.kt
```

- **`auth/`**: Semua logic terkait autentikasi (login, register, dll)
- **`dashboard/`**: Fitur dashboard dan verifikasi sensor
- Setiap fitur memiliki struktur konsisten: `activities/`, `model/`, dan `ui/`

#### 📱 Root Level Components

```
app/
├── MainActivity.kt        # Entry point utama aplikasi Android
└── Platform.android.kt   # Implementasi platform-specific
```

- **`MainActivity.kt`**: Activity utama yang mengatur navigation dan lifecycle
- **`Platform.android.kt`**: Implementasi interface untuk platform Android

---

## 📱 Proyek `iosApp` (Xcode Project)

Proyek Xcode standar yang menjadi entry point untuk aplikasi iOS.

```
iosApp/
├── 📁 Configuration/        # Konfigurasi Xcode
├── 📁 iosApp/               # Source code iOS (Swift/Objective-C)
├── 📁 iosApp.xcodeproj/     # File project Xcode
└── 📄 iosApp.xcworkspace/   # Workspace Xcode
```

- Berfungsi sebagai **wrapper** untuk memanggil kode Kotlin dari `app`
- Mengatur konfigurasi build iOS
- Mengelola deployment ke App Store

---

## 🔧 Konfigurasi Gradle

### `gradle/`

```
gradle/
├── 📄 gradle-daemon-jvm.properties  # Konfigurasi JVM untuk Gradle
├── 📄 libs.versions.toml            # Version catalog untuk dependencies
└── 📁 wrapper/                      # Gradle wrapper files
    ├── 📄 gradle-wrapper.jar
    └── 📄 gradle-wrapper.properties
```

- **`libs.versions.toml`**: Centralized dependency management
- **`wrapper/`**: Memastikan konsistensi Gradle version

### File Konfigurasi Utama

- **`build.gradle.kts`**: Build script root project
- **`settings.gradle.kts`**: Konfigurasi module dan repositories
- **`gradle.properties`**: Global properties untuk build
- **`local.properties`**: Konfigurasi lokal (path SDK, signing keys)

---

## 🎯 Alur Kerja Development

### 1. **Core Development** (`core/`)

- Tulis konstanta, utilitas, dan navigation logic di folder `core/`
- Komponen di sini digunakan di seluruh aplikasi
- Fokus pada reusable components

### 2. **Feature Development** (`features/`)

- Buat fitur baru di dalam folder `features/`
- Setiap fitur memiliki struktur konsisten: `activities/`, `model/`, `ui/`
- Implementasi business logic dan UI untuk fitur spesifik

### 3. **Platform Integration**

- `MainActivity.kt` mengatur lifecycle dan navigation
- `Platform.android.kt` untuk platform-specific implementations
- Integrasi dengan Android system components

### 4. **Build & Deploy**

- Android: Build APK/AAB dari Android Studio
- iOS: Build IPA dari Xcode (jika ada module iOS)

---

## 📚 Konsep Penting Kotlin Multiplatform

### **Expect/Actual Pattern**

```kotlin
// commonMain
expect fun getPlatformName(): String

// androidMain
actual fun getPlatformName(): String = "Android"

// iosMain
actual fun getPlatformName(): String = "iOS"
```

### **Compose Multiplatform**

- UI framework yang berjalan di Android & iOS
- Satu codebase UI untuk kedua platform
- Declarative UI seperti React Native

### **Shared Business Logic**

- Network calls, data processing, state management
- Database operations (dengan SQLDelight)
- Business rules dan validation

---

## 🔍 Tips untuk Pemula

1. **Fokus di `core/` dan `features/`** - Ini adalah core aplikasi kamu
2. **Pelajari Compose** - UI framework modern dari Google
3. **Ikuti struktur fitur** - Setiap fitur punya pola konsisten: activities/, model/, ui/
4. **Start Simple** - Mulai dengan fitur sederhana sebelum complex features
5. **Use Version Catalog** - Mudahkan dependency management

---

## 📖 Resource Tambahan

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Android Developer Guide](https://developer.android.com/)
- [Apple Developer Documentation](https://developer.apple.com/documentation/)

---

_Happy Coding! 🚀_
