package com.neimasilk.purefocus.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.neimasilk.purefocus.util.PerformanceMonitor

/**
 * Layar utama untuk menulis teks dengan fokus penuh.
 * Menyediakan area teks penuh layar tanpa distraksi.
 */
@Composable
fun FocusWriteScreen(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mulai timer untuk mengukur performa
    LaunchedEffect(Unit) {
        PerformanceMonitor.startTimer("FocusWriteScreen_Composition")
    }
    
    // Focus requester untuk mengatur fokus ke text field saat layar dibuka
    val focusRequester = remember { FocusRequester() }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                onTextChanged(newText)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .focusRequester(focusRequester),
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
    }
    
    // Set fokus ke text field saat komposisi selesai
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        PerformanceMonitor.endTimer("FocusWriteScreen_Composition")
    }
}