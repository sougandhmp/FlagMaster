package org.smp.domain.model

enum class DifficultyMode(val timerMs: Long, val label: String) {
    EASY(45_000L, "Easy"),
    NORMAL(30_000L, "Normal"),
    HARD(15_000L, "Hard"),
}
