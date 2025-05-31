package com.neimasilk.purefocus.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.util.PerformanceMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class yang merepresentasikan state UI utama aplikasi
 */
data class MainUiState(
    val isDarkMode: Boolean = false,
    val textFieldValue: TextFieldValue = TextFieldValue(text = "", selection = TextRange(0))
)

/**
 * ViewModel utama untuk aplikasi PureFocus.
 * Mengelola state UI dan interaksi dengan data layer.
 */
class MainViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {
    
    // State UI yang diobservasi oleh UI
    private val _uiState = MutableStateFlow(MainUiState(
        isDarkMode = preferencesManager.isDarkMode,
        textFieldValue = TextFieldValue(
            text = preferencesManager.lastText,
            selection = TextRange(preferencesManager.lastText.length)
        )
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
     * Update teks dan simpan ke preferences dengan debouncing
     * untuk mengurangi operasi I/O yang berlebihan
     */
    fun updateText(newText: String) {
        val currentValue = _uiState.value.textFieldValue
        val newValue = currentValue.copy(text = newText)
        updateTextFieldValue(newValue)
    }
    
    /**
     * Update TextFieldValue untuk mendukung pemulihan posisi kursor
     */
    fun updateTextFieldValue(newValue: TextFieldValue) {
        _uiState.update { it.copy(textFieldValue = newValue) }
    }
    
    /**
     * Menghapus semua teks dan membersihkan storage
     */
    fun clearText() {
        _uiState.update { it.copy(textFieldValue = TextFieldValue()) }
        viewModelScope.launch {
            preferencesManager.clearFocusWriteText()
        }
    }
    
    /**
     * Menyimpan teks secara manual (jika diperlukan)
     */
    fun saveTextManually() {
        viewModelScope.launch {
            preferencesManager.saveFocusWriteText(_uiState.value.textFieldValue.text)
        }
    }
    
    // Inisialisasi auto-load dan auto-save
    init {
        // Load saved text saat ViewModel diinisialisasi
        viewModelScope.launch {
            preferencesManager.getFocusWriteText().collect { savedText ->
                if (savedText.isNotEmpty() && _uiState.value.textFieldValue.text.isEmpty()) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            textFieldValue = TextFieldValue(
                                text = savedText,
                                selection = TextRange(savedText.length)
                            )
                        )
                    }
                }
            }
        }
        
        // Auto-save dengan debouncing 1 detik
        viewModelScope.launch {
            _uiState
                .map { it.textFieldValue.text }
                .distinctUntilChanged()
                .debounce(1000) // Tunggu 1 detik setelah input terakhir
                .collect { text ->
                    // Simpan teks ke preferences
                    preferencesManager.saveFocusWriteText(text)
                }
        }
    }
}