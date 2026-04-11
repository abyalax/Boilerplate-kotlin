# Boilerplate Kotlin Multiplatform Project

This is a Kotlin Multiplatform (KMP) project targeting **Android** and **iOS** using **Compose Multiplatform** for the UI.

## Project Structure

*   **`composeApp`**: A Kotlin Multiplatform module containing the shared logic and UI.
    *   `commonMain`: Shared code for all platforms (Logic + Compose UI).
    *   `androidMain`: Android-specific implementations.
    *   `iosMain`: iOS-specific implementations.
*   **`iosApp`**: A standard Xcode project that serves as the entry point for the iOS application.

```
└── 📁composeApp
    └── 📁build
        └── 📁generated
    └── 📁src
        └── 📁androidMain
            └── 📁kotlin
                └── 📁com
                    └── 📁example
                        └── 📁boilerplate_kotlin
                            ├── MainActivity.kt
                            ├── Platform.android.kt
            └── 📁res
                └── 📁values
                    ├── strings.xml
            ├── AndroidManifest.xml
        └── 📁commonMain
            └── 📁composeResources
                └── 📁drawable
                    ├── compose-multiplatform.xml
            └── 📁kotlin
                └── 📁com
                    └── 📁example
                        └── 📁boilerplate_kotlin
                            ├── App.kt
                            ├── Greeting.kt
                            ├── Platform.kt
        └── 📁commonTest
            └── 📁kotlin
                └── 📁com
                    └── 📁example
                        └── 📁boilerplate_kotlin
                            ├── ComposeAppCommonTest.kt
        └── 📁iosMain
            └── 📁kotlin
                └── 📁com
                    └── 📁example
                        └── 📁boilerplate_kotlin
                            ├── MainViewController.kt
                            ├── Platform.ios.kt
    └── build.gradle.kts
```

## Prerequisites

- **JDK 17 or higher**
- **Android Studio** (latest version recommended)
- **Xcode** (required for iOS development, macOS only)
- **Kotlin Multiplatform Plugin** installed in Android Studio

## Setup and Run

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Boilerplate-kotlin
```

### 2. Run on Android
1.  Open the project in **Android Studio**.
2.  Wait for the Gradle sync to complete.
3.  Select **`composeApp`** in the run configurations dropdown.
4.  Select an Android emulator or a physical device.
5.  Click the **Run** button.

Alternatively, use the terminal:
```bash
./gradlew :composeApp:installDebug
```

### 3. Run on iOS (macOS Only)
#### Via Android Studio:
1.  Select **`iosApp`** in the run configurations dropdown.
2.  Select an iOS simulator.
3.  Click **Run**.

#### Via Xcode:
1.  Navigate to the `iosApp` directory.
2.  Open `iosApp.xcodeproj` in Xcode.
3.  Select a simulator and click the **Play** button.

## Migration Note (AGP 9.0+)
This project is currently using the standard `com.android.application` plugin within the KMP module. If you plan to upgrade to **Android Gradle Plugin 9.0 or higher**, you must:
1.  Extract the Android entry point (`MainActivity`) into a separate `:androidApp` module.
2.  Switch the `:composeApp` module to use the `com.android.kotlin.multiplatform.library` plugin.

## Common Gradle Commands
- `clean`: `./gradlew clean`
- `build`: `./gradlew build`
- `Android Lint`: `./gradlew :composeApp:lint`
