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