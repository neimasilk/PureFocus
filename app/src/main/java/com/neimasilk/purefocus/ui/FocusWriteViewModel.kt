package com.neimasilk.purefocus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neimasilk.purefocus.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel untuk mengelola state teks di FocusWriteScreen.
 * Menangani input teks pengguna dan integrasi dengan penyimpanan data.
 */
@HiltViewModel
class FocusWriteViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // State internal untuk teks yang sedang ditulis
    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    // Job untuk auto-save dengan debouncing
    private var autoSaveJob: Job? = null

    init {
        // Muat teks yang tersimpan saat ViewModel diinisialisasi
        loadText()
    }

    /**
     * Memperbarui state teks saat pengguna mengetik
     */
    fun onTextChanged(newText: String) {
        _text.value = newText
        
        // Auto-save dengan debouncing (tunggu 2 detik setelah pengguna berhenti mengetik)
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(2000) // Debounce 2 detik
            saveText()
        }
    }

    /**
     * Menyimpan teks saat ini ke PreferencesManager
     */
    fun saveText() {
        viewModelScope.launch {
            preferencesManager.lastText = _text.value
        }
    }

    /**
     * Memuat teks yang tersimpan dari PreferencesManager
     */
    private fun loadText() {
        _text.value = preferencesManager.lastText
    }

    /**
     * Dipanggil saat ViewModel akan dihancurkan
     * Memastikan teks tersimpan sebelum ViewModel dihancurkan
     */
    override fun onCleared() {
        super.onCleared()
        // Cancel auto-save job dan simpan teks terakhir
        autoSaveJob?.cancel()
        // Simpan teks secara sinkron menggunakan PreferencesManager
        preferencesManager.lastText = _text.value
    }
}