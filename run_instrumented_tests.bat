@echo off
REM Batch file untuk menjalankan instrumented tests di device fisik
echo ================================================================
echo PureFocus - Run Instrumented Tests on Physical Device
echo ================================================================
echo.

REM Set environment variables
set ANDROID_HOME=C:\Users\neima\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

REM Check if Android SDK exists
if not exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    echo ERROR: Android SDK not found at %ANDROID_HOME%
    echo Please install Android Studio first.
    pause
    exit /b 1
)

echo [1/5] Checking connected devices...
echo Available devices:
"%ANDROID_HOME%\platform-tools\adb.exe" devices -l

REM Check if any device is connected
for /f "skip=1 tokens=1" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" devices') do (
    if not "%%i"=="" (
        echo ✅ Device detected: %%i
        goto :device_found
    )
)

echo ❌ ERROR: No device detected!
echo.
echo Please make sure:
echo 1. Your device is connected via USB
echo 2. USB Debugging is enabled in Developer Options
echo 3. You have authorized the computer on your device
echo.
echo To enable USB Debugging:
echo 1. Go to Settings ^> About Phone
echo 2. Tap Build Number 7 times to enable Developer Options
echo 3. Go to Settings ^> Developer Options
echo 4. Enable USB Debugging
pause
exit /b 1

:device_found
echo.
echo [2/5] Getting device information...
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.product.model 2^>nul') do echo Device Model: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.build.version.release 2^>nul') do echo Android Version: %%i
for /f "tokens=*" %%i in ('"%ANDROID_HOME%\platform-tools\adb.exe" shell getprop ro.build.version.sdk 2^>nul') do echo API Level: %%i

echo.
echo [3/5] Building test APK...
echo This may take a few minutes...
echo.

.\gradlew assembleDebugAndroidTest

if %ERRORLEVEL% neq 0 (
    echo ❌ Build test APK failed!
    echo.
    echo Debugging build errors:
    echo 1. Check internet connection for dependency downloads
    echo 2. Try cleaning project: .\gradlew clean
    echo 3. Check for syntax errors in test code
    echo 4. Verify test dependencies in build.gradle
    pause
    exit /b 1
)

echo ✅ Test APK built successfully!
echo.
echo [4/5] Installing app and test APK...
echo Installing main app...
.\gradlew installDebug

if %ERRORLEVEL% neq 0 (
    echo ❌ Failed to install main app!
    echo Check device storage and permissions.
    pause
    exit /b 1
)

echo Installing test APK...
.\gradlew installDebugAndroidTest

if %ERRORLEVEL% neq 0 (
    echo ❌ Failed to install test APK!
    echo Check device storage and permissions.
    pause
    exit /b 1
)

echo ✅ Installation successful!
echo.
echo [5/5] Running instrumented tests...
echo Running tests on physical device...
echo This may take several minutes depending on test complexity...
echo.

.\gradlew connectedAndroidTest

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✓ SEMUA TEST BERHASIL!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ✗ TEST GAGAL - Periksa output di atas
    echo ========================================
)

echo.
echo Tekan ENTER untuk menutup...
pause