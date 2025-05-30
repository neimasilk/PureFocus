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
    
    // Inisialisasi debouncing untuk auto-save
    init {
        // Observe perubahan text dan simpan ke preferences setelah 500ms tidak ada perubahan
        viewModelScope.launch {
            _uiState
                .map { it.textFieldValue.text }
                .distinctUntilChanged()
                .debounce(500) // Tunggu 500ms setelah input terakhir
                .collect { text ->
                    // Hanya simpan jika teks tidak kosong atau berbeda dari yang tersimpan
                    if (text != preferencesManager.lastText) {
                        preferencesManager.lastText = text
                        // Gunakan try-catch untuk menangani jika PerformanceMonitor tidak tersedia (dalam test)
                        // try {
                        //     PerformanceMonitor.logMemoryUsage() // Monitor penggunaan memori setelah save
                        // } catch (e: Exception) {
                        //     // Ignore exception in tests
                        // }
                    }
                }
        }
    }
}