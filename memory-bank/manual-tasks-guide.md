# PureFocus - Manual Tasks Guide

**Document Version:** 1.1  
**Date:** January 2025  
**Purpose:** Guide for tasks that need to be done manually by the developer

## Status Update: Baby Steps 1-3 Completed ✅

**Baby Step 1 (Notifications):** DONE - Notification system for the end of focus sessions has been implemented
**Baby Step 2 (Settings UI):** DONE - Settings UI with focus duration input has been implemented
**Baby Step 3 (Focus Write Text Logging):** DONE - Text from FocusWriteScreen is successfully saved to Logcat when a focus session ends

## Verification Status for Baby-Step "Project Foundation Setup"

### ✅ Completed:
1. **Repository Creation** - GitHub repository has been created and configured
2. **Android Studio Setup** - Android project with Kotlin and Jetpack Compose has been created
3. **Basic Theme System** - Basic theme system has been implemented in `ui/theme/`

### 🔄 To Be Completed:

#### 1. Build Configuration for Performance (MANUAL)
**File:** `app/build.gradle.kts`

**What needs to be changed:**
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true  // Change from false to true
        isShrinkResources = true  // Add this line
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

**Steps:**
1. Open the `app/build.gradle.kts` file
2. Look for the `buildTypes > release` section
3. Change `isMinifyEnabled = false` to `isMinifyEnabled = true`
4. Add the line `isShrinkResources = true`
5. Sync project

#### 2. Implement MVVM Architecture (CAN BE ASSISTED BY AI)
**What needs to be created:**
- `app/src/main/java/com/neimasilk/purefocus/data/PreferencesManager.kt`
- `app/src/main/java/com/neimasilk/purefocus/ui/MainViewModel.kt`
- `app/src/main/java/com/neimasilk/purefocus/util/PerformanceMonitor.kt`
- `app/src/main/java/com/neimasilk/purefocus/PureFocusApplication.kt`

**Folder structure to be created:**
```
app/src/main/java/com/neimasilk/purefocus/
├── data/
│   └── PreferencesManager.kt
├── ui/
│   ├── MainViewModel.kt
│   └── theme/ (already exists)
├── util/
│   └── PerformanceMonitor.kt
├── MainActivity.kt (already exists)
└── PureFocusApplication.kt
```

#### 3. Update ProGuard Rules (MANUAL)
**File:** `app/proguard-rules.pro`

**Add the following configuration:**
```proguard
# Optimasi dasar
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Pengaturan untuk Kotlin
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep class kotlin.Metadata { *; }

# Pengaturan untuk Compose
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
```

#### 4. Update AndroidManifest.xml (MANUAL)
**File:** `app/src/main/AndroidManifest.xml`

**What needs to be changed:**
```xml
<application
    android:name=".PureFocusApplication"  <!-- Add this line -->
    android:allowBackup="true"
    ...
```

#### 5. Update MainActivity.kt (CAN BE ASSISTED BY AI)
**File:** `app/src/main/java/com/neimasilk/purefocus/MainActivity.kt`

Needs to be updated to use ViewModel and a persistent theme system.

#### 6. Update Theme.kt for Persistence (CAN BE ASSISTED BY AI)
**File:** `app/src/main/java/com/neimasilk/purefocus/ui/theme/Theme.kt`

Needs to be updated to support theme persistence.





## Manual Tasks for the Developer

### High Priority (Must be done now):

1. **Update build.gradle.kts** (5 minutes)
   - Enable minify and shrinkResources for release build

2. **Update proguard-rules.pro** (5 minutes)
   - Add optimization configuration

3. **Create folder structure** (2 minutes)
   - Create `data/` and `util/` folders within the main package

4. **Manual Testing** (15 minutes)
   - Build and run the application
   - Verify the application runs without errors
   - Test on a physical device if possible

### Can Be Assisted by AI:

1. **Implement PreferencesManager.kt**
2. **Implement MainViewModel.kt**
3. **Implement PerformanceMonitor.kt**
4. **Implement PureFocusApplication.kt**
5. **Update MainActivity.kt**
6. **Update Theme.kt for persistence**
7. **Update AndroidManifest.xml**

## Validation After Implementation

### Validation Checklist:
- [ ] Application builds without errors
- [ ] Application runs on emulator/device
- [ ] Release build is smaller than debug build
- [ ] Theme can be changed (if implemented)
- [ ] Theme persists after restart (if implemented)
- [ ] Performance logs appear in the debug console
- [ ] StrictMode is active in debug build

### Performance Targets:
- Startup time: < 1 second
- Memory usage: < 50MB
- APK size (release): < 10MB

## Next Steps

After all the above tasks are completed:
1. Update `progress.md` with the achieved progress
2. Delete `baby-step.md` (completed)
3. Ready for the next baby-step: "Text Editor Foundation"

---

**Note:** This file will be deleted after all manual tasks are completed.