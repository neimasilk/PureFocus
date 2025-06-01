package com.neimasilk.purefocus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.neimasilk.purefocus.data.PreferencesManager
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {
    
    /**
     * StateFlow untuk focus duration yang dapat diobservasi oleh UI
     */
    val focusDuration: StateFlow<Int> = preferencesManager.focusDuration
    
    /**
     * StateFlow untuk short break duration yang dapat diobservasi oleh UI
     */
    val shortBreakDuration: StateFlow<Int> = preferencesManager.shortBreakDuration
    
    /**
     * StateFlow untuk long break duration yang dapat diobservasi oleh UI
     */
    val longBreakDuration: StateFlow<Int> = preferencesManager.longBreakDuration
    
    /**
     * StateFlow untuk sound notifications yang dapat diobservasi oleh UI
     */
    val enableSoundNotifications: StateFlow<Boolean> = preferencesManager.enableSoundNotifications
    
    /**
     * Update focus duration dan simpan ke preferences
     */
    fun updateFocusDuration(duration: Int) {
        if (duration > 0 && duration <= 180) {
            preferencesManager.updateFocusDuration(duration)
        }
    }
    
    /**
     * Update short break duration dan simpan ke preferences
     */
    fun updateShortBreakDuration(duration: Int) {
        if (duration > 0 && duration <= 60) {
            preferencesManager.updateShortBreakDuration(duration)
        }
    }
    
    /**
     * Update long break duration dan simpan ke preferences
     */
    fun updateLongBreakDuration(duration: Int) {
        if (duration > 0 && duration <= 120) {
            preferencesManager.updateLongBreakDuration(duration)
        }
    }
    
    /**
     * Toggle sound notifications preference
     */
    fun toggleSoundNotifications(enable: Boolean) {
        preferencesManager.updateEnableSoundNotifications(enable)
    }
    
    companion object {
        /**
         * Factory untuk membuat SettingsViewModel dengan dependency injection
         */
        fun factory(preferencesManager: PreferencesManager): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SettingsViewModel(preferencesManager)
                }
            }
        }
    }
}