package com.neimasilk.purefocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.ui.MainViewModel
import com.neimasilk.purefocus.ui.screens.FocusWriteScreen
import com.neimasilk.purefocus.ui.theme.PureFocusTheme
import com.neimasilk.purefocus.util.PerformanceMonitor

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Mulai timer untuk mengukur waktu startup
        PerformanceMonitor.startTimer("MainActivity_onCreate")
        
        // Inisialisasi ViewModel
        val preferencesManager = PreferencesManager(applicationContext)
        val factory = viewModelFactory { 
            initializer { MainViewModel(preferencesManager) } 
        }
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            // Collect UI state dari ViewModel
            val uiState by viewModel.uiState.collectAsState()
            
            // Gunakan isDarkMode dari preferences
            PureFocusTheme(darkTheme = uiState.isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold { innerPadding ->
                        FocusWriteScreen(
                            textFieldValue = uiState.textFieldValue,
                            onTextFieldValueChanged = { viewModel.updateTextFieldValue(it) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
        
        // Akhiri timer dan log waktu startup
        PerformanceMonitor.endTimer("MainActivity_onCreate")
        PerformanceMonitor.logMemoryUsage()
    }
}

@Preview(showBackground = true)
@Composable
fun FocusWriteScreenPreview() {
    PureFocusTheme {
        FocusWriteScreen(
            text = "Ini adalah contoh teks untuk preview.",
            onTextChanged = {}
        )
    }
}