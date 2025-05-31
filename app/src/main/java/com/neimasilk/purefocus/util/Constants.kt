package com.neimasilk.purefocus.util

object PrefKeys {
    const val PREFERENCES_NAME = "purefocus_preferences"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_FOCUS_DURATION = "focus_duration"
    const val KEY_SHORT_BREAK_DURATION = "short_break_duration"
    const val KEY_LONG_BREAK_DURATION = "long_break_duration"
    const val KEY_TIMER_RUNNING = "timer_running" // Verifikasi apakah masih digunakan
    const val KEY_CURRENT_SESSION_TYPE = "current_session_type" // Verifikasi apakah masih digunakan
    const val KEY_TIME_LEFT_IN_MILLIS = "time_left_in_millis" // Verifikasi apakah masih digunakan
    const val KEY_FOCUS_WRITE_TEXT = "focus_write_text"
    // Tambahkan kunci preferensi lainnya jika teridentifikasi.
}

object PomodoroServiceActions {
    const val ACTION_START_RESUME = "com.neimasilk.purefocus.ACTION_START_RESUME"
    const val ACTION_PAUSE = "com.neimasilk.purefocus.ACTION_PAUSE"
    const val ACTION_RESET = "com.neimasilk.purefocus.ACTION_RESET"
    const val ACTION_SKIP = "com.neimasilk.purefocus.ACTION_SKIP" // Jika akan ada fitur skip
    // Tambahkan action service lainnya jika teridentifikasi.
}

object NotificationConstants {
    const val CHANNEL_ID_POMODORO = "pomodoro_channel"
    const val NOTIFICATION_ID_POMODORO = 1
    const val CHANNEL_ID_SESSION_END = "session_end_channel"
    const val NOTIFICATION_ID_FOCUS_END = 2
    const val NOTIFICATION_ID_BREAK_END = 3
    // Tambahkan konstanta notifikasi lainnya jika teridentifikasi.
}

object DefaultSettings {
    const val DEFAULT_FOCUS_DURATION_MINUTES = 25
    const val DEFAULT_SHORT_BREAK_DURATION_MINUTES = 5
    const val DEFAULT_LONG_BREAK_DURATION_MINUTES = 15

    // Konstanta dari PomodoroTimerViewModel
    const val VM_SHORT_BREAK_DURATION_MINUTES = 5L // Nama diubah untuk menghindari konflik jika ada penggunaan lain
    const val VM_LONG_BREAK_DURATION_MINUTES = 15L // Nama diubah untuk menghindari konflik
    // Tambahkan pengaturan default lainnya jika teridentifikasi.
}