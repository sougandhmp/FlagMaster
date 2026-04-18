# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
./gradlew :app:installDebug          # Build and install debug APK
./gradlew :app:installRelease        # Build and install release APK
./gradlew clean build                # Full clean build
```

## Tests

```bash
./gradlew test                       # All unit tests
./gradlew :app:testDebug             # App module unit tests only
./gradlew :data:testDebug            # Data module unit tests only
./gradlew connectedAndroidTest       # Instrumented tests (requires connected device/emulator)
```

## Architecture

Three Gradle modules following Clean Architecture:

- **`:domain`** — Pure Kotlin/JVM. Models (`Question`, `Country`, `QuizAnswer`), the `FlagsRepository` interface, and 12 single-responsibility use cases grouped under `answers/`, `challenge/`, and `questions/`.
- **`:data`** — Android library. Implements `FlagsRepository` using Room (question persistence), DataStore (quiz state: `challenge_time` + `quiz_answers` JSON), and an asset data source that seeds the DB from `app/src/main/assets/questions.json` on first launch.
- **`:app`** — Presentation layer. Single `FlagsChallengeViewModel` holding a `MutableStateFlow<FlagsUiState>`. UI events flow through the `FlagsScreenAction` sealed class. Hilt handles DI throughout.

**Data flow:** Compose UI → ViewModel → Use Cases → Repository interface → Room / DataStore / Asset source

**State reconciliation on launch** (in ViewModel):
- Before scheduled time → show countdown
- In progress → calculate current question from elapsed time and resume
- Expired (> 15 × 40 s after start) → auto-clear for next challenge

## Key Files

| Purpose | Path |
|---|---|
| Game logic & state | `app/src/main/java/org/smp/flagmaster/ui/FlagsChallengeViewModel.kt` |
| UI state model | `app/src/main/java/org/smp/flagmaster/ui/FlagsUiState.kt` |
| UI event actions | `app/src/main/java/org/smp/flagmaster/ui/FlagsScreenAction.kt` |
| Repository impl | `data/src/main/java/org/smp/data/repository/FlagsRepositoryImpl.kt` |
| Domain use cases | `domain/src/main/java/org/smp/domain/usecase/` |
| Question seed data | `app/src/main/assets/questions.json` |
| Dependency versions | `gradle/libs.versions.toml` |

## Flag Assets

Flag drawables are XML vector files in `app/src/main/res/drawable/`, named by ISO 3166-1 alpha-2 country code (e.g., `us.xml`, `gb.xml`). They are loaded at runtime via `getIdentifier()` using the country code from the domain model.

## Tech Stack Highlights

- Kotlin 2.3.20, KSP 2.3.6, AGP 9.1.1, Gradle 9.4.1, Java 21
- Jetpack Compose BOM 2026.03.01
- Hilt 2.59.2, Room 2.8.4, DataStore 1.2.1
- Coil 3.4.0 (image loading), Gson 2.13.2 (JSON), Timber 5.0.1 (logging)
- Min SDK 24 (Android 7.0), Target SDK 36 (Android 15)
