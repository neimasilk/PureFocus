# Developer Setup Guide

This comprehensive guide will help you set up the development environment for PureFocus, a minimalist focus writing app built with Kotlin and Jetpack Compose.

## Prerequisites

### Required Software
*   **Android Studio** (Hedgehog 2023.1.1 or later)
*   **JDK 17** (recommended) or JDK 11 minimum
*   **Git** for version control
*   **Android SDK** with the following components:
    - Android SDK Platform 34 (API level 34)
    - Android SDK Build-Tools 34.0.0
    - Android Emulator (for testing)

### System Requirements
*   **RAM**: 8GB minimum, 16GB recommended
*   **Storage**: 4GB free space for Android Studio + 2GB for project
*   **OS**: Windows 10/11, macOS 10.14+, or Linux (64-bit)

## Quick Setup

### 1. Clone the Repository

```bash
git clone [repository-url]
cd PureFocus
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select "Open" from the welcome screen
3. Navigate to the cloned PureFocus directory
4. Click "OK" and wait for project indexing

### 3. Automatic Project Sync

*   Android Studio will automatically detect the Gradle project
*   Click "Sync Now" if prompted
*   Wait for dependency resolution (may take 2-5 minutes on first run)
*   Resolve any SDK or dependency issues if prompted

### 4. Verify Setup

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Install debug APK
./gradlew installDebug
```

## Device Configuration

### Emulator Setup
*   Open AVD Manager (Tools > AVD Manager)
*   Create a new Virtual Device with API level 34
*   Allocate at least 2GB RAM for optimal performance

### Physical Device Setup
*   Enable Developer Options and USB Debugging
*   Connect via USB and authorize debugging
*   Ensure device runs Android 7.0 (API 24) or higher

## Build and Run

1. Select "app" configuration in Android Studio toolbar
2. Choose your target device (emulator or physical)
3. Click Run (▶️) or press `Shift + F10`
4. Verify the PureFocus app launches successfully

## Project Architecture Overview

### Directory Structure

```
PureFocus/
├── app/
│   ├── src/main/java/com/purefocus/
│   │   ├── ui/
│   │   │   ├── screens/         # Compose screens (FocusWrite, Settings)
│   │   │   ├── components/      # Reusable UI components
│   │   │   └── theme/           # Material Design theme
│   │   ├── viewmodel/           # MVVM ViewModels
│   │   │   ├── FocusWriteViewModel.kt
│   │   │   ├── SettingsViewModel.kt
│   │   │   ├── MainViewModel.kt
│   │   │   └── PomodoroTimerViewModel.kt
│   │   ├── data/
│   │   │   ├── PreferencesManager.kt  # SharedPreferences wrapper
│   │   │   └── models/          # Data classes
│   │   ├── service/
│   │   │   └── PomodoroService.kt     # Background timer service
│   │   ├── utils/               # Utility classes
│   │   └── MainActivity.kt      # Single activity + Compose
│   ├── src/main/res/            # Android resources
│   └── build.gradle.kts         # App-level build configuration
├── memory-bank/                 # Project documentation
├── build.gradle.kts             # Project-level build configuration
└── gradle/                      # Gradle wrapper
```

### Key Technologies
*   **UI**: Jetpack Compose with Material Design 3
*   **Architecture**: MVVM with ViewModels and Compose State
*   **Storage**: SharedPreferences via PreferencesManager
*   **Background**: Foreground Service for timer persistence
*   **Navigation**: Compose Navigation (single-activity)

## Development Commands

### Essential Gradle Tasks

```bash
# Clean and build
./gradlew clean build

# Run all tests
./gradlew test

# Install debug build
./gradlew installDebug

# Run app directly
./gradlew installDebug && adb shell am start -n com.purefocus/.MainActivity
```

### Code Quality

```bash
# Run lint analysis
./gradlew lint

# Generate lint report
./gradlew lintDebug

# Check code formatting (if ktlint configured)
./gradlew ktlintCheck
```

### Build Variants

```bash
# Debug build (default)
./gradlew assembleDebug

# Release build (requires signing)
./gradlew assembleRelease

# Install specific variant
./gradlew installDebug
./gradlew installRelease
```

### Testing

```bash
# Unit tests only
./gradlew testDebugUnitTest

# Generate test coverage report
./gradlew testDebugUnitTestCoverage

# Run specific test class
./gradlew test --tests "*ViewModelTest"
```

## Development Workflow

### Branch Strategy
*   `main`: Stable production code
*   `develop`: Integration branch for features
*   `feature/*`: Individual feature branches
*   `bugfix/*`: Bug fix branches
*   `hotfix/*`: Critical production fixes

### Code Standards
*   **Kotlin**: Follow official Kotlin coding conventions
*   **Compose**: Use Material Design 3 components
*   **Architecture**: Maintain MVVM separation of concerns
*   **Comments**: Add KDoc for public APIs
*   **Testing**: Write unit tests for ViewModels and business logic

### Pre-commit Checklist
```bash
# Run before committing
./gradlew clean build test lint
```

### Performance Considerations
*   **Compose**: Minimize recomposition with `remember` and `derivedStateOf`
*   **Background**: Use efficient timer implementation in PomodoroService
*   **Memory**: Avoid memory leaks in ViewModels and Services
*   **Startup**: Keep app launch time under 1 second

## Troubleshooting Setup

### Gradle Issues

**Sync fails:**
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/
./gradlew clean

# In Android Studio: File → Invalidate Caches and Restart
```

**Dependency resolution errors:**
*   Check internet connection and proxy settings
*   Verify `repositories` block in build.gradle.kts
*   Try Gradle offline mode: `./gradlew --offline build`

**Build configuration issues:**
*   Ensure JDK 17 is selected in Android Studio
*   Check Android SDK path in Project Structure
*   Verify Gradle wrapper version compatibility

### Android Studio Issues

**Slow performance:**
*   Increase IDE memory: Help → Edit Custom VM Options
*   Add: `-Xmx4g -XX:ReservedCodeCacheSize=512m`
*   Disable unused plugins
*   Exclude build directories from antivirus scanning

**Compose preview not working:**
*   Ensure `@Preview` annotations are correct
*   Check if Compose compiler version matches runtime
*   Try Build → Clean Project

### Device/Emulator Issues

**Emulator won't start:**
*   Enable hardware acceleration (Intel HAXM/AMD-V)
*   Allocate sufficient RAM (2GB minimum)
*   Check available disk space
*   Try cold boot: AVD Manager → Wipe Data

**App installation fails:**
```bash
# Check device connection
adb devices

# Clear app data
adb uninstall com.purefocus

# Reinstall
./gradlew installDebug
```

**USB debugging issues:**
*   Revoke USB debugging authorizations on device
*   Try different USB cable/port
*   Install device-specific drivers
*   Enable "Transfer files" mode on device

### Runtime Issues

**App crashes on startup:**
*   Check logcat for stack traces: `adb logcat | grep PureFocus`
*   Verify all required permissions in AndroidManifest.xml
*   Test on different API levels

**Timer service not working:**
*   Check battery optimization settings
*   Verify foreground service permissions
*   Test notification channel creation

**Performance problems:**
*   Use Android Studio Profiler
*   Check for memory leaks with LeakCanary
*   Monitor Compose recomposition with Layout Inspector

### Getting Help

**Internal Resources:**
*   Check `memory-bank/` documentation
*   Review existing GitHub issues
*   Consult architecture.md for design decisions

**External Resources:**
*   [Android Developer Documentation](https://developer.android.com/)
*   [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
*   [Kotlin Documentation](https://kotlinlang.org/docs/)

**Reporting Issues:**
*   Include Android Studio version and OS
*   Provide full error messages and stack traces
*   Describe steps to reproduce
*   Mention device/emulator specifications

---

**Ready to contribute?** Once setup is complete, you can build, test, and modify PureFocus. Check the project's contribution guidelines and start with small improvements to familiarize yourself with the codebase.