package com.neimasilk.purefocus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import java.util.concurrent.TimeUnit

/**
 * Screen untuk menampilkan timer Pomodoro dan kontrol-kontrolnya.
 * Ditampilkan selama sesi istirahat (SHORT_BREAK dan LONG_BREAK).
 */
@Composable
fun PomodoroTimerScreen(
    pomodoroState: PomodoroState,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tampilkan tipe sesi saat ini
        Text(
            text = when (pomodoroState.currentSessionType) {
                SessionType.WORK -> "Sesi Fokus"
                SessionType.SHORT_BREAK -> "Istirahat Pendek"
                SessionType.LONG_BREAK -> "Istirahat Panjang"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tampilkan waktu yang tersisa
        Text(
            text = formatTime(pomodoroState.timeLeftInMillis),
            style = MaterialTheme.typography.displayLarge,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Kontrol timer
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = if (pomodoroState.isTimerRunning) onPauseTimer else onStartTimer
            ) {
                Text(if (pomodoroState.isTimerRunning) "Pause" else "Start")
            }
            
            Button(onClick = onResetTimer) {
                Text("Reset")
            }
            
            Button(onClick = onSkipSession) {
                Text("Skip")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tampilkan jumlah pomodoro yang telah diselesaikan
        Text(
            text = "Pomodoro diselesaikan: ${pomodoroState.pomodorosCompletedInCycle}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Format waktu dalam milidetik menjadi format MM:SS
 */
private fun formatTime(timeInMillis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}