package com.neimasilk.purefocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neimasilk.purefocus.model.SessionType
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.ui.MainViewModel
import com.neimasilk.purefocus.ui.PomodoroTimerViewModel
import com.neimasilk.purefocus.ui.screens.FocusWriteScreen
import com.neimasilk.purefocus.ui.screens.PomodoroTimerScreen
import com.neimasilk.purefocus.ui.theme.PureFocusTheme
import com.neimasilk.purefocus.util.PerformanceMonitor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var pomodoroViewModel: PomodoroTimerViewModel
    
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
        pomodoroViewModel = ViewModelProvider(this).get(PomodoroTimerViewModel::class.java)
        
        enableEdgeToEdge()
        setContent {
            // Collect UI state dari ViewModel
            val uiState by viewModel.uiState.collectAsState()
            val pomodoroState by pomodoroViewModel.uiState.collectAsState()
            
            // Gunakan isDarkMode dari preferences
            PureFocusTheme(darkTheme = uiState.isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold { innerPadding ->
                        // Tampilan kondisional berdasarkan currentSessionType
                        when (pomodoroState.currentSessionType) {
                            SessionType.WORK -> {
                                // Tampilkan FocusWriteScreen selama sesi fokus
                                FocusWriteScreen(
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> {
                                // Tampilkan UI timer untuk sesi istirahat
                                PomodoroTimerScreen(
                                    pomodoroState = pomodoroState,
                                    onStartTimer = { pomodoroViewModel.startTimer() },
                                    onPauseTimer = { pomodoroViewModel.pauseTimer() },
                                    onResetTimer = { pomodoroViewModel.resetTimer() },
                                    onSkipSession = { pomodoroViewModel.skipSession() },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
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