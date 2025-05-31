package com.neimasilk.purefocus.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager untuk menyimpan dan mengambil preferensi aplikasi.
 * Menggunakan SharedPreferences sebagai penyimpanan data persisten.
 */
class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    
    // StateFlow untuk focus duration
    private val _focusDuration = MutableStateFlow(getFocusDurationFromPrefs())
    val focusDuration: StateFlow<Int> = _focusDuration.asStateFlow()

    /**
     * Preferensi untuk mode tema (gelap/terang)
     */
    var isDarkMode: Boolean
        get() = sharedPreferences.getBoolean(KEY_DARK_MODE, false)
        set(value) = sharedPreferences.edit { putBoolean(KEY_DARK_MODE, value) }

    /**
     * Teks terakhir yang ditulis pengguna
     */
    var lastText: String
        get() = sharedPreferences.getString(KEY_LAST_TEXT, "") ?: ""
        set(value) = sharedPreferences.edit { putString(KEY_LAST_TEXT, value) }
    
    /**
     * Durasi focus session dalam menit (default 25 menit)
     */
    private fun getFocusDurationFromPrefs(): Int {
        return sharedPreferences.getInt(KEY_FOCUS_DURATION, 25)
    }
    
    /**
     * Update focus duration dan emit nilai baru ke StateFlow
     */
    fun updateFocusDuration(duration: Int) {
        sharedPreferences.edit { putInt(KEY_FOCUS_DURATION, duration) }
        _focusDuration.value = duration
    }

    companion object {
        private const val PREFERENCES_NAME = "purefocus_preferences"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LAST_TEXT = "last_text"
        private const val KEY_FOCUS_DURATION = "focus_duration"
    }
}