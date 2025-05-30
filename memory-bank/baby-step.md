# PureFocus - Baby Step Implementation Guide

**Document Version:** 1.0
**Date:** 31 Mei 2025
**Baby-Step Name:** Text Editor Foundation
**Estimated Duration:** 2-3 days

## Baby-Step Overview

**Objective:** Implement the foundational UI and ViewModel for the full-screen text editor, including basic text input and auto-save functionality to SharedPreferences.

**Context:** Phase 0 (Initial Project Setup) is complete. The application builds and runs on an emulator. This baby-step initiates Phase 1: Core Text Editor, focusing on the primary user interface for writing.

**Success Criteria:**
- A full-screen, distraction-free text input field is visible and usable.
- Text entered by the user is persisted via SharedPreferences on change (with debouncing).
- Text is loaded from SharedPreferences when the editor screen is launched.
- Basic MVVM structure for the editor screen is implemented (`FocusWriteScreen.kt`, `MainViewModel.kt`).
- `PreferencesManager.kt` handles saving and loading text.
- App launch time remains < 1 second.
- Text input latency is < 16ms.

## Detailed Implementation Tasks

### Task 1: Implement `FocusWriteScreen.kt` (UI)
**Description:** Create the Jetpack Compose UI for the full-screen text editor. This will be a simple, minimalist screen with a `TextField` that occupies the entire screen.
**Purpose:** Provide the primary user interface for text input.
**Files to Create/Modify:**
- `app/src/main/java/com/neimasilk/purefocus/ui/screens/FocusWriteScreen.kt` (Create)
- `app/src/main/java/com/neimasilk/purefocus/MainActivity.kt` (Modify to display `FocusWriteScreen`)
- `app/src/main/java/com/neimasilk/purefocus/ui/theme/Theme.kt` (Potentially modify for full-screen appearance if needed)
**Implementation Steps:**
1. Create `FocusWriteScreen.kt`.
2. Define a Composable function, e.g., `FocusWriteScreenContent(text: String, onTextChanged: (String) -> Unit)`.
3. Use a `Scaffold` or `Box` with `modifier = Modifier.fillMaxSize()`.
4. Implement a `BasicTextField` (or `TextField` if more features are immediately needed, but aim for minimalism) that fills the screen.
   - `value = text`
   - `onValueChange = onTextChanged`
   - `modifier = Modifier.fillMaxSize().padding(16.dp)` (or appropriate padding)
   - `textStyle = MaterialTheme.typography.bodyLarge` (or a custom style for readability)
   - Ensure it handles multi-line input correctly.
5. Update `MainActivity.kt` in `setContent` to call `FocusWriteScreenContent` (initially with a dummy ViewModel or state).
**Acceptance Criteria:**
- A full-screen text input area is displayed.
- User can type and see text appear.
- Text field is scrollable for long content.
- No other UI elements are visible (distraction-free).
**Validation Method:**
- Run the app on an emulator/device.
- Verify the text field appears full-screen and accepts input.

### Task 2: Implement `MainViewModel.kt` for Text State
**Description:** Create a ViewModel to hold and manage the state of the text editor's content.
**Purpose:** Separate UI logic from data management, following MVVM.
**Files to Create/Modify:**
- `app/src/main/java/com/neimasilk/purefocus/ui/viewmodel/MainViewModel.kt` (Create)
- `app/src/main/java/com/neimasilk/purefocus/ui/screens/FocusWriteScreen.kt` (Modify to use ViewModel)
- `app/src/main/java/com/neimasilk/purefocus/PureFocusApplication.kt` (Potentially for Hilt setup if used, or manual ViewModel instantiation)
**Implementation Steps:**
1. Create `MainViewModel.kt` extending `ViewModel`.
2. Define a `MutableStateFlow<String>` or `LiveData<String>` to hold the current text, e.g., `val textContent = MutableStateFlow("")`.
3. Create a function to update `textContent`, e.g., `fun onTextChanged(newText: String) { textContent.value = newText }`.
4. In `FocusWriteScreen.kt`, obtain an instance of `MainViewModel` (e.g., using `viewModel()` delegate if Hilt is set up, or passed manually).
5. Connect the `TextField`'s `value` to `viewModel.textContent.collectAsState().value` and `onValueChange` to `viewModel::onTextChanged`.
**Acceptance Criteria:**
- Text typed into `FocusWriteScreen` updates the state in `MainViewModel`.
- ViewModel provides the text state to the UI.
**Validation Method:**
- Use Android Studio's debugger or Logcat to verify ViewModel state changes.
- Ensure UI reflects ViewModel state.

### Task 3: Implement `PreferencesManager.kt` for Auto-Save
**Description:** Create a class to handle saving and loading text to/from SharedPreferences.
**Purpose:** Persist user's text so it's not lost when the app closes.
**Files to Create/Modify:**
- `app/src/main/java/com/neimasilk/purefocus/model/PreferencesManager.kt` (Create)
- `app/src/main/java/com/neimasilk/purefocus/ui/viewmodel/MainViewModel.kt` (Modify to use `PreferencesManager`)
- `app/src/main/java/com/neimasilk/purefocus/PureFocusApplication.kt` (Modify to initialize `PreferencesManager` or provide context)
**Implementation Steps:**
1. Create `PreferencesManager.kt`.
2. Define methods `saveText(context: Context, text: String)` and `loadText(context: Context): String`.
   - Use a constant key for storing the text, e.g., `"user_text"`.
3. In `MainViewModel.kt`:
   - Inject or pass `Context` (or `Application` context) to access `PreferencesManager`.
   - In `init` block, load text using `preferencesManager.loadText()` and update `textContent`.
   - When `onTextChanged` is called, after updating `textContent`, call `preferencesManager.saveText()`.
   - **Crucial:** Implement debouncing for `saveText` to avoid excessive writes. Use `kotlinx.coroutines.flow.debounce` on the `textContent` flow before collecting it to save, or a manual debounce mechanism if preferred.
     ```kotlin
     // Example in ViewModel init or a dedicated coroutine scope
     viewModelScope.launch {
         textContent
             .debounce(500L) // Save after 500ms of no new input
             .collect { text ->
                 preferencesManager.saveText(applicationContext, text)
             }
     }
     ```
**Acceptance Criteria:**
- Text entered is saved to SharedPreferences automatically after a short delay (e.g., 500ms-1s) of inactivity.
- When the app is relaunched, the previously saved text is loaded and displayed.
**Validation Method:**
- Type text, close the app (fully stop from recent apps), and reopen. Verify text is restored.
- Check SharedPreferences file content using Android Studio's Device File Explorer if necessary.

## Testing and Validation

### Unit Tests Required
- **`MainViewModelTests.kt`:**
    - Test `onTextChanged` updates `textContent`.
    - Test `init` loads text from a mock `PreferencesManager`.
    - Test `onTextChanged` triggers save on a mock `PreferencesManager` (consider how to test debouncing, might be tricky in unit tests without specific frameworks or delays).
- **`PreferencesManagerTests.kt`:**
    - Test `saveText` correctly writes to mock SharedPreferences.
    - Test `loadText` correctly reads from mock SharedPreferences.
    - Test `loadText` returns default (e.g., empty string) if no text saved.

### Manual Testing Steps
1. Launch the app.
2. Type several paragraphs of text into the editor.
3. Wait a few seconds for auto-save.
4. Close the app completely (swipe away from recents).
5. Relaunch the app. **Expected:** The previously typed text should be displayed.
6. Edit the text. Wait. Close and relaunch. **Expected:** Edited text is displayed.
7. Clear app data (via Android settings). Launch app. **Expected:** Editor is empty.

### Performance Validation
- **App Launch Time:** Measure time from app icon tap to `FocusWriteScreen` fully interactive. Use Android Studio Profiler or manual timing. **Target: < 1 second.**
- **Text Input Latency:** While typing, observe for any noticeable lag. Use Android Studio Profiler (CPU and Memory) to check for spikes during text input. **Target: < 16ms per frame (smooth typing).**
- **Memory Usage:** Observe memory usage in Android Studio Profiler during text editing. **Target: < 50MB for this basic functionality.**

## Dependencies and Prerequisites

**Required Before Starting:**
- Phase 0 (Initial Project Setup) fully completed.
- Android Studio and emulator/device configured.
- Basic understanding of Jetpack Compose, Kotlin Coroutines (Flow), and SharedPreferences.

**External Dependencies:**
- `androidx.lifecycle:lifecycle-viewmodel-ktx` (for ViewModel)
- `org.jetbrains.kotlinx:kotlinx-coroutines-core` and `org.jetbrains.kotlinx:kotlinx-coroutines-android` (for coroutines and debounce)
- `androidx.compose.material3:material3` (for UI components, if not already added)
- `androidx.compose.foundation:foundation` (for BasicTextField)

## Risk Mitigation

**Potential Issues:**
- **Debouncing logic complexity:** If `Flow.debounce` is tricky, a simpler manual debounce (e.g., using `Handler.postDelayed`) can be a fallback, but Flow is preferred for coroutine-centric code.
- **Context handling in ViewModel:** Ensure Application context is used for SharedPreferences to avoid memory leaks.
- **State restoration across configuration changes:** Compose's `rememberSaveable` might be needed for `TextField` state if not fully handled by ViewModel state flow on its own during rapid config changes (though ViewModel should cover most cases).

**Performance Risks:**
- **Excessive SharedPreferences writes:** Debouncing is key to mitigate this.
- **Slow `TextField` performance with large text:** Not expected at this stage, but something to monitor. `BasicTextField` is generally more performant than `TextField`.

## Completion Checklist

- [ ] `FocusWriteScreen.kt` implemented with full-screen `TextField`.
- [ ] `MainViewModel.kt` manages text state using `StateFlow`.
- [ ] `PreferencesManager.kt` saves and loads text using SharedPreferences.
- [ ] Auto-save implemented with debouncing in `MainViewModel`.
- [ ] Text loads from SharedPreferences on app launch.
- [ ] Unit tests for `MainViewModel` and `PreferencesManager` written and passing.
- [ ] Manual testing steps completed successfully.
- [ ] Performance validation targets met (launch time, input latency, memory).
- [ ] Code reviewed for clarity, efficiency, and adherence to MVVM.
- [ ] Documentation updated (`architecture.md` if significant changes, `progress.md` to reflect baby-step completion).
- [ ] Changes committed to Git with clear message: "feat: Implement text editor foundation with auto-save".
- [ ] Ready for next baby-step (e.g., Text Editor Optimization).

---

**Note:** This file details the "Text Editor Foundation" baby-step. It will guide the implementation by the AI Coding Assistant and validation by the developer.

**Performance Reminder:** Every baby-step must maintain or improve app performance. No feature is worth compromising the core speed and responsiveness goals.