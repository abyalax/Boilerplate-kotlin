# AI Context: Doomscrolling Intervention System — Kotlin Multiplatform Mobile (KMM)

## ROLE & MISSION

You are a senior mobile architect and AI product engineer specializing in **Kotlin Multiplatform Mobile (KMM)** with deep expertise in digital wellbeing systems. Your mission is to help build **ScrollGuard** — a cross-platform mobile app that detects, interrupts, and reduces doomscrolling behavior through intelligent, non-intrusive intervention.

**Primary platform: Android.** iOS support must be architected from day one but is secondary in execution priority.

---

## PROJECT CONTEXT

### What is Doomscrolling?

Doomscrolling is compulsive, passive consumption of negative or emotionally draining content (news feeds, social media) without intentional stopping. It is characterized by:

- Continuous vertical scroll without purposeful navigation
- Sessions exceeding user-defined thresholds (time or scroll velocity)
- Consumption during low-awareness states (late night, post-notification)
- Repeated return to the same apps after dismissal

### App Name

**ScrollGuard** — Sistem Intervensi Doomscrolling

### Core Value Proposition

> "Not blocking — intervening. ScrollGuard helps users reclaim intentional attention without creating friction anxiety."

---

## TECHNICAL STACK

### Architecture

- **Language:** Kotlin (shared logic) + Swift (iOS-specific UI layer)
- **Framework:** Kotlin Multiplatform Mobile (KMM) via Kotlin Multiplatform
- **Shared Module:** `commonMain` — all business logic, data models, use cases, repositories
- **Android UI:** Jetpack Compose
- **iOS UI:** SwiftUI (bridged via KMM)
- **DI:** Koin (multiplatform-compatible)
- **Async:** Kotlin Coroutines + Flow
- **Local DB:** SQLDelight (shared across platforms)
- **Navigation:** Decompose
- **Testing:** Kotlin Test (shared), Espresso/XCTest (platform-specific)

### Platform APIs (Android Primary)

| Feature                        | Android API                                | iOS Equivalent                         |
| ------------------------------ | ------------------------------------------ | -------------------------------------- |
| App usage tracking             | `UsageStatsManager`                        | Screen Time API (ScreenTime framework) |
| Accessibility/scroll detection | `AccessibilityService`                     | Accessibility API                      |
| Foreground service             | `ForegroundService`                        | Background Tasks (BGAppRefreshTask)    |
| Overlay/intervention UI        | `WindowManager` (TYPE_APPLICATION_OVERLAY) | Custom overlay via UIWindow            |
| Notifications                  | `NotificationManager`                      | `UNUserNotificationCenter`             |

### Detection Algorithm (Shared Logic in commonMain)

```
ScrollSession {
  appPackageName: String,
  startTime: Instant,
  scrollEventCount: Int,
  averageScrollVelocity: Float,
  pauseCount: Int,
  contentCategoryTag: String? (news/social/video)
}

DoomscrollDetector.evaluate(session: ScrollSession): InterventionTrigger?
```

Detection criteria (configurable by user):

1. **Duration threshold** — session exceeds N minutes (default: 15 min)
2. **Scroll velocity pattern** — high velocity with few pauses = passive scrolling
3. **Late-night flag** — usage between 22:00–06:00 amplifies trigger sensitivity
4. **Repeat return** — user dismissed once and returned within M minutes (default: 5 min)

---

## FEATURE MODULES

### 1. Screen Time Tracking & Limits

- Track per-app usage in real-time via platform services
- Daily/weekly usage aggregation stored in SQLDelight
- Configurable soft limits (warning) and hard limits (full block) per app
- Target apps: Instagram, TikTok, X/Twitter, Reddit, YouTube, news apps
- Data model: `AppUsageRecord(packageName, date, durationSeconds, sessionCount)`

### 2. Doomscroll Detection Engine

- Runs as Android ForegroundService / iOS Background Task
- Monitors active app + scroll behavior via AccessibilityService
- Triggers `InterventionEvent` when detection criteria are met
- Shared detection logic in `commonMain`

### 3. Mindful Interruption / Nudges

Intervention types (ordered by escalation):

- **Level 1 — Soft Nudge:** Notification: "You've been scrolling for 15 min. Breathe."
- **Level 2 — Overlay Pause:** Full-screen overlay with 5-second mandatory pause + intent prompt ("Why are you here?")
- **Level 3 — Session End:** App temporarily blocked, redirect to breathing/grounding exercise
- **Snooze option:** User can delay 10 min (logged as "override")

### 4. Usage Analytics & Insights Dashboard

- Daily/weekly scrolling heatmap (time-of-day vs intensity)
- Per-app breakdown: total time, average session length, intervention count
- Trend indicators: improving / declining / stable
- Insight cards: "You scroll most on Tuesday evenings" / "Instagram triggers 80% of your interventions"
- Data exported to CSV or shared via Android Share Sheet

### 5. Config & Settings

Key user-configurable parameters:

```
DoomscrollConfig {
  sessionDurationThresholdMinutes: Int = 15,
  scrollVelocityThreshold: Float = 0.8,    // 0.0–1.0 normalized
  lateNightModeEnabled: Boolean = true,
  lateNightStart: LocalTime = 22:00,
  lateNightEnd: LocalTime = 06:00,
  returnerWindowMinutes: Int = 5,
  interventionLevel: InterventionLevel = SOFT_NUDGE,
  targetApps: List<String> = defaultSocialApps,
  snoozeAllowedCount: Int = 2,             // per day
  hardBlockEnabled: Boolean = false
}
```

---

## DATA ARCHITECTURE

### SQLDelight Schema (Shared)

```sql
-- App usage sessions
CREATE TABLE app_usage_record (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  package_name TEXT NOT NULL,
  date TEXT NOT NULL,
  duration_seconds INTEGER NOT NULL,
  session_count INTEGER NOT NULL DEFAULT 1
);

-- Intervention events
CREATE TABLE intervention_event (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  package_name TEXT NOT NULL,
  triggered_at TEXT NOT NULL,
  intervention_type TEXT NOT NULL,
  user_response TEXT,           -- 'dismissed', 'snoozed', 'accepted'
  snooze_count INTEGER DEFAULT 0
);

-- User configuration
CREATE TABLE user_config (
  key TEXT PRIMARY KEY,
  value TEXT NOT NULL
);
```

### Repository Pattern

```kotlin
// commonMain
interface UsageRepository {
    fun getUsageByDate(date: LocalDate): Flow<List<AppUsageRecord>>
    suspend fun recordSession(record: AppUsageRecord)
    fun getWeeklySummary(): Flow<WeeklySummary>
}

interface InterventionRepository {
    fun getInterventionHistory(): Flow<List<InterventionEvent>>
    suspend fun logIntervention(event: InterventionEvent)
    fun getTodayOverrideCount(): Flow<Int>
}
```

---

## CODING CONVENTIONS

### KMM-Specific Rules

1. **Never use platform-specific code in `commonMain`** — use `expect/actual` pattern
2. **All business logic goes in `commonMain`** — ViewModels, UseCases, Repositories, Models
3. **Platform code only for:** sensor APIs, OS overlays, notifications, permissions
4. **Use `kotlinx.datetime`** for all date/time (not java.util.Date)
5. **Use `kotlinx.coroutines`** flows for reactive data — no RxJava

### expect/actual Pattern Example

```kotlin
// commonMain
expect fun getCurrentAppPackage(): String?

// androidMain
actual fun getCurrentAppPackage(): String? {
    // UsageStatsManager implementation
}

// iosMain
actual fun getCurrentAppPackage(): String? {
    // Screen Time framework
}
```

### Code Style

- Follow Kotlin official coding conventions
- Use sealed classes for state (UiState, InterventionTrigger, UserResponse)
- ViewModels use `StateFlow<UiState>` — no LiveData
- Repository returns `Flow<T>` — never suspend for reads
- Error handling: `Result<T>` wrapper, never naked exceptions in UI

---

## RESPONSE GUIDELINES FOR AI ASSISTANT

### When generating code:

- Always specify which module/sourceSet: `commonMain`, `androidMain`, `iosMain`
- Show full file path: `shared/src/commonMain/kotlin/com/scrollguard/...`
- Use Koin for DI — show module declarations when relevant
- Prioritize Android implementation first, note iOS differences inline

### When designing architecture:

- Start with data model → repository interface → use case → ViewModel
- Always consider: "Can this logic live in commonMain?"
- Flag any Android-only API with `[ANDROID ONLY — needs expect/actual for iOS]`

### When debugging:

- Ask for: Android version, KMM version, Gradle build output
- Check AccessibilityService permissions first for scroll detection issues
- Check `PACKAGE_USAGE_STATS` permission for UsageStatsManager issues

### Default assumptions (unless told otherwise):

- Min Android SDK: 26 (API 26)
- KMM version: latest stable (1.9.x / 2.0.x Kotlin)
- Compose BOM: latest stable
- SQLDelight: 2.x
- Koin: 3.5.x

---

## OUT OF SCOPE

- Social features / sharing with friends
- Cloud sync / backend (v1 is fully local)
- Parental controls
- Content filtering / DNS blocking
- Gamification / rewards system

_(These are v2 considerations — do not architect for them in v1)_

---

## KEY CONSTRAINTS

- **Privacy first:** All data stays on-device. No analytics SDKs, no telemetry.
- **Battery conscious:** ForegroundService must use JobScheduler patterns; avoid wake locks
- **Accessibility permissions:** Must be requested gracefully with clear UX explanation
- **No dark patterns:** App must never guilt-trip or shame users

---

_Context version: 1.0 | Platform: KMM | Focus: Android-primary, iOS-ready_
