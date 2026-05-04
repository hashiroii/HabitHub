# HabitHub

A modern Android habit tracker built entirely with Jetpack Compose. Track daily habits, visualise your progress with a GitHub-style contribution grid, and stay on track with per-habit reminders.

## Screenshots

| Home | Add Habit | Edit / Calendar | Reminders | Settings (Dark) | Settings (Light) |
|------|-----------|-----------------|-----------|-----------------|------------------|
| ![Home](screenshots/Home%20Page.png) | ![Add](screenshots/Add%20Screen.png) | ![Edit](screenshots/Edit%20Screen.png) | ![Reminders](screenshots/Reminder%20Screen.png) | ![Settings Dark](screenshots/Settings%20Screen%20Dark.png) | ![Settings Light](screenshots/Settings%20Screen%20Light.png) |

## Features

- **Habit tracking** — create habits with a custom icon and colour, log completions with a single tap, and set a daily repetition goal (e.g. "drink water 8×/day")
- **Contribution grid** — a full-year GitHub-style heat-map per habit showing activity intensity at a glance
- **Streak counter** — consecutive-day streak displayed on the home card and habit detail screen
- **Calendar view** — monthly calendar on the edit screen showing exactly which days a habit was completed
- **Per-habit reminders** — schedule multiple time-based alarms per habit, choose which days of the week each alarm fires
- **Theme switching** — System default / Light / Dark via a bottom-sheet picker, persisted with DataStore
- **Localisation** — English and Russian (strings split per feature module)
- **Dev / Prod flavours** — separate `applicationId` suffix and `BuildConfig` fields for staging vs. production builds

## Architecture

The project follows **Clean Architecture** with a **multi-module** structure and a strict **MVI** (Model-View-Intent) pattern inside each feature.

```
HabitHub/
├── app/                          # Application entry point, NavHost, DI graph root
├── core/
│   ├── data/                     # Room DB, DAOs, entity mappers, DataStore, repository impls
│   ├── domain/                   # Models, repository interfaces, use cases
│   ├── ui/                       # Shared Compose components (ContributionGrid, HabitActivityCard, HabitIcons)
│   └── designsystem/             # Material 3 theme, colour tokens, typography, reusable components
└── feature/
    ├── home/                     # Home screen (habit list + summary), Settings screen
    ├── habit/                    # Add Habit / Edit Habit screens
    ├── reminders/                # Per-habit reminder management
    └── settings/                 # Settings feature module
```

Each feature module exposes only a `*Navigation.kt` entry point; internals are package-private. Data flows upward through use cases; UI events flow downward as sealed `Intent` classes.

## Tech Stack

| Layer | Library / Tool |
|-------|----------------|
| UI | Jetpack Compose, Material 3 |
| State management | `ViewModel` + `StateFlow`, MVI pattern |
| Navigation | Navigation Compose (type-safe, deep-link ready) |
| Dependency injection | Hilt (Dagger) + KSP |
| Database | Room 2.7 with Flow-based reactive queries |
| Preferences | Jetpack DataStore (Preferences) |
| Alarms | `AlarmManager` exact alarms (`USE_EXACT_ALARM`) |
| Async | Kotlin Coroutines + Flow (`flowOn(Dispatchers.Default)` for heavy grid work) |
| Build | Gradle with Version Catalog (`libs.versions.toml`), KSP |
| Testing | JUnit 4, MockK, Turbine, Espresso, Compose UI Test |

## Requirements

- Android **8.1+ (API 27)**
- Compiled against SDK **36**

## Getting Started

1. Clone the repository
2. Open in Android Studio Ladybug or newer
3. Select the `devDebug` or `prodDebug` build variant
4. Run on a device or emulator

No API keys or external services are required — the app is fully local.

## Project Highlights

- **Recomposition-free grid**: `ContributionGrid` uses `Canvas` + `remember`-cached data to avoid per-frame allocation, keeping the main thread idle while 365 cells are rendered
- **Startup flicker prevention**: `AppPreferencesRepository.getInitialThemeBlocking()` reads the saved theme synchronously once before the first frame so the app never flashes the wrong theme
- **Exact alarm scheduling**: `ReminderScheduler` requests `USE_EXACT_ALARM` permission (Android 13+) and falls back gracefully, firing a `BroadcastReceiver` that posts a styled notification per habit
- **Module isolation**: feature modules have no direct dependencies on each other; cross-feature navigation uses deep-link URIs (`habithub://habit/{id}`)