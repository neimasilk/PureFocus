package com.neimasilk.purefocus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neimasilk.purefocus.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class yang merepresentasikan state UI utama aplikasi
 */
data class MainUiState(
    val isDarkMode: Boolean = false,
    val text: String = ""
)

/**
 * ViewModel utama untuk aplikasi PureFocus.
 * Mengelola state UI dan interaksi dengan data layer.
 */
class MainViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {
    
    // State UI yang diobservasi oleh UI
    private val _uiState = MutableStateFlow(MainUiState(
        isDarkMode = preferencesManager.isDarkMode,
        text = preferencesManager.lastText
    ))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    /**
     * Toggle antara mode gelap dan terang
     */
    fun toggleTheme() {
        _uiState.update { currentState ->
            val newDarkMode = !currentState.isDarkMode
            preferencesManager.isDarkMode = newDarkMode
            currentState.copy(isDarkMode = newDarkMode)
        }
    }

    /**
     * Update teks dan simpan ke preferences
     */
    fun updateText(newText: String) {
        _uiState.update { it.copy(text = newText) }
        viewModelScope.launch {
            preferencesManager.lastText = newText
        }
    }
}