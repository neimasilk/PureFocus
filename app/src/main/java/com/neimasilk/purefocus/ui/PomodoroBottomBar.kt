package com.neimasilk.purefocus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.util.Locale
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
fun PomodoroBottomBar(
    pomodoroViewModel: PomodoroTimerViewModel,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by pomodoroViewModel.uiState.collectAsState()
    val pomodoroState = uiState

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timer and session info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimerValue(pomodoroState.timeLeftInMillis.toInt() / 1000),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("TimerText")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = getCurrentSessionText(pomodoroState.currentSessionType),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("SessionTypeText")
                )
            }
            
            // Control buttons dengan ikon yang intuitif - ukuran lebih kecil
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause button dengan ikon yang jelas
                FilledIconButton(
                    onClick = { pomodoroViewModel.startPauseTimer() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag(if (pomodoroState.isTimerRunning) "PauseButton" else "StartButton"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (pomodoroState.isTimerRunning) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (pomodoroState.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (pomodoroState.isTimerRunning) "Pause Timer" else "Start Timer",
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Reset button dengan ikon refresh
                FilledIconButton(
                    onClick = { pomodoroViewModel.resetTimer() },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("ResetButton"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Skip button dengan ikon skip next
                FilledIconButton(
                    onClick = { pomodoroViewModel.skipSession() },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("SkipButton"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip Session",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Settings button
                FilledIconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("SettingsButton"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimerValue(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format(Locale.ROOT, "%02d:%02d", minutes, remainingSeconds)
}

private fun getCurrentSessionText(sessionType: SessionType): String {
    return when (sessionType) {
        SessionType.WORK -> "Focus"
        SessionType.SHORT_BREAK -> "Short Break"
        SessionType.LONG_BREAK -> "Long Break"
    }
}