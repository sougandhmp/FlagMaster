# 🇺🇳 FlagMaster

> A feature-rich Android quiz game built with Jetpack Compose, ViewModel, and DataStore, allowing users to test their knowledge of world flags in a time-based challenge format.

---

## 🎯 Features

✅ Schedule a custom challenge time using a time setter interface  
✅ Automatic countdown and challenge initiation  
✅ 15 timed questions (30 seconds each), spaced by 10-second intervals  
✅ Flag image + 4 options per question  
✅ Selected answers styled with meaningful visual feedback  
✅ Persistent state using DataStore: survive app kill and restore at correct question  
✅ Tracks score and displays GAME OVER + SCORE screen  
✅ Unanswered questions auto-marked incorrect

---

## 🧠 Game Flow

1. User schedules a challenge using a digit-based time setter.
2. App starts a pre-countdown sequence 20 seconds before the scheduled time.
3. At start time:
    - 15 flag questions are asked, one at a time.
    - Each question lasts 30 seconds with countdown and flag image.
    - A 10-second feedback interval follows: shows "Correct" or "Wrong".
4. After all 15 questions, the app shows:
    - "GAME OVER"
    - "SCORE: X/15" based on user's answers

---

## 💾 Persistent State

The app uses **DataStore** to persist:
- Scheduled time (`Long`)
- Quiz answers (`List<QuizAnswer>`)

If the user closes or kills the app mid-challenge:
- It resumes automatically from the correct question and shows remaining time.

---

## 🧱 Architecture

- 🌟 **Jetpack Compose** – Declarative, modern UI
- 📦 **ViewModel + StateFlow** – Clean state management
- 💾 **DataStore (Preferences)** – Challenge/Answer persistence
- 🧪 **Coroutines + Flows** – For reactive, ticking timers
- 🛠 **Hilt DI** – Dependency injection

---

## ⏰ UI Screens

| Screen                  | Description                              |
|-------------------------|------------------------------------------|
| Time Setter             | "Flags Challenge" header + editable HH:MM:SS fields + Save button |
| Pre-Start Countdown     | Displays: “CHALLENGE WILL START IN 00:20” |
| Question View           | Flag image, 4 options, countdown timer top-left, feedback ✅ ❌ |
| Game Over Screen        | "GAME OVER" text and final score |

---

## 🏁 Example Assets

- Flag images stored in `res/drawable/`
- Flag filenames use lowercase country code: `us.xml`, `in.xml`, `jp.xml`


