package com.neimasilk.purefocus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.neimasilk.purefocus.util.PerformanceMonitor

/**
 * Layar utama untuk menulis teks dengan fokus penuh.
 * Menyediakan area teks penuh layar tanpa distraksi.
 * 
 * Versi ini mendukung String sebagai input untuk kompatibilitas mundur.
 */
@Composable
fun FocusWriteScreen(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Konversi String ke TextFieldValue untuk digunakan internal
    val textFieldValue = remember(text) { TextFieldValue(text = text, selection = TextRange(text.length)) }
    var textFieldValueState by remember { mutableStateOf(textFieldValue) }
    
    // Implementasi FocusWriteScreen dengan TextFieldValue
    FocusWriteScreenImpl(
        value = textFieldValueState,
        onValueChange = { newValue ->
            textFieldValueState = newValue
            // Hanya panggil onTextChanged jika teks berubah
            if (newValue.text != text) {
                onTextChanged(newValue.text)
            }
        },
        modifier = modifier
    )
}

/**
 * Layar utama untuk menulis teks dengan fokus penuh.
 * Menyediakan area teks penuh layar tanpa distraksi.
 * 
 * Versi ini menerima TextFieldValue langsung untuk mendukung pemulihan posisi kursor.
 */
@Composable
fun FocusWriteScreen(
    textFieldValue: TextFieldValue,
    onTextFieldValueChanged: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementasi FocusWriteScreen dengan TextFieldValue
    FocusWriteScreenImpl(
        value = textFieldValue,
        onValueChange = onTextFieldValueChanged,
        modifier = modifier
    )
}

/**
 * Implementasi internal FocusWriteScreen yang menggunakan TextFieldValue
 * untuk mendukung pemulihan posisi kursor dan seleksi teks.
 */
@Composable
private fun FocusWriteScreenImpl(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mulai timer untuk mengukur performa
    LaunchedEffect(Unit) {
        PerformanceMonitor.startTimer("FocusWriteScreen_Composition")
    }
    
    // Focus requester untuk mengatur fokus ke text field saat layar dibuka
    val focusRequester = remember { FocusRequester() }
    
    // Akses clipboard manager untuk fungsionalitas salin
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    // State untuk menu konteks
    var showContextMenu by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .focusRequester(focusRequester)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { showContextMenu = true }
                    )
                },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            // Tidak menggunakan dekorasi untuk memaksimalkan kesederhanaan
            decorationBox = { innerTextField -> innerTextField() }
        )
        
        // Menu konteks untuk salin teks
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Salin Semua") },
                onClick = {
                    clipboardManager.setText(AnnotatedString(value.text))
                    Toast.makeText(context, "Teks disalin", Toast.LENGTH_SHORT).show()
                    showContextMenu = false
                }
            )
        }
    }
    
    // Set fokus ke text field saat komposisi selesai
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        PerformanceMonitor.endTimer("FocusWriteScreen_Composition")
    }
}