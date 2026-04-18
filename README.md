# 🇺🇳 FlagMaster

> An Android quiz game where you race against the clock to identify world flags. Schedule a challenge, survive a countdown, and answer 15 timed questions — all with persistent state that survives app kills.

---

## 📸 Screenshots

| Time Scheduler | Countdown | Question | Game Over |
|:-:|:-:|:-:|:-:|
| <img src="screenshots/time_schedule.png" width="180"/> | <img src="screenshots/count_down.png" width="180"/> | <img src="screenshots/challenge_screen_1.png" width="180"/> | <img src="screenshots/game_over_score.png" width="180"/> |

---

## ✨ Features

- **Time-scheduled challenge** — Set an exact HH:MM:SS start time; the app counts down and auto-starts
- **15 timed questions** — 30 seconds per question with an animated depleting timer bar
- **Urgency colors** — Timer bar shifts green → yellow → red as time runs low
- **Instant feedback** — Select an answer and move on in 1 second; no need to wait the full 30s
- **Streak tracking** — 🔥 streak chip appears after 2+ consecutive correct answers
- **Flag recognition** — One flag image, four country options per question
- **Haptic feedback** — Tactile response on every answer tap
- **Visual feedback** — Correct/wrong answer highlighting with scale animation
- **Live score** — Header shows your running score throughout the quiz
- **Persistent state** — Powered by DataStore; the quiz resumes at the right question even after an app kill
- **Animated results screen** — Game over screen with animated score counter and percentage ring
- **Unanswered questions** — Auto-marked incorrect when the timer expires

---

## 🎮 Game Flow

```
Schedule time (HH:MM:SS)
        │
        ▼
  Waiting screen
  (shows scheduled time)
        │
        ▼  20 seconds before start
  Countdown (00:20 → 00:00)
        │
        ▼
  Question 1 of 15  ──[30s timer bar]──►  Answer revealed (1–2s)
        │                                          │
        └──────────────────────────────────────────┘
                    × 15 questions
        │
        ▼
   Game Over + Score
```

---

## 🏗 Architecture

The project follows **Clean Architecture** split across three Gradle modules:

```
FlagMaster/
├── app/          # Presentation — Jetpack Compose UI, ViewModels
├── domain/       # Business logic — models, use cases, repository interface (pure JVM)
└── data/         # Infrastructure — Room DB, DataStore, asset loading
```

### Key patterns
- **Single source of truth** — `MutableStateFlow<ScheduleTimeUiState>` in the ViewModel
- **Sealed actions** — `FlagsScreenAction` for type-safe UI events
- **Suspend use cases** — each domain operation is a single-responsibility suspend class
- **IO-dispatched repository** — all DB and asset I/O runs on `Dispatchers.IO` via `withContext`
- **Coroutine-based timer** — countdowns use a `suspend fun runCountdown()` + coroutine `Job` instead of `CountDownTimer`, keeping the ViewModel Android-framework-free and unit-testable

---

## 🛠 Tech Stack

| Layer | Library | Version |
|---|---|---|
| UI | Jetpack Compose BOM | 2026.03.01 |
| Navigation | Navigation Compose | 2.9.7 |
| State | ViewModel + StateFlow | Lifecycle 2.10.0 |
| DI | Hilt | 2.59.2 |
| Database | Room | 2.8.4 |
| Persistence | DataStore Preferences | 1.2.1 |
| Image loading | Coil | 3.4.0 |
| Serialization | Gson | 2.13.2 |
| Logging | Timber | 5.0.1 |
| Testing | JUnit Jupiter | 6.0.3 |
| Build | AGP 9.1.1 · Gradle 9.4.1 · Kotlin 2.3.20 · KSP 2.3.6 | — |
| Min SDK | Android 7.0 (API 24) | — |
| Target SDK | Android 15 (API 36) | — |

---

## 💾 Persistent State

DataStore stores two keys:

| Key | Type | Purpose |
|---|---|---|
| `challenge_time` | `Long` | Epoch millis of the scheduled start |
| `quiz_answers` | `String` (JSON) | `List<QuizAnswer>` — answers so far |

On every launch the app reconciles current time against the saved challenge time:

- **Before start** → shows the scheduled time
- **In progress** → calculates the current question index from elapsed time and resumes
- **Expired** (> 15 × 40s after start) → clears state automatically

---

## 🔄 Data Sync

Question data is served from three sources in priority order:

```
Firebase RTDB  ──(online)──►  Room cache  ──►  App (live questions)
                                  │
               ──(offline)────────┘
                                  │
               ──(no cache)───────┴──►  Bundled assets (questions.json)
```

### Sources

| Source | Class | When used |
|---|---|---|
| Firebase Realtime Database | `FirebaseDataSource` | Network available; fetches `/questions` node |
| Room (cache) | `FlagsRepositoryImpl` | Firebase unreachable but DB has rows |
| Bundled assets | `AssetDataSource` | No network and empty DB (first install / no cache) |

### Background sync

`SyncViewModel` (scoped to `FlagsNavigation`) owns all sync lifecycle:

| Trigger | Method called | When |
|---|---|---|
| App launch | `schedulePeriodic()` + `scheduleImmediateSync()` | `SyncViewModel.init` |
| App goes to background (`ON_STOP`) | `pausePeriodicSync()` | `DisposableEffect` lifecycle observer |
| App returns to foreground (`ON_START`) | `resumePeriodicSync()` | `DisposableEffect` lifecycle observer |

`FirebaseBackgroundSyncManager` translates these into WorkManager tasks:

| Method | Type | Constraint | Policy |
|---|---|---|---|
| `schedulePeriodic()` | Periodic, every 24 h | Network connected | `KEEP` existing |
| `scheduleImmediateSync()` | One-time | Network connected | `REPLACE` existing |
| `pausePeriodicSync()` | — | — | Cancels periodic work |
| `resumePeriodicSync()` | — | — | Re-registers periodic work |

The `FirebaseSyncWorker` is a `@HiltWorker` / `CoroutineWorker` that calls `repository.seedQuestions()` and retries automatically on failure (exponential backoff).

### Network awareness

`NetworkStateManager` (injected via `SyncModule`) checks `ConnectivityManager` for `NET_CAPABILITY_INTERNET` + `NET_CAPABILITY_VALIDATED` before any Firebase call, avoiding unnecessary requests on captive-portal or metered connections.

---

## 🗂 Project Structure

```
app/src/main/java/org/smp/flagmaster/
├── ui/
│   ├── FlagsChallengeViewModel.kt   # Quiz game logic & state
│   ├── FlagsNavigation.kt           # NavHost + lifecycle sync observer
│   ├── FlagsUiState.kt              # State, enums, sealed classes
│   ├── FlagsScreenAction.kt         # User action sealed class
│   ├── sync/
│   │   └── SyncViewModel.kt         # Firebase sync lifecycle (seed, periodic, pause/resume)
│   ├── mapper/
│   │   ├── TimeSchedulerErrorMapper.kt
│   │   └── ChallengeTimeMapper.kt       # Digit list → Calendar (UI layer, injectable)
│   └── components/
│       ├── StartChallengeScreen.kt  # Entry screen / time scheduler
│       ├── TimerScheduleView.kt     # HH:MM:SS digit input
│       ├── ChallengeScheduledView.kt
│       ├── CountDownView.kt
│       ├── QuestionScreen.kt        # Timer bar, streak chip, answer options
│       ├── ChallengeView.kt         # Flag + answer grid with haptic feedback
│       ├── ChallengeCompleteView.kt # Mid-challenge completion view
│       ├── GameOverScreen.kt        # Animated results screen
│       ├── StatsScreen.kt           # Per-question answer review
│       └── ...
app/src/main/assets/
├── questions.json                   # Seed data loaded on first launch
└── flags/                           # 255 SVG flag files (ISO 3166-1 alpha-2)

domain/src/main/java/org/smp/domain/
├── model/          # Question, Country, QuizAnswer
├── repository/     # FlagsRepository interface
└── usecase/        # One class per operation (answers/, challenge/, questions/)

data/src/main/java/org/smp/data/
├── database/           # Room DB, DAOs, entities
├── datastore/          # DataStore read/write
├── asset_data_source/  # questions.json loader
├── firebase/           # FirebaseDataSource (RTDB)
├── sync/               # FirebaseBackgroundSyncManager, NetworkStateManagerImpl
└── repository/         # FlagsRepositoryImpl (orchestrates all sources)
```

---

## 🚀 Getting Started

1. **Clone the repo**
   ```bash
   git clone https://github.com/sougandhmp/FlagMaster.git
   cd FlagMaster
   ```

2. **Open in Android Studio** (Meerkat or later recommended)

3. **Run on a device or emulator** targeting API 24+
   ```bash
   ./gradlew :app:installDebug
   ```

> The app seeds its question data from `app/src/main/assets/questions.json` on first launch — no network required.

---

## 🖼 Flag Assets

255 flag SVGs live in `app/src/main/assets/flags/`, named by lowercase ISO 3166-1 alpha-2 country code:

```
ad.svg   ae.svg   af.svg   ...   us.svg   gb.svg   fr.svg   ...
```

They are loaded at runtime by Coil using a `SvgDecoder` configured in `FlagsApplication`:

```kotlin
.data("file:///android_asset/flags/${countryCode.lowercase()}.svg")
```
