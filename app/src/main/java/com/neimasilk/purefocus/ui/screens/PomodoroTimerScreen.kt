package com.neimasilk.purefocus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neimasilk.purefocus.model.SessionType
import com.neimasilk.purefocus.ui.PomodoroTimerViewModel

@Composable
fun PomodoroTimerScreen(
    pomodoroViewModel: PomodoroTimerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by pomodoroViewModel.uiState.collectAsState()
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Session Type Display
                Text(
                    text = when (uiState.currentSessionType) {
                        SessionType.WORK -> "Focus Session"
                        SessionType.SHORT_BREAK -> "Short Break"
                        SessionType.LONG_BREAK -> "Long Break"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Timer Display
                Text(
                    text = formatTime(uiState.timeLeftInMillis),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 64.sp,
                    modifier = Modifier.testTag("TimerText")
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Control Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Start/Pause Button
                    Button(
                        onClick = {
                            if (uiState.isTimerRunning) {
                                pomodoroViewModel.pauseTimer()
                            } else {
                                pomodoroViewModel.startTimer()
                            }
                        },
                        modifier = Modifier
                            .testTag(if (uiState.isTimerRunning) "PauseButton" else "StartButton")
                            .size(width = 120.dp, height = 48.dp)
                    ) {
                        Text(
                            text = if (uiState.isTimerRunning) "Pause" else "Start",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Reset Button
                    OutlinedButton(
                        onClick = { pomodoroViewModel.resetTimer() },
                        modifier = Modifier
                            .testTag("ResetButton")
                            .size(width = 120.dp, height = 48.dp)
                    ) {
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Session Progress Info
                Text(
                    text = "Pomodoros completed: ${uiState.pomodorosCompletedInCycle}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Formats time in milliseconds to MM:SS format
 */
private fun formatTime(timeInMillis: Long): String {
    val totalSeconds = timeInMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}