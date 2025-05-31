# Status, TODO, and Suggestions (Post-MVP)

## Current Status (Based on progress.md - January 2025)

*   **PROJECT STATUS: MVP COMPLETE ✅**
*   **Main Milestone Achieved:** The PureFocus application with all core MVP features has been successfully built, comprehensively tested, and meets performance targets. Ready for production deployment.
*   **Core MVP Functionality (All Implemented & Tested):**
    *   Minimalist "Focus Write" Mode with a full-screen text editor.
    *   Auto-save and auto-load text in Focus Write Screen using SharedPreferences with debouncing.
    *   "Clear All Text" functionality with a confirmation dialog.
    *   Real-time word count and character count.
    *   Integrated Pomodoro Timer (Focus Session, Short Break). Basic logic for Long Break exists.
    *   `PomodoroService` as a foreground service ensures the timer runs in the background.
    *   User-configurable timer duration (focus) settings.
    *   Notifications for the end of each session.
    *   Navigation between screens (Timer/Focus Write, Settings).
    *   Light/Dark Theme.
*   **Quality & Technical:**
    *   MVVM Architecture with Jetpack Compose.
    *   Use of Coroutines and Flow for asynchronous operations and state management.
    *   Clean, structured code following modern Android practices.
*   **Testing:**
    *   Comprehensive test coverage: 21 unit tests and 5 instrumented tests, all passing successfully.
    *   All previously recorded issues related to unit tests and instrumented tests have been resolved.

## TODO - Future Work (Post-MVP Features & Enhancements)

This list is taken from previous plans and adjusted for the post-MVP context.

### Next Top Priorities:

1.  **✅ DONE - Refinement of `PomodoroService` & Timer State Management:**
    *   **✅ Move Timer Logic to `PomodoroService`:** Core timer logic (countdown, session transition) has been successfully moved entirely to `PomodoroService`. The timer is now independent of the UI lifecycle and more robust.
    *   **✅ State Synchronization between Service and ViewModel:** Implementation of `StateFlow` exposed from the Service to update the UI in `PomodoroTimerViewModel` has been successful.
    *   **Comprehensive Timer State Persistence:** Ensure all aspects of `PomodoroState` (including `timeLeftInMillis`, `currentSessionType`, `pomodorosCompletedInCycle`, `isTimerRunning`) are saved and restored correctly if the service is stopped and restarted (e.g., by the system). Consider DataStore for this.
2.  **✅ DONE - Full Implementation of Long Break Cycle:**
    *   **✅ Activate and fully integrate transition logic to `LONG_BREAK`:** Transition logic to `LONG_BREAK` after `POMODOROS_PER_CYCLE` (4) is completed has been implemented.
    *   **`LONG_BREAK` duration configurable in Settings:** Still using default constant, needs to be added as a setting in UI Settings.
3.  **Break Duration Settings (Short & Long):**
    *   Add options in `SettingsScreen` to set Short Break and Long Break durations.
    *   Save these values in `PreferencesManager` and use them in `PomodoroService`.
4.  **Notification & Timer Sound Enhancements:**
    *   Add a subtle "tick-tock" sound option during focus sessions (toggleable in settings).
    *   Different/distinct notification sounds for the end of focus sessions and end of break sessions.
    *   Option to choose notification sounds from the system or built-in app sounds.

### Medium Priorities:

5.  **Focus Write Screen - UX Enhancements:**
    *   Undo/Redo functionality.
    *   Basic text formatting options (bold, italic, bullet points).
    *   Find and replace functionality within the text.
    *   Optional full-screen mode for a more immersive writing experience.
    *   Text export/sharing functionality (to file, email, or other apps).
6.  **Pomodoro Statistics & History:**
    *   Track the number of focus sessions completed per day/week.
    *   Display simple statistics (e.g., bar chart or summary).
    *   Requires more structured data persistence (possibly using Room Database).
7.  **Advanced UI/UX Enhancements:**
    *   Smoother transition animations between sessions.
    *   Additional theme options (e.g., more customizable dark theme, different color themes).
    *   Review and improve accessibility aspects (test with TalkBack, ensure sufficient color contrast).
    *   Use Jetpack Compose Navigation for more standard and manageable screen navigation.
8.  **Advanced Settings:**
    *   Option to disable auto-start of the next break/focus session.
    *   Option for custom notification sounds per session type.
    *   "Daily Goal" for the number of Pomodoro sessions.

### Low Priorities / Long-Term Ideas:

9.  **Calendar Integration (Optional):**
    *   Synchronize focus sessions with the user's calendar.
10. **"Strict" Pomodoro Mode:**
    *   Prevent users from exiting the app or opening other apps during focus sessions.
11. **Settings/Statistics Backup & Restore:**
    *   Using Android backup service or manual export/import.

## "Baby-Step Todolist" Suggestions (For Immediate Post-MVP Action):

Here are suggestions for small, concrete steps based on the top priorities above:

1.  **Move Core Timer Logic to `PomodoroService`:**
    *   **Identify & Move:** In `PomodoroTimerViewModel`, identify the countdown logic, `timeLeftInMillis` updates, and end-of-session handling. Move it to `PomodoroService`.
    *   **Internal Service State:** `PomodoroService` will manage its own internal `PomodoroState` (e.g., using `MutableStateFlow`).
    *   **Control from ViewModel:** `PomodoroTimerViewModel` will send commands (Start, Pause, Reset, Skip, etc.) to `PomodoroService` via `Intent`.
2.  **Implement Two-Way Communication from Service to UI (ViewModel/Activity):**
    *   **Expose State from Service:** `PomodoroService` needs to send `PomodoroState` updates (or at least `timeLeftInMillis` and `currentSessionType`) back to the UI. Use a `Flow` exposed from the Service that can be collected by `PomodoroTimerViewModel`. This might require binding to the service.
    *   **Update UI:** `PomodoroTimerViewModel` updates `_uiState` based on data received from the service.
3.  **Implement Short & Long Break Duration Settings in Settings:**
    *   **Update `PreferencesManager`:** Add keys and functions to save/retrieve short and long break durations.
    *   **Update `SettingsViewModel`:** Add `StateFlow` for these durations and functions to update them.
    *   **Update `SettingsScreen`:** Add `OutlinedTextField` and a `Save` button for short and long break durations.
    *   **Use in `PomodoroService`:** `PomodoroService` should read these durations from `PreferencesManager` when starting a break session.
4.  **Fully Integrate Long Break Cycle in `PomodoroService`:**
    *   Track `pomodorosCompletedInCycle` in `PomodoroService`.
    *   After `POMODOROS_PER_CYCLE` is reached, transition to `LONG_BREAK` using the duration configurable from Settings.
    *   Reset `pomodorosCompletedInCycle` after `LONG_BREAK`.
5.  **Unit Test for Timer Logic in `PomodoroService`:** After the timer logic is moved and refined, write/update unit tests to ensure countdown, session transitions (including long break), and command handling in the service function correctly.

---
## Status of Pre-Release Critical Tasks (From Previous List)

This section revisits items from the "Minimum Critical Steps Before Public Release" based on the MVP Complete status.

*   **[RESOLVED/RE-EVALUATE] ~~[TODO-RELEASE-1.1] Secure `FocusWriteScreen` Text State from Configuration Changes:~~**
    *   *Note:* `progress.md` and the `MainViewModel` implementation handling text storage via `PreferencesManager` (auto-save/load) likely already address text loss due to configuration changes. This needs re-verification if there's local text state in `FocusWriteScreen.kt` not managed by the ViewModel.
*   **[DONE] ✅ Secure Input Field State in `SettingsScreen` from Configuration Changes:**
    *   Use of `rememberSaveable` for input fields in `SettingsScreen` has been implemented.
*   **[DONE] ✅ Implement Basic Notification Sound Control Options:**
    *   Notification sound control feature has been implemented (preferences, UI switch, conditional sound playback).

---

By completing the MVP, PureFocus has a strong foundation. The next steps will build upon this foundation to create a richer-featured and more mature application. Happy continued development!