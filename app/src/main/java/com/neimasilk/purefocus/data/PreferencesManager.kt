package com.neimasilk.purefocus.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.neimasilk.purefocus.util.DefaultSettings
import com.neimasilk.purefocus.util.PrefKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager untuk menyimpan dan mengambil preferensi aplikasi.
 * Menggunakan SharedPreferences sebagai penyimpanan data persisten.
 */
class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PrefKeys.PREFERENCES_NAME, Context.MODE_PRIVATE)
    
    // StateFlow untuk focus duration
    private val _focusDuration = MutableStateFlow(getFocusDurationFromPrefs())
    val focusDuration: StateFlow<Int> = _focusDuration.asStateFlow()

    /**
     * Preferensi untuk mode tema (gelap/terang)
     */
    var isDarkMode: Boolean
        get() = sharedPreferences.getBoolean(PrefKeys.KEY_DARK_MODE, false)
        set(value) = sharedPreferences.edit { putBoolean(PrefKeys.KEY_DARK_MODE, value) }

    /**
     * Teks terakhir yang ditulis pengguna
     */
    var lastText: String
        get() = sharedPreferences.getString(PrefKeys.KEY_FOCUS_WRITE_TEXT, "") ?: ""
        set(value) = sharedPreferences.edit { putString(PrefKeys.KEY_FOCUS_WRITE_TEXT, value) }
    
    /**
     * Durasi focus session dalam menit (default 25 menit)
     */
    private fun getFocusDurationFromPrefs(): Int {
        return sharedPreferences.getInt(PrefKeys.KEY_FOCUS_DURATION, DefaultSettings.DEFAULT_FOCUS_DURATION_MINUTES)
    }
    
    /**
     * Update focus duration dan emit nilai baru ke StateFlow
     */
    fun updateFocusDuration(duration: Int) {
        sharedPreferences.edit { putInt(PrefKeys.KEY_FOCUS_DURATION, duration) }
        _focusDuration.value = duration
    }

    companion object {
        // PREFERENCES_NAME dipindahkan ke Constants.kt
        // KEY_DARK_MODE dipindahkan ke Constants.kt
        // KEY_LAST_TEXT diganti dengan PrefKeys.KEY_FOCUS_WRITE_TEXT
        // KEY_FOCUS_DURATION diganti dengan PrefKeys.KEY_FOCUS_DURATION
    }
}