package com.neimasilk.purefocus.ui

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel untuk FocusWriteScreen yang mengelola state teks.
 * Menyediakan StateFlow untuk text content yang dapat diobservasi oleh UI.
 */
@HiltViewModel
class FocusWriteViewModel @Inject constructor() : ViewModel() {
    
    // Private mutable state untuk text content
    private val _textFieldValue = MutableStateFlow(TextFieldValue(""))
    
    // Public read-only state untuk UI
    val textFieldValue: StateFlow<TextFieldValue> = _textFieldValue.asStateFlow()
    
    /**
     * Update text content dari UI
     */
    fun updateText(newTextFieldValue: TextFieldValue) {
        _textFieldValue.value = newTextFieldValue
    }
    
    /**
     * Clear semua text content
     */
    fun clearText() {
        _textFieldValue.value = TextFieldValue("")
    }
    
    /**
     * Get current text as String
     */
    fun getCurrentText(): String {
        return _textFieldValue.value.text
    }
    
    /**
     * Get word count dari current text
     */
    fun getWordCount(): Int {
        val text = _textFieldValue.value.text.trim()
        return if (text.isEmpty()) 0 else text.split("\\s+".toRegex()).size
    }
    
    /**
     * Get character count dari current text
     */
    fun getCharacterCount(): Int {
        return _textFieldValue.value.text.length
    }
}