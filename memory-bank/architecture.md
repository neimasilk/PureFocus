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

PureFocus menerapkan prinsip-prinsip Clean Architecture dengan pemisahan *concerns* untuk meningkatkan *testability* dan *maintainability*. Meskipun saat ini semua komponen berada dalam satu modul `app`, pemisahan logis antar lapisan tetap dijaga. Pemisahan fisik ke modul-modul terpisah (misalnya, `feature`, `domain`, `data`) adalah pertimbangan untuk evolusi proyek di masa mendatang.

Berikut adalah gambaran arsitektur tingkat tinggi:

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

Bertanggung jawab untuk menyimpan dan mengelola data terkait UI, serta menangani logika presentasi.

**TextEditorViewModel:**
- Manages text state and auto-save
- Handles text operations (copy, clear)
- Optimizes text rendering performance
- Manages keyboard and input state

**PomodoroTimerViewModel:**
- Mengelola logika dan state timer Pomodoro (sesi kerja, istirahat pendek, istirahat panjang).
- Melacak siklus sesi dan progres pengguna.
- Berkomunikasi dengan `PomodoroService` untuk menjalankan timer di background.
- Mengirim event ke `MainActivity` untuk menampilkan notifikasi akhir sesi melalui `NotificationHelper`.
- Mengelola teks yang ditulis pengguna dalam mode "Focus Write", menyimpannya melalui `PreferencesManager`.

**MainViewModel:**
- Mengelola state UI utama seperti mode tema (gelap/terang).
- Menyimpan dan memuat teks terakhir yang ditulis pengguna dari `PreferencesManager`.
- Menghitung jumlah kata dan karakter.
- Menyediakan fungsi untuk membersihkan teks dan menyimpan teks secara manual (meskipun auto-save adalah mekanisme utama).

**SettingsViewModel:**
- Mengelola preferensi pengguna yang dapat dikonfigurasi melalui layar Pengaturan.
- Menyimpan dan mengambil durasi timer (fokus, istirahat pendek, istirahat panjang) dan interval istirahat panjang dari `PreferencesManager`.

**SettingsViewModel:**
- ✅ User preference management (IMPLEMENTED)
- Theme switching logic (PLANNED)
- ✅ Timer configuration (IMPLEMENTED)
- Word count toggle (PLANNED)

### Data Layer

Bertanggung jawab untuk menyediakan dan memanipulasi data aplikasi. Saat ini, lapisan data sangat bergantung pada `PreferencesManager` yang menggunakan `SharedPreferences`.

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

**PreferencesManager (menggunakan SharedPreferences):**
- Bertindak sebagai sumber utama penyimpanan data persisten untuk MVP ini.
- Menyimpan preferensi pengguna seperti durasi timer, mode tema, dan teks terakhir yang ditulis dalam mode "Focus Write".
- Menyediakan fungsi untuk menyimpan, mengambil, dan menghapus data preferensi.
- `ProtoDataStore` dengan `UserSettings` adalah mekanisme yang dipertimbangkan untuk pengelolaan preferensi yang lebih terstruktur di masa depan, namun untuk saat ini `SharedPreferences` melalui `PreferencesManager` adalah implementasi yang digunakan.

*Catatan tentang Room:* `Room` database dipertimbangkan untuk fitur masa depan seperti penyimpanan statistik sesi atau beberapa dokumen. Namun, untuk MVP saat ini, `Room` belum diimplementasikan, dan penyimpanan data utama dilakukan melalui `PreferencesManager`.

### System Services Layer

Menyediakan fungsionalitas yang berjalan di background atau berinteraksi dengan sistem Android.

**PomodoroService (Foreground Service):**
- Bertanggung jawab untuk menjalankan logika timer Pomodoro secara akurat bahkan ketika aplikasi berada di background.
- Menampilkan notifikasi foreground yang persisten untuk memberitahu pengguna bahwa timer aktif.
- Menerima perintah (start, pause, reset) dari `PomodoroTimerViewModel` melalui `Intent`.
- Tidak secara langsung berinteraksi dengan `PomodoroTimerViewModel` melalui `SharedFlow` dalam implementasi saat ini; komunikasi bersifat satu arah dari ViewModel ke Service melalui `Intent`, dan Service berjalan independen untuk timer.
- Mengelola siklus hidupnya sendiri dan memastikan timer terus berjalan selama sesi aktif.

**NotificationHelper:**
- Utilitas untuk membuat dan menampilkan notifikasi kepada pengguna.
- Digunakan oleh `MainActivity` (berdasarkan event dari `PomodoroTimerViewModel`) untuk memberitahu akhir sesi fokus atau istirahat.
- Mengelola channel notifikasi dan memastikan notifikasi ditampilkan dengan benar sesuai versi Android.

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
- Mekanisme penyimpanan utama untuk MVP.
- Digunakan untuk menyimpan semua preferensi pengguna dan data aplikasi sederhana seperti teks terakhir.

**Struktur Kunci SharedPreferences yang Utama (dikelola oleh `PreferencesManager` melalui `PrefKeys`):**
```
PureFocus_Preferences:
├── ${PrefKeys.KEY_DARK_MODE} (Boolean) - Mode tema (gelap/terang)
├── ${PrefKeys.KEY_FOCUS_WRITE_TEXT} (String) - Teks terakhir dari mode Focus Write
├── ${PrefKeys.KEY_FOCUS_DURATION} (Int) - Durasi sesi fokus (menit)
// ... (kunci lain untuk durasi istirahat pendek, panjang, interval, dll.)
```
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