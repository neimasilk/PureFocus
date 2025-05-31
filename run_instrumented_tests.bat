@echo off
REM Batch file untuk menjalankan instrumented tests dengan emulator
REM Harus dijalankan sebagai Administrator

echo ========================================
echo PureFocus - Instrumented Test Runner
echo ========================================
echo.

REM Set Android SDK path
set ANDROID_HOME=C:\Users\neima\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\emulator;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

echo [1/5] Checking Android SDK setup...
emulator -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Android SDK tidak ditemukan atau tidak dikonfigurasi dengan benar
    echo Pastikan Android Studio sudah terinstall di: %ANDROID_HOME%
    pause
    exit /b 1
)
echo ✓ Android SDK ditemukan

echo.
echo [2/5] Listing available AVDs...
emulator -list-avds
if %errorlevel% neq 0 (
    echo ERROR: Tidak ada AVD yang tersedia
    echo Silakan buat AVD melalui Android Studio terlebih dahulu
    pause
    exit /b 1
)

echo.
echo [3/5] Starting emulator Medium_Phone_API_36.0...
echo Tunggu hingga emulator selesai booting (biasanya 1-3 menit)
start "Android Emulator" emulator -avd Medium_Phone_API_36.0 -gpu swiftshader_indirect

echo.
echo [4/5] Waiting for emulator to boot...
echo Tekan ENTER setelah emulator menampilkan home screen Android
pause

echo.
echo [5/5] Running instrumented tests...
echo Menjalankan connectedAndroidTest...
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