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

## Module Structure

Six Gradle modules following Clean Architecture + feature-based modularization:

- **`:domain`** — Pure Kotlin/JVM. Models (`Question`, `Country`, `QuizAnswer`, `AuthUser`, `UserStats`, `DifficultyMode`), repository interfaces (`FlagsRepository`, `AuthRepository`, `LeaderboardRepository`), and use cases grouped under `answers/`, `challenge/`, `questions/`, `auth/`, and `leaderboard/`.
- **`:data`** — Android library. Implements repository interfaces using Room (question persistence), DataStore (quiz state), Firebase Auth (`FirebaseAuthRepository`), Firebase Firestore (`FirebaseLeaderboardRepository`), and an asset data source that seeds the DB from `questions.json` on first launch.
- **`:core:ui`** — Shared UI components and the app-wide `VibrantTheme`. Contains `ConfettiShower`, `Shimmer`, `VibrantBackground`.
- **`:feature:flags`** — Quiz gameplay. `FlagsChallengeViewModel` owns game state. `SyncViewModel` (scoped to `FlagsNavigation`) manages Firebase sync lifecycle — seeds on launch, schedules periodic 24 h sync, pauses/resumes with app lifecycle. UI events via `FlagsScreenAction` sealed class.
- **`:feature:auth`** — Login/sign-up screens. `AuthViewModel` with Google Sign-In (`CredentialManagerGoogleAuthProvider`) and email/password flows.
- **`:feature:leaderboard`** — Leaderboard screen backed by Firestore via `ObserveTopPlayersUseCase`.
- **`:feature:profile`** — Profile setup and display screens.
- **`:app`** — Shell. `FlagsNavigation` wires all feature nav graphs. `FlagsApplication` registers Coil's `SvgDecoder`.

**Data flow:** Compose UI → ViewModel → Use Cases → Repository interface → Room / DataStore / Firebase

**Quiz state reconciliation on launch** (in `FlagsChallengeViewModel`):
- Before scheduled time → show countdown
- In progress → calculate current question from elapsed time and resume
- Expired (> 15 × 40 s after start) → auto-clear for next challenge

## Key Files

| Purpose                 | Path                                                                                    |
|-------------------------|-----------------------------------------------------------------------------------------|
| Game logic & state      | `feature/flags/src/main/java/org/smp/feature/flags/FlagsChallengeViewModel.kt`          |
| Sync lifecycle          | `feature/flags/src/main/java/org/smp/feature/flags/sync/SyncViewModel.kt`               |
| Flags screen            | `feature/flags/src/main/java/org/smp/feature/flags/FlagsScreen.kt`                      |
| UI state model          | `feature/flags/src/main/java/org/smp/feature/flags/FlagsUiState.kt`                     |
| UI event actions        | `feature/flags/src/main/java/org/smp/feature/flags/FlagsScreenAction.kt`                |
| Auth ViewModel          | `feature/auth/src/main/java/org/smp/feature/auth/AuthViewModel.kt`                      |
| Leaderboard ViewModel   | `feature/leaderboard/src/main/java/org/smp/feature/leaderboard/LeaderboardViewModel.kt` |
| App navigation          | `app/src/main/java/org/smp/flagmaster/FlagsNavigation.kt`                               |
| Repository impl         | `data/src/main/java/org/smp/data/repository/FlagsRepositoryImpl.kt`                     |
| Firebase auth           | `data/src/main/java/org/smp/data/auth/FirebaseAuthRepository.kt`                        |
| Firebase leaderboard    | `data/src/main/java/org/smp/data/repository/FirebaseLeaderboardRepository.kt`           |
| Background sync manager | `data/src/main/java/org/smp/data/firebase/FirebaseDataSource.kt`                        |
| Shared theme            | `core/ui/src/main/java/org/smp/core/ui/VibrantTheme.kt`                                 |
| Domain use cases        | `domain/src/main/java/org/smp/domain/usecase/`                                          |
| Question seed data      | `app/src/main/assets/questions.json`                                                    |
| Dependency versions     | `gradle/libs.versions.toml`                                                             |

## Flag Assets

255 flag SVGs live in `app/src/main/assets/flags/`, named by lowercase ISO 3166-1 alpha-2 country code (e.g., `us.svg`, `gb.svg`). Loaded at runtime by Coil via `file:///android_asset/flags/<code>.svg` using a `SvgDecoder` registered in `FlagsApplication`.

## Tech Stack

- Kotlin 2.3.20, KSP 2.3.6, AGP 9.1.1, Gradle 9.4.1, Java 21
- Jetpack Compose BOM 2026.03.01
- Hilt 2.59.2, Room 2.8.4, DataStore 1.2.1
- Firebase Auth + Firestore (auth and leaderboard)
- Coil 3.4.0 (SVG image loading), Gson 2.13.2 (JSON), Timber 5.0.1 (logging)
- Min SDK 24 (Android 7.0), Target SDK 36 (Android 15)

## Coding Standards

- Kotlin only — no Java.
- Jetpack Compose only for UI; stateless composables with state hoisting.
- Use `collectAsStateWithLifecycle` for Flow in UI.
- Business logic in UseCases, not ViewModels.
- All UI state classes must be immutable (`data class` with `val` fields).
- Prefer `val` over `var`.
- Add dependency versions via `gradle/libs.versions.toml` only.
- All new features must include unit tests for UseCases and ViewModels.
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
