package com.neimasilk.purefocus.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manager untuk menyimpan dan mengambil preferensi aplikasi.
 * Menggunakan SharedPreferences sebagai penyimpanan data persisten.
 */
class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

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

    companion object {
        private const val PREFERENCES_NAME = "purefocus_preferences"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LAST_TEXT = "last_text"
    }
}