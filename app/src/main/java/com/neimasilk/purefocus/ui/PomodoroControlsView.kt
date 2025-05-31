package com.neimasilk.purefocus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import androidx.compose.foundation.layout.Column

@Composable
fun PomodoroControlsView(
    pomodoroViewModel: PomodoroTimerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by pomodoroViewModel.uiState.collectAsState() // Use uiState
    val pomodoroState = uiState // for easier access to PomodoroState properties

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formatTimerValue(pomodoroState.timeLeftInMillis.toInt() / 1000), // Access timeLeftInMillis and convert to seconds
            fontSize = 48.sp,
            modifier = Modifier.padding(vertical = 16.dp).testTag("TimerText")
        )
        Text(
            text = getCurrentSessionText(pomodoroState.currentSessionType),
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp).testTag("SessionTypeText")
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { pomodoroViewModel.startPauseTimer() },
                modifier = Modifier.testTag(if (pomodoroState.isTimerRunning) "PauseButton" else "StartButton") // use isTimerRunning
            ) {
                Text(if (pomodoroState.isTimerRunning) "Pause" else "Start") // use isTimerRunning
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { pomodoroViewModel.resetTimer() },
                modifier = Modifier.testTag("ResetButton")
            ) {
                Text("Reset")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { pomodoroViewModel.skipSession() },
                modifier = Modifier.testTag("SkipButton")
            ) {
                Text("Skip")
            }
        }
    }
}

private fun formatTimerValue(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

private fun getCurrentSessionText(sessionType: SessionType): String {
    return when (sessionType) {
        SessionType.WORK -> "Focus Session" // Changed FOCUS to WORK
        SessionType.SHORT_BREAK -> "Short Break"
        SessionType.LONG_BREAK -> "Long Break"
        // No else needed as SessionType is an enum and all cases are handled
    }
}