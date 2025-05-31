# PureFocus - Architecture Documentation

**Document Version:** 1.1  
**Date:** January 2025  
**Project:** PureFocus - Minimalist Focus Writing App  
**Status:** Phase 1 Complete - Core Features Implemented

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

**TextEditorViewModel:**
- Manages text state and auto-save
- Handles text operations (copy, clear)
- Optimizes text rendering performance
- Manages keyboard and input state

**PomodoroTimerViewModel:**
- ✅ Timer logic and state management (IMPLEMENTED)
- ✅ Session tracking (work/break cycles) (IMPLEMENTED)
- ✅ Notification scheduling (IMPLEMENTED)
- ✅ Focus write text integration and logging (IMPLEMENTED)
- Background timer persistence (PLANNED)

**SettingsViewModel:**
- ✅ User preference management (IMPLEMENTED)
- Theme switching logic (PLANNED)
- ✅ Timer configuration (IMPLEMENTED)
- Word count toggle (PLANNED)

### Data Layer

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

**PreferencesRepository:**
- User settings storage
- Theme preference management
- App configuration
- Performance settings

### System Services Layer

**PomodoroService (Foreground Service):**
- Maintains timer accuracy in background
- Displays persistent notification with timer status
- Integrates with PomodoroTimerViewModel via SharedFlow
- Handles service lifecycle management
- Ensures timer continues during app backgrounding

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

**SharedPreferences Structure:**
```
PureFocus_Preferences:
├── text_draft (String) - Current text content
├── theme_mode (String) - "light" | "dark" | "system"
├── pomodoro_work_duration (Int) - Work session minutes
├── pomodoro_break_duration (Int) - Break session minutes
├── pomodoro_long_break_duration (Int) - Long break minutes
├── word_count_enabled (Boolean) - Show word count
├── timer_state (String) - Current timer state JSON
└── last_save_timestamp (Long) - Auto-save tracking
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

**Document Status:** Living document - Updated throughout development  
**Next Review:** After Phase 0 completion  
**Update Frequency:** After each significant architectural change

**Note:** This document will be continuously updated as the architecture evolves during development. Each baby-step that introduces architectural changes should update this document accordingly.