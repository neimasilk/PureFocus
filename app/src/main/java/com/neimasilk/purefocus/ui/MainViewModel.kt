package com.neimasilk.purefocus.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neimasilk.purefocus.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.neimasilk.purefocus.util.PerformanceMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
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
@HiltViewModel
class MainViewModel @Inject constructor(private val preferencesManager: PreferencesManager) : ViewModel() {
    
    // State UI internal untuk text field
    private val _textState = MutableStateFlow(TextFieldValue(
        text = preferencesManager.lastText,
        selection = TextRange(preferencesManager.lastText.length)
    ))
    
    // Combine dark mode dari preferences dengan text state
    val uiState: StateFlow<MainUiState> = combine(
        preferencesManager.isDarkMode,
        _textState
    ) { isDarkMode, textFieldValue ->
        MainUiState(
            isDarkMode = isDarkMode,
            textFieldValue = textFieldValue,
            wordCount = calculateWordCount(textFieldValue.text),
            characterCount = calculateCharacterCount(textFieldValue.text)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(
            isDarkMode = false,
            textFieldValue = TextFieldValue(
                text = preferencesManager.lastText,
                selection = TextRange(preferencesManager.lastText.length)
            ),
            wordCount = calculateWordCount(preferencesManager.lastText),
            characterCount = calculateCharacterCount(preferencesManager.lastText)
        )
    )
    
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
        viewModelScope.launch {
            val currentDarkMode = preferencesManager.isDarkMode.value
            preferencesManager.updateDarkMode(!currentDarkMode)
        }
    }

    /**
     * Update teks dan simpan ke preferences dengan debouncing
     * untuk mengurangi operasi I/O yang berlebihan
     */
    fun updateText(newText: String) {
        val currentValue = _textState.value
        val newValue = currentValue.copy(text = newText)
        updateTextFieldValue(newValue)
    }
    
    /**
     * Update TextFieldValue untuk mendukung pemulihan posisi kursor
     */
    fun updateTextFieldValue(newValue: TextFieldValue) {
        _textState.value = newValue
    }
    
    /**
     * Menghapus semua teks dan membersihkan storage
     */
    fun clearText() {
        _textState.value = TextFieldValue()
        viewModelScope.launch {
            preferencesManager.clearFocusWriteText()
        }
    }
    
    /**
     * Menyimpan teks secara manual (jika diperlukan)
     */
    fun saveTextManually() {
        viewModelScope.launch {
            preferencesManager.saveFocusWriteText(_textState.value.text)
        }
    }
    
    // Inisialisasi auto-load dan auto-save
    init {
        // Load saved text saat ViewModel diinisialisasi
        viewModelScope.launch {
            preferencesManager.getFocusWriteText().collect { savedText ->
                if (savedText.isNotEmpty() && _textState.value.text.isEmpty()) {
                    _textState.value = TextFieldValue(
                        text = savedText,
                        selection = TextRange(savedText.length)
                    )
                }
            }
        }
        
        // Auto-save dengan debouncing 1 detik
        viewModelScope.launch {
            _textState
                .map { it.text }
                .distinctUntilChanged()
                .debounce(1000) // Tunggu 1 detik setelah input terakhir
                .collect { text ->
                    // Simpan teks ke preferences
                    preferencesManager.saveFocusWriteText(text)
                }
        }
    }
}