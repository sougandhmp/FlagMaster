package org.smp.feature.flags

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(resId: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener { it.release() }
        } catch (e: Exception) {
            // Log error if needed
        }
    }

    fun playCorrect() {
        // Placeholder for R.raw.correct_answer
        // playSound(R.raw.correct_answer)
    }

    fun playWrong() {
        // Placeholder for R.raw.wrong_answer
        // playSound(R.raw.wrong_answer)
    }

    fun playConfetti() {
        // Placeholder for R.raw.confetti_pop
        // playSound(R.raw.confetti_pop)
    }
}
