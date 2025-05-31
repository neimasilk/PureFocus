# PureFocus Developer Setup Guide

This document describes how to set up the development environment for the PureFocus project.

## Prerequisites

Ensure you have the following software installed:

1.  **Android Studio:** Latest stable version (e.g., Hedgehog or newer). Download from the [official Android Studio website](https://developer.android.com/studio).
2.  **Java Development Kit (JDK):** Android Studio usually includes its own JDK, but having a system JDK installed (e.g., JDK 17 or 11) can be useful. Ensure the `JAVA_HOME` environment variable is set correctly if you manage JDKs manually.
3.  **Git:** For cloning the repository and version management. Download from [git-scm.com](https://git-scm.com/).

## Setup Steps

1.  **Clone the Repository:**
    Open a terminal or Git Bash, navigate to the directory where you want to store the project, and run the following command:
    ```bash
    git clone [Your PureFocus Repository URL]
    cd PureFocus
    ```
    Replace `[Your PureFocus Repository URL]` with the actual Git repository URL for this project.

2.  **Open Project in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an Existing Project".
    *   Navigate to the directory where you cloned the PureFocus repository and select the project's root folder.
    *   Android Studio will start importing the project and downloading the necessary Gradle dependencies. This process might take a few minutes depending on your internet connection.

3.  **Configure Emulator or Physical Device:**
    *   **Emulator:**
        *   In Android Studio, open AVD Manager (Tools > AVD Manager).
        *   Create a new Virtual Device if you don't already have one. Choose appropriate hardware and Android system version (latest API level or one close to the project's target SDK is recommended).
    *   **Physical Device:**
        *   Enable "Developer Options" and "USB Debugging" on your Android device.
        *   Connect your device to your computer via USB.
        *   Allow USB debugging if prompted on the device.

4.  **Build and Run the Application:**
    *   Once Gradle sync is complete and your device/emulator is ready, select the "app" run configuration from the dropdown in the Android Studio toolbar.
    *   Select your target device (emulator or connected physical device).
    *   Click the "Run" button (green triangle icon) or press `Shift + F10`.
    *   Android Studio will build the application and install it on the selected device/emulator.

5.  **Verify Installation:**
    *   The PureFocus application should launch automatically.
    *   Check basic functionalities to ensure everything is working correctly.

## Main Project Structure

Here is a brief overview of important directories in the project:

*   `app/src/main/java/com/neimasilk/purefocus/`: Contains the main application source code (Kotlin).
    *   `data/`: Classes related to data management (e.g., `PreferencesManager`).
    *   `di/`: Dependency Injection modules (e.g., Hilt/Koin if used).
    *   `model/`: Data model classes.
    *   `service/`: Background services (e.g., `PomodoroService`).
    *   `ui/`: UI components (Composable functions, ViewModels, Navigation).
        *   `screens/`: Specific screens within the application.
        *   `theme/`: Application theme definitions.
    *   `util/`: Utility classes.
*   `app/src/main/res/`: Application resources (XML layouts (if any), drawables, strings, etc.).
*   `app/src/test/`: Unit tests.
*   `app/src/androidTest/`: Instrumented tests.
*   `build.gradle.kts` (Project level): Build configuration for the entire project.
*   `app/build.gradle.kts` (Module level): Build configuration for the `app` module, including dependencies.

## Common Gradle Commands

(See also `manual-tasks-guide.md` for more details)

*   Clean build: `./gradlew clean`
*   Build debug APK: `./gradlew assembleDebug`
*   Run unit tests: `./gradlew testDebugUnitTest`
*   Run instrumented tests: `./gradlew connectedDebugAndroidTest`

## Troubleshooting Setup

*   **Gradle Sync Failed:**
    *   Ensure your internet connection is stable.
    *   Try "File > Invalidate Caches / Restart..." in Android Studio.
    *   Check error messages in the "Build" tab for specific clues.
    *   Ensure Gradle version and Android Gradle Plugin version are compatible.
*   **Emulator Not Running or Slow:**
    *   Ensure HAXM (for Intel processors) or AMD Hypervisor is installed and enabled.
    *   Allocate sufficient RAM for the emulator.
    *   Try "Cold Boot Now" from the AVD Manager.
*   **Physical Device Not Detected:**
    *   Ensure the device's USB drivers are correctly installed on your computer.
    *   Try a different USB cable or a different USB port.
    *   Ensure USB Debugging is enabled and authorized.

If you encounter other issues, please refer to the Android Studio documentation or search for solutions online based on the error messages you receive.