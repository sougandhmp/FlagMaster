package org.smp.flagmaster.ui.theme

import androidx.compose.ui.graphics.Color

data class VibrantThemeConfig(
    val mainGradient: List<Color>,
    val cardBackground: Color,
    val accentColor: Color,
    val buttonGradient: List<Color>,
    val optionSelectedBackground: Color,
    val progressTrack: Color,
    val progressIndicator: Color,
    val timerCircleColor: Color,
    val scorePillColor: Color
)

val BlueVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF001F26), Color(0xFF003547), Color(0xFF00BCD4)),
    cardBackground = Color.White.copy(alpha = 0.15f),
    accentColor = Color(0xFF87E4FF),
    buttonGradient = listOf(Color(0xFF00BCD4), Color(0xFF87E4FF)),
    optionSelectedBackground = Color(0xFF00BCD4).copy(alpha = 0.6f),
    progressTrack = Color.White.copy(alpha = 0.1f),
    progressIndicator = Color(0xFF87E4FF),
    timerCircleColor = Color(0xFFFF9800).copy(alpha = 0.2f),
    scorePillColor = Color(0xFFE91E63).copy(alpha = 0.3f)
)

val OrangeVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF2D1600), Color(0xFF4A2800), Color(0xFFFF9800)),
    cardBackground = Color.White.copy(alpha = 0.25f),
    accentColor = Color(0xFFFFB74D),
    buttonGradient = listOf(Color(0xFFFF9800), Color(0xFFFFB74D)),
    optionSelectedBackground = Color(0xFFFF9800).copy(alpha = 0.6f),
    progressTrack = Color.White.copy(alpha = 0.2f),
    progressIndicator = Color(0xFFFFB74D),
    timerCircleColor = Color(0xFFE91E63).copy(alpha = 0.3f),
    scorePillColor = Color(0xFF00BCD4).copy(alpha = 0.4f)
)

val PurpleVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF1A0033), Color(0xFF2D0052), Color(0xFF7B2FBE)),
    cardBackground = Color.White.copy(alpha = 0.12f),
    accentColor = Color(0xFFCE93D8),
    buttonGradient = listOf(Color(0xFF7B2FBE), Color(0xFFCE93D8)),
    optionSelectedBackground = Color(0xFF7B2FBE).copy(alpha = 0.6f),
    progressTrack = Color.White.copy(alpha = 0.1f),
    progressIndicator = Color(0xFFCE93D8),
    timerCircleColor = Color(0xFF00BCD4).copy(alpha = 0.25f),
    scorePillColor = Color(0xFFFF9800).copy(alpha = 0.35f)
)

val GreenVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF001A00), Color(0xFF003300), Color(0xFF00C853)),
    cardBackground = Color.White.copy(alpha = 0.12f),
    accentColor = Color(0xFF69F0AE),
    buttonGradient = listOf(Color(0xFF00C853), Color(0xFF69F0AE)),
    optionSelectedBackground = Color(0xFF00C853).copy(alpha = 0.55f),
    progressTrack = Color.White.copy(alpha = 0.1f),
    progressIndicator = Color(0xFF69F0AE),
    timerCircleColor = Color(0xFFFF9800).copy(alpha = 0.25f),
    scorePillColor = Color(0xFF7B2FBE).copy(alpha = 0.35f)
)

val RoseVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF2D0016), Color(0xFF4A0026), Color(0xFFE91E63)),
    cardBackground = Color.White.copy(alpha = 0.15f),
    accentColor = Color(0xFFF48FB1),
    buttonGradient = listOf(Color(0xFFE91E63), Color(0xFFF48FB1)),
    optionSelectedBackground = Color(0xFFE91E63).copy(alpha = 0.55f),
    progressTrack = Color.White.copy(alpha = 0.1f),
    progressIndicator = Color(0xFFF48FB1),
    timerCircleColor = Color(0xFF00C853).copy(alpha = 0.25f),
    scorePillColor = Color(0xFF00BCD4).copy(alpha = 0.35f)
)

val IndigoVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF0D0033), Color(0xFF1A0066), Color(0xFF3D5AFE)),
    cardBackground = Color.White.copy(alpha = 0.13f),
    accentColor = Color(0xFF82B1FF),
    buttonGradient = listOf(Color(0xFF3D5AFE), Color(0xFF82B1FF)),
    optionSelectedBackground = Color(0xFF3D5AFE).copy(alpha = 0.55f),
    progressTrack = Color.White.copy(alpha = 0.1f),
    progressIndicator = Color(0xFF82B1FF),
    timerCircleColor = Color(0xFFE91E63).copy(alpha = 0.25f),
    scorePillColor = Color(0xFF00C853).copy(alpha = 0.35f)
)

val TealVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF001A1A), Color(0xFF00332E), Color(0xFF00897B)),
    cardBackground = Color.White.copy(alpha = 0.13f),
    accentColor = Color(0xFF80CBC4),
    buttonGradient = listOf(Color(0xFF00897B), Color(0xFF80CBC4)),
    optionSelectedBackground = Color(0xFF00897B).copy(alpha = 0.55f),
    progressTrack = Color.White.copy(alpha = 0.1f),
    progressIndicator = Color(0xFF80CBC4),
    timerCircleColor = Color(0xFFFF9800).copy(alpha = 0.25f),
    scorePillColor = Color(0xFF7B2FBE).copy(alpha = 0.3f)
)

val SunsetVibrantTheme = VibrantThemeConfig(
    mainGradient = listOf(Color(0xFF1A0A00), Color(0xFF8B1A00), Color(0xFFFF5722)),
    cardBackground = Color.White.copy(alpha = 0.15f),
    accentColor = Color(0xFFFFAB91),
    buttonGradient = listOf(Color(0xFFFF5722), Color(0xFFFFAB91)),
    optionSelectedBackground = Color(0xFFFF5722).copy(alpha = 0.55f),
    progressTrack = Color.White.copy(alpha = 0.1f),
    progressIndicator = Color(0xFFFFAB91),
    timerCircleColor = Color(0xFF00C853).copy(alpha = 0.2f),
    scorePillColor = Color(0xFF3D5AFE).copy(alpha = 0.35f)
)

val allVibrantThemes = listOf(
    BlueVibrantTheme,
    OrangeVibrantTheme,
    PurpleVibrantTheme,
    GreenVibrantTheme,
    RoseVibrantTheme,
    IndigoVibrantTheme,
    TealVibrantTheme,
    SunsetVibrantTheme
)
