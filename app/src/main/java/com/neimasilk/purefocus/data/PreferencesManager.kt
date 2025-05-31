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
    
    // StateFlow untuk sound notifications
    private val _enableSoundNotifications = MutableStateFlow(getEnableSoundNotificationsFromPrefs())
    val enableSoundNotifications: StateFlow<Boolean> = _enableSoundNotifications.asStateFlow()

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
     * Menyimpan teks Focus Write ke SharedPreferences
     */
    fun saveFocusWriteText(text: String) {
        sharedPreferences.edit { putString(PrefKeys.KEY_FOCUS_WRITE_TEXT, text) }
    }
    
    /**
     * Mengambil teks Focus Write dari SharedPreferences sebagai Flow
     */
    fun getFocusWriteText(): kotlinx.coroutines.flow.Flow<String> {
        return kotlinx.coroutines.flow.flow {
            emit(sharedPreferences.getString(PrefKeys.KEY_FOCUS_WRITE_TEXT, "") ?: "")
        }
    }
    
    /**
     * Menghapus teks Focus Write dari SharedPreferences
     */
    fun clearFocusWriteText() {
        sharedPreferences.edit { remove(PrefKeys.KEY_FOCUS_WRITE_TEXT) }
    }
    
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
    
    /**
     * Mengambil preferensi sound notifications dari SharedPreferences
     */
    private fun getEnableSoundNotificationsFromPrefs(): Boolean {
        return sharedPreferences.getBoolean(PrefKeys.KEY_ENABLE_SOUND_NOTIFICATIONS, true)
    }
    
    /**
     * Update sound notifications preference dan emit nilai baru ke StateFlow
     */
    fun updateEnableSoundNotifications(enable: Boolean) {
        sharedPreferences.edit { putBoolean(PrefKeys.KEY_ENABLE_SOUND_NOTIFICATIONS, enable) }
        _enableSoundNotifications.value = enable
    }

    /**
     * Menyimpan state service untuk persistence
     */
    fun saveServiceState(
        timeLeftInMillis: Long,
        currentSessionType: String,
        isRunning: Boolean,
        pomodorosCompletedInCycle: Int
    ) {
        sharedPreferences.edit {
            putLong(PrefKeys.KEY_TIME_LEFT_IN_MILLIS, timeLeftInMillis)
            putString(PrefKeys.KEY_CURRENT_SESSION_TYPE, currentSessionType)
            putBoolean(PrefKeys.KEY_TIMER_RUNNING, isRunning)
            putInt(PrefKeys.KEY_POMODOROS_COMPLETED_IN_CYCLE, pomodorosCompletedInCycle)
            putLong(PrefKeys.KEY_SERVICE_LAST_SAVE_TIMESTAMP, System.currentTimeMillis())
        }
    }

    /**
     * Mengambil state service yang tersimpan
     */
    fun getSavedServiceState(): ServiceState? {
        val timestamp = sharedPreferences.getLong(PrefKeys.KEY_SERVICE_LAST_SAVE_TIMESTAMP, 0L)
        
        // Jika tidak ada timestamp atau timestamp terlalu lama (lebih dari 1 jam), abaikan state
        if (timestamp == 0L || System.currentTimeMillis() - timestamp > 3600000L) {
            return null
        }
        
        return ServiceState(
            timeLeftInMillis = sharedPreferences.getLong(PrefKeys.KEY_TIME_LEFT_IN_MILLIS, 0L),
            currentSessionType = sharedPreferences.getString(PrefKeys.KEY_CURRENT_SESSION_TYPE, "WORK") ?: "WORK",
            isRunning = sharedPreferences.getBoolean(PrefKeys.KEY_TIMER_RUNNING, false),
            pomodorosCompletedInCycle = sharedPreferences.getInt(PrefKeys.KEY_POMODOROS_COMPLETED_IN_CYCLE, 0)
        )
    }

    /**
     * Menghapus state service yang tersimpan
     */
    fun clearSavedServiceState() {
        sharedPreferences.edit {
            remove(PrefKeys.KEY_TIME_LEFT_IN_MILLIS)
            remove(PrefKeys.KEY_CURRENT_SESSION_TYPE)
            remove(PrefKeys.KEY_TIMER_RUNNING)
            remove(PrefKeys.KEY_POMODOROS_COMPLETED_IN_CYCLE)
            remove(PrefKeys.KEY_SERVICE_LAST_SAVE_TIMESTAMP)
        }
    }

    /**
     * Data class untuk menyimpan state service
     */
    data class ServiceState(
        val timeLeftInMillis: Long,
        val currentSessionType: String,
        val isRunning: Boolean,
        val pomodorosCompletedInCycle: Int
    )

    companion object {
        // PREFERENCES_NAME dipindahkan ke Constants.kt
        // KEY_DARK_MODE dipindahkan ke Constants.kt
        // KEY_LAST_TEXT diganti dengan PrefKeys.KEY_FOCUS_WRITE_TEXT
        // KEY_FOCUS_DURATION diganti dengan PrefKeys.KEY_FOCUS_DURATION
    }
}