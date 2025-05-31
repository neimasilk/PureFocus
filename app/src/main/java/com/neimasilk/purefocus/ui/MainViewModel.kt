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
    val textFieldValue: TextFieldValue = TextFieldValue(text = "", selection = TextRange(0)),
    val wordCount: Int = 0,
    val characterCount: Int = 0
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
        ),
        wordCount = calculateWordCount(preferencesManager.lastText),
        characterCount = calculateCharacterCount(preferencesManager.lastText)
    ))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    /**
     * Menghitung jumlah kata dalam teks
     * @param text Teks yang akan dihitung kata-katanya
     * @return Jumlah kata dalam teks
     */
    private fun calculateWordCount(text: String): Int {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return 0
        
        // Split berdasarkan whitespace dan filter kata yang tidak kosong
        return trimmedText.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
    }
    
    /**
     * Menghitung jumlah karakter dalam teks (termasuk spasi dan newline)
     * @param text Teks yang akan dihitung karakternya
     * @return Jumlah karakter dalam teks
     */
    private fun calculateCharacterCount(text: String): Int {
        return text.length
    }

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
        val newWordCount = calculateWordCount(newValue.text)
        val newCharacterCount = calculateCharacterCount(newValue.text)
        
        _uiState.update { 
            it.copy(
                textFieldValue = newValue,
                wordCount = newWordCount,
                characterCount = newCharacterCount
            )
        }
    }
    
    /**
     * Menghapus semua teks dan membersihkan storage
     */
    fun clearText() {
        _uiState.update { 
            it.copy(
                textFieldValue = TextFieldValue(),
                wordCount = 0,
                characterCount = 0
            )
        }
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
                    val newWordCount = calculateWordCount(savedText)
                    val newCharacterCount = calculateCharacterCount(savedText)
                    
                    _uiState.update { currentState ->
                        currentState.copy(
                            textFieldValue = TextFieldValue(
                                text = savedText,
                                selection = TextRange(savedText.length)
                            ),
                            wordCount = newWordCount,
                            characterCount = newCharacterCount
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