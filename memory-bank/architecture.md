# PureFocus - Architecture Documentation

**Document Version:** 1.1  
**Date:** January 2025  
**Project:** PureFocus - Minimalist Focus Writing App  
**Status:** MVP Complete - All Core Features Implemented and Tested

## Architecture Overview

**Architecture Pattern:** MVVM (Model-View-ViewModel)  
**UI Framework:** Jetpack Compose  
**Language:** Kotlin  
**Platform:** Android (MVP)

### Core Principles
1. **Performance First:** Every architectural decision prioritizes speed and responsiveness
2. **Simplicity:** Minimal layers and abstractions
3. **Single Responsibility:** Each component has one clear purpose
4. **Testability:** Architecture supports easy testing
5. **Maintainability:** Clean, readable, and well-documented code

## High-Level Architecture

PureFocus applies Clean Architecture principles with separation of concerns to enhance testability and maintainability. Although all components currently reside in a single `app` module, logical separation between layers is maintained. Physical separation into distinct modules (e.g., `feature`, `domain`, `data`) is a consideration for future project evolution.

The following is a high-level architectural overview:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Compose UI    │  │   Theme System  │  │  Navigation │ │
│  │   Components    │  │                 │  │   (Minimal) │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    ViewModel Layer                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  TextEditor     │  │  PomodoroTimer  │  │  Settings   │ │
│  │  ViewModel      │  │  ViewModel      │  │  ViewModel  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                     Data Layer                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  TextRepository │  │  TimerRepository│  │ Preferences │ │
│  │                 │  │                 │  │ Repository  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   System Services Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  PomodoroService│  │ NotificationHelper│  │Performance  │ │
│  │ (Foreground)    │  │                 │  │ Monitor     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   Storage Layer                             │
│              SharedPreferences (Primary)                   │
└─────────────────────────────────────────────────────────────┘
```

## Component Details

### Presentation Layer

**Compose UI Components:**
- `FocusWriteScreen`: Main full-screen text editor
- `PomodoroTimerOverlay`: Non-intrusive timer display
- `SettingsDialog`: Minimal settings interface
- `ThemeProvider`: Theme management and switching

**Design Principles:**
- Minimal recomposition
- Stateless composables where possible
- Performance-optimized rendering
- Accessibility support

### ViewModel Layer

Responsible for storing and managing UI-related data, as well as handling presentation logic.

**TextEditorViewModel:**
- Manages text state and auto-save
- Handles text operations (copy, clear)
- Optimizes text rendering performance
- Manages keyboard and input state

**PomodoroTimerViewModel:**
- Manages Pomodoro timer logic and state (work sessions, short breaks, long breaks).
- Tracks session cycles and user progress.
- Communicates with `PomodoroService` to run the timer in the background.
- Sends events to `MainActivity` to display end-of-session notifications via `NotificationHelper`.
- Manages user-written text in "Focus Write" mode, saving it via `PreferencesManager`.

**MainViewModel:**
- Manages main UI state such as theme mode (dark/light).
- Saves and loads the user's last written text from `PreferencesManager`.
- Calculates word and character counts.
- Provides functions to clear text and save text manually (although auto-save is the primary mechanism).

**SettingsViewModel:**
- Manages user preferences configurable via the Settings screen.
- Saves and retrieves timer durations (focus, short break, long break) and long break interval from `PreferencesManager`.

**SettingsViewModel:**
- ✅ User preference management (IMPLEMENTED)
- Theme switching logic (PLANNED)
- ✅ Timer configuration (IMPLEMENTED)
- Word count toggle (PLANNED)

### Data Layer

Responsible for providing and manipulating application data. Currently, the data layer heavily relies on `PreferencesManager` which uses `SharedPreferences`.

**TextRepository:**
- Text persistence to SharedPreferences
- Auto-save scheduling
- Text retrieval and backup
- Performance-optimized storage

**TimerRepository:**
- Timer state persistence
- Session history (minimal)
- Timer configuration storage
- Background state management

**PreferencesManager (using SharedPreferences):**
- Acts as the primary source of persistent data storage for this MVP.
- Stores user preferences such as timer durations, theme mode, and the last text written in "Focus Write" mode.
- Provides functions to save, retrieve, and delete preference data.
- `ProtoDataStore` with `UserSettings` is a mechanism considered for more structured preference management in the future, but for now, `SharedPreferences` via `PreferencesManager` is the implemented solution.

*Note on Room:* A `Room` database is considered for future features like storing session statistics or multiple documents. However, for the current MVP, `Room` is not implemented, and primary data storage is handled via `PreferencesManager`.

### System Services Layer

Provides functionality that runs in the background or interacts with the Android system.

**PomodoroService (Foreground Service):**
- Responsible for accurately running Pomodoro timer logic even when the application is in the background.
- Displays a persistent foreground notification to inform the user that the timer is active.
- Receives commands (start, pause, reset) from `PomodoroTimerViewModel` via `Intent`.
- Does not directly interact with `PomodoroTimerViewModel` via `SharedFlow` in the current implementation; communication is one-way from ViewModel to Service via `Intent`, and the Service runs independently for the timer.
- Manages its own lifecycle and ensures the timer continues running during active sessions.

**NotificationHelper:**
- Utility for creating and displaying notifications to the user.
- Used by `MainActivity` (based on events from `PomodoroTimerViewModel`) to notify the end of a focus or break session.
- Manages notification channels and ensures notifications are displayed correctly according to the Android version.

**NotificationHelper:**
- Manages notification channels and permissions
- Creates and displays timer notifications
- Handles notification actions and updates
- Provides consistent notification styling

**PerformanceMonitor:**
- Tracks app performance metrics
- Monitors memory usage and performance
- Provides debugging and optimization insights
- Mock implementation for testing

### Storage Layer

**SharedPreferences (via PreferencesManager):**
- Primary storage mechanism for the MVP.
- Used to store all user preferences and simple application data like the last text.

**Main SharedPreferences Key Structure (managed by `PreferencesManager` via `PrefKeys`):**
```
PureFocus_Preferences:
├── ${PrefKeys.KEY_DARK_MODE} (Boolean) - Theme mode (dark/light)
├── ${PrefKeys.KEY_FOCUS_WRITE_TEXT} (String) - Last text from Focus Write mode
├── ${PrefKeys.KEY_FOCUS_DURATION} (Int) - Focus session duration (minutes)
├── ${PrefKeys.KEY_SHORT_BREAK_DURATION} (Int) - Short break session duration (minutes)
├── ${PrefKeys.KEY_LONG_BREAK_DURATION} (Int) - Long break session duration (minutes)
├── ${PrefKeys.KEY_LONG_BREAK_INTERVAL} (Int) - Number of focus sessions before a long break
// ... (other keys as defined in PrefKeys.kt)
```

## Performance Considerations

### Memory Management
- Minimal object allocation during text input
- Efficient string handling for large texts
- Proper lifecycle management for ViewModels
- Memory leak prevention in coroutines

### UI Performance
- Compose recomposition optimization
- Lazy loading where applicable
- Efficient state management
- 60fps target maintenance

### Storage Performance
- Batched SharedPreferences writes
- Minimal I/O operations
- Efficient serialization for complex data
- Background thread usage for storage operations

## Security Considerations

### Data Protection
- Local data only (no network transmission)
- SharedPreferences automatic backup encryption
- No sensitive data storage
- Secure text handling in memory

### Privacy
- No analytics or tracking (MVP)
- No network permissions
- Local-only operation
- User data remains on device

## Testing Strategy

### Unit Testing
- ViewModel logic testing
- Repository functionality testing
- Utility function testing
- Performance benchmark testing

### UI Testing
- Compose UI testing
- User interaction testing
- Performance testing
- Accessibility testing

### Integration Testing
- End-to-end workflow testing
- Data persistence testing
- Timer functionality testing
- Theme switching testing

## Future Architecture Considerations

### Potential Enhancements (Post-MVP)
- Room database for multiple documents
- WorkManager for background tasks
- DataStore for complex preferences
- Modular architecture for feature expansion

### Scalability Considerations
- Plugin architecture for future features
- Modular UI components
- Extensible repository pattern
- Performance monitoring integration

---

**Document Status:** Finalized - MVP Complete  
**Next Review:** Not applicable (Project Complete)  
**Update Frequency:** Not applicable (Project Complete)

**Note:** This document reflects the architecture of the completed MVP.