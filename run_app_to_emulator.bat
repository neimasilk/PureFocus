@echo off
echo ========================================
echo PureFocus - Run App to Emulator
echo ========================================
echo.

REM Set Android environment variables
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

echo [1/6] Checking Android SDK setup...
if not exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    echo ERROR: Android SDK not found at %ANDROID_HOME%
    echo Please install Android Studio and SDK first.
    pause
    exit /b 1
)
echo ✅ Android SDK found

echo.
echo [2/6] Listing available AVDs...
emulator -list-avds
if %ERRORLEVEL% neq 0 (
    echo ERROR: No AVDs found. Please create an AVD first.
    pause
    exit /b 1
)

echo.
echo [3/6] Checking for running emulators...
"%ANDROID_HOME%\platform-tools\adb.exe" devices | findstr emulator
if %ERRORLEVEL% equ 0 (
    echo ✅ Emulator already running! Skipping emulator startup.
    goto :skip_emulator_start
)

echo Starting emulator (Medium_Phone_API_36.0)...
echo Note: Emulator will start in background. Wait for it to boot completely.
start /b emulator -avd Medium_Phone_API_36.0 -gpu swiftshader_indirect

echo.
echo [4/6] Waiting for emulator to boot...
echo Please wait while emulator starts up (this may take 1-2 minutes)...
echo Press any key when you see the Android home screen...
pause

:skip_emulator_start

echo.
echo [5/6] Verifying emulator connection...
"%ANDROID_HOME%\platform-tools\adb.exe" devices
if %ERRORLEVEL% neq 0 (
    echo ERROR: ADB not working properly
    pause
    exit /b 1
)

echo.
echo [6/6] Building and installing PureFocus app...
echo Building debug APK...
.\gradlew assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo Installing APK to emulator...
"%ANDROID_HOME%\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk
if %ERRORLEVEL% neq 0 (
    echo ERROR: Installation failed
    pause
    exit /b 1
)

echo.
echo ✅ SUCCESS! PureFocus installed successfully!
echo.
echo [AUTO-LAUNCH] Starting PureFocus app...
echo Launching PureFocus...
"%ANDROID_HOME%\platform-tools\adb.exe" shell am start -n com.neimasilk.purefocus/.MainActivity
if %ERRORLEVEL% equ 0 (
    echo ✅ App launched successfully! Check your emulator.
) else (
    echo ❌ Failed to launch app automatically.
    echo You can manually open PureFocus from the emulator.
)

echo.
echo ========================================
echo PureFocus deployment complete!
echo ========================================
echo.
echo To manually launch the app later:
echo adb shell am start -n com.neimasilk.purefocus/.MainActivity
echo.
echo To uninstall:
echo adb uninstall com.neimasilk.purefocus
echo.
pause