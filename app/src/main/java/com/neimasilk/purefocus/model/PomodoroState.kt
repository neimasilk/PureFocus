package com.neimasilk.purefocus.model

data class PomodoroState(
    val timeLeftInMillis: Long = WORK_DURATION_MILLIS,
    val currentSessionType: SessionType = SessionType.WORK,
    val isTimerRunning: Boolean = false,
    val pomodorosCompletedInCycle: Int = 0
) {
    companion object {
        const val WORK_DURATION_MINUTES = 25L
        const val SHORT_BREAK_DURATION_MINUTES = 5L
        const val LONG_BREAK_DURATION_MINUTES = 15L

        const val WORK_DURATION_MILLIS = WORK_DURATION_MINUTES * 60 * 1000
        const val SHORT_BREAK_DURATION_MILLIS = SHORT_BREAK_DURATION_MINUTES * 60 * 1000
        const val LONG_BREAK_DURATION_MILLIS = LONG_BREAK_DURATION_MINUTES * 60 * 1000
    }
}
