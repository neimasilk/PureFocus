package com.neimasilk.purefocus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.ui.MainViewModel
import com.neimasilk.purefocus.ui.PomodoroTimerViewModel
import com.neimasilk.purefocus.ui.SettingsViewModel
import com.neimasilk.purefocus.service.PomodoroService
import com.neimasilk.purefocus.util.PomodoroServiceActions
import com.neimasilk.purefocus.ui.screens.FocusWriteScreen
import com.neimasilk.purefocus.ui.screens.SettingsScreen
import com.neimasilk.purefocus.ui.theme.PureFocusTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import com.neimasilk.purefocus.ui.PomodoroControlsView
import com.neimasilk.purefocus.util.NotificationHelper
import com.neimasilk.purefocus.util.PerformanceMonitor

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var pomodoroViewModel: PomodoroTimerViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    
    // State untuk dialog permission
    private var showPermissionRationale = mutableStateOf(false)
    
    // Launcher untuk meminta permission notifikasi
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, notifications dapat berjalan
        } else {
            // Permission denied, tampilkan dialog penjelasan
            showPermissionRationale.value = true
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Mulai timer untuk mengukur waktu startup
        PerformanceMonitor.startTimer("MainActivity_onCreate")
        
        // Inisialisasi ViewModel
        val preferencesManager = PreferencesManager(applicationContext)
        val mainViewModelFactory = viewModelFactory { 
            initializer { MainViewModel(preferencesManager) } 
        }
        val pomodoroViewModelFactory = viewModelFactory {
            initializer { PomodoroTimerViewModel(preferencesManager) }
        }
        val settingsViewModelFactory = SettingsViewModel.factory(preferencesManager)
        
        viewModel = ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
        pomodoroViewModel = ViewModelProvider(this, pomodoroViewModelFactory)[PomodoroTimerViewModel::class.java]
        settingsViewModel = ViewModelProvider(this, settingsViewModelFactory)[SettingsViewModel::class.java]
        
        // Request notification permission untuk Android 13+ sudah dihandle di dalam Composable
        
        enableEdgeToEdge()
        setContent {
            // Collect UI state dari ViewModel
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current
            
            // State untuk dialog permission rationale
            val showRationale by showPermissionRationale
            
            // Dialog untuk menjelaskan mengapa permission diperlukan
            if (showRationale) {
                AlertDialog(
                    onDismissRequest = { showPermissionRationale.value = false },
                    title = { Text("Izin Notifikasi Diperlukan") },
                    text = { 
                        Text("PureFocus memerlukan izin notifikasi untuk memberi tahu Anda ketika sesi Pomodoro berakhir. Tanpa izin ini, notifikasi timer tidak akan muncul. Anda dapat mengaktifkan izin ini melalui Pengaturan Aplikasi.") 
                    },
                    confirmButton = {
                        TextButton(onClick = { showPermissionRationale.value = false }) {
                            Text("Mengerti")
                        }
                    }
                )
            }
            
            // Request notification permission untuk Android 13+ saat aplikasi pertama dibuka
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            // Permission sudah diberikan
                        }
                        else -> {
                            // Request permission langsung
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            }
            
            // Observasi event notifikasi dari PomodoroTimerViewModel
        LaunchedEffect(pomodoroViewModel) {
            pomodoroViewModel.showFocusEndNotificationEvent.collect {
                val enableSound = settingsViewModel.enableSoundNotifications.value
                NotificationHelper.showFocusSessionEndNotification(context, enableSound)
            }
        }
        
        // Observasi event notifikasi akhir sesi istirahat
        LaunchedEffect(pomodoroViewModel) {
            pomodoroViewModel.showBreakEndNotificationEvent.collect {
                val enableSound = settingsViewModel.enableSoundNotifications.value
                NotificationHelper.showBreakSessionEndNotification(context, enableSound)
            }
        }
            
            // Observasi event service command dari PomodoroTimerViewModel
            LaunchedEffect(pomodoroViewModel) {
                pomodoroViewModel.serviceCommandEvent.collect { action ->
                    val serviceIntent = Intent(applicationContext, PomodoroService::class.java)
                    when (action) {
                        PomodoroTimerViewModel.ServiceAction.START -> {
                            serviceIntent.action = PomodoroServiceActions.ACTION_START_RESUME
                            ContextCompat.startForegroundService(applicationContext, serviceIntent)
                        }
                        PomodoroTimerViewModel.ServiceAction.PAUSE -> {
                            serviceIntent.action = PomodoroServiceActions.ACTION_PAUSE
                            startService(serviceIntent)
                        }
                        PomodoroTimerViewModel.ServiceAction.RESET -> {
                            serviceIntent.action = PomodoroServiceActions.ACTION_RESET
                            startService(serviceIntent)
                        }
                        PomodoroTimerViewModel.ServiceAction.SKIP -> {
                            serviceIntent.action = PomodoroServiceActions.ACTION_SKIP
                            startService(serviceIntent)
                        }
                    }
                }
            }
            
            // Gunakan isDarkMode dari preferences
            PureFocusTheme(darkTheme = uiState.isDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSettings by remember { mutableStateOf(false) }
                    
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { showSettings = !showSettings }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = if (showSettings) "Back to Focus" else "Settings"
                                )
                            }
                        }
                    ) { innerPadding ->
                        if (showSettings) {
                             SettingsScreen(
                                 settingsViewModel = settingsViewModel,
                                 modifier = Modifier.padding(innerPadding)
                             )
                        } else {
                            // Collect teks dari PomodoroTimerViewModel untuk Focus Write
                            val focusWriteText by pomodoroViewModel.focusWriteText.collectAsState()
                            
                            Column(modifier = Modifier.padding(innerPadding)) {
                                FocusWriteScreen(
                                    text = focusWriteText,
                                    onTextChanged = { pomodoroViewModel.updateFocusWriteText(it) },
                                    onClearText = { viewModel.clearText() },
                                    wordCount = uiState.wordCount,
                                    characterCount = uiState.characterCount,
                                    modifier = Modifier.weight(1f) // Allow FocusWriteScreen to take available space
                                )
                                PomodoroControlsView(
                                    pomodoroViewModel = pomodoroViewModel,
                                    modifier = Modifier.fillMaxWidth()
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
            onTextChanged = {},
            onClearText = {},
            wordCount = 7,
            characterCount = 37
        )
    }
}