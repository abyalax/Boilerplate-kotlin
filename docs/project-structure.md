# Dokumentasi Struktur Proyek Kotlin Multiplatform

## 📋 Overview

Proyek ini adalah **Kotlin Multiplatform Project** yang menggunakan **Compose Multiplatform** untuk membuat aplikasi mobile yang dapat berjalan di **Android** dan **iOS** dengan satu codebase.

## 🏗️ Struktur Utama Proyek

```
Boilerplate-kotlin/
├── 📁 composeApp/           # Module utama KMP (shared logic & UI)
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

## 📱 Module `composeApp` (Kotlin Multiplatform Module)

Ini adalah module utama yang berisi semua shared logic dan UI yang akan digunakan oleh kedua platform.

### Struktur Lengkap `composeApp/`

```
composeApp/
├── 📁 build/                # Hasil build kompilasi
├── 📁 src/                  # Source code
│   ├── 📁 androidMain/      # Kode khusus Android
│   ├── 📁 commonMain/       # Kode shared untuk semua platform
│   ├── 📁 commonTest/       # Test shared
│   └── 📁 iosMain/          # Kode khusus iOS
└── 📄 build.gradle.kts      # Build script untuk module ini
```

---

### 📂 `src/commonMain/` - Shared Code

Ini adalah folder **paling penting** karena berisi kode yang akan berjalan di Android dan iOS.

```
commonMain/
├── 📁 composeResources/     # Resources untuk Compose UI
│   └── 📁 drawable/         # Gambar & icon
│       └── compose-multiplatform.xml
└── 📁 kotlin/               # Source code Kotlin
    └── 📁 com/
        └── 📁 example/
            └── 📁 boilerplate_kotlin/
                ├── 📄 App.kt        # Aplikasi utama (Compose UI)
                ├── 📄 Greeting.kt   # Contoh komponen UI
                └── 📄 Platform.kt   # Interface untuk platform-specific
```

**Fungsi:**
- **`App.kt`**: Entry point aplikasi dengan Compose UI
- **`Greeting.kt`**: Contoh komponen UI yang reusable
- **`Platform.kt`**: Interface untuk abstraksi platform-specific
- **`composeResources/`**: Resources yang digunakan oleh Compose (gambar, icon, dll)

---

### 🤖 `src/androidMain/` - Android Specific Code

Kode yang hanya berjalan di Android.

```
androidMain/
├── 📁 kotlin/
│   └── 📁 com/
│       └── 📁 example/
│           └── 📁 boilerplate_kotlin/
│               ├── 📄 MainActivity.kt    # Activity utama Android
│               └── 📄 Platform.android.kt # Implementasi platform Android
├── 📁 res/                   # Android resources
│   └── 📁 values/
│       └── 📄 strings.xml    # String resources
└── 📄 AndroidManifest.xml    # Manifest file Android
```

**Fungsi:**
- **`MainActivity.kt`**: Entry point aplikasi Android
- **`Platform.android.kt`**: Implementasi interface Platform untuk Android
- **`AndroidManifest.xml`**: Konfigurasi aplikasi Android
- **`res/`**: Resources khusus Android (strings, layouts, dll)

---

### 🍎 `src/iosMain/` - iOS Specific Code

Kode yang hanya berjalan di iOS.

```
iosMain/
└── 📁 kotlin/
    └── 📁 com/
        └── 📁 example/
            └── 📁 boilerplate_kotlin/
                ├── 📄 MainViewController.kt # View controller untuk iOS
                └── 📄 Platform.ios.kt       # Implementasi platform iOS
```

**Fungsi:**
- **`MainViewController.kt`**: Bridge antara Kotlin dan iOS UIKit
- **`Platform.ios.kt`**: Implementasi interface Platform untuk iOS

---

### 🧪 `src/commonTest/` - Shared Tests

Test cases yang berjalan di semua platform.

```
commonTest/
└── 📁 kotlin/
    └── 📁 com/
        └── 📁 example/
            └── 📁 boilerplate_kotlin/
                └── 📄 ComposeAppCommonTest.kt # Test untuk shared code
```

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

**Fungsi:**
- Berfungsi sebagai **wrapper** untuk memanggil kode Kotlin dari `composeApp`
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

**Fungsi:**
- **`libs.versions.toml`**: Centralized dependency management
- **`wrapper/`**: Memastikan konsistensi Gradle version

### File Konfigurasi Utama

- **`build.gradle.kts`**: Build script root project
- **`settings.gradle.kts`**: Konfigurasi module dan repositories
- **`gradle.properties`**: Global properties untuk build
- **`local.properties`**: Konfigurasi lokal (path SDK, signing keys)

---

## 🎯 Alur Kerja Development

### 1. **Shared Code Development** (`commonMain`)
- Tulis business logic dan UI di sini
- Gunakan Compose Multiplatform untuk UI
- Buat interface untuk platform-specific functionality

### 2. **Platform Implementation** (`androidMain` & `iosMain`)
- Implementasikan interface dari `commonMain`
- Tambahkan platform-specific functionality
- Android: Activities, Services, Android APIs
- iOS: ViewControllers, iOS APIs

### 3. **Testing** (`commonTest`)
- Tulis test untuk shared logic
- Test akan berjalan di semua platform

### 4. **Build & Deploy**
- Android: Build APK/AAB dari Android Studio
- iOS: Build IPA dari Xcode

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

1. **Fokus di `commonMain`** - Ini adalah core aplikasi kamu
2. **Pelajari Compose** - UI framework modern dari Google
3. **Pahami Expect/Actual** - Pattern untuk platform-specific code
4. **Start Simple** - Mulai dengan logic sederhana sebelum complex features
5. **Use Version Catalog** - Mudahkan dependency management

---

## 📖 Resource Tambahan

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Android Developer Guide](https://developer.android.com/)
- [Apple Developer Documentation](https://developer.apple.com/documentation/)

---

*Happy Coding! 🚀*
