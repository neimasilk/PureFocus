# PureFocus - Panduan Tugas Manual

**Document Version:** 1.1  
**Date:** January 2025  
**Purpose:** Panduan untuk tugas-tugas yang perlu dilakukan secara manual oleh developer

## Status Update: Baby Steps 1-3 Completed ✅

**Baby Step 1 (Notifikasi):** SELESAI - Sistem notifikasi untuk akhir sesi fokus telah diimplementasikan
**Baby Step 2 (Settings UI):** SELESAI - UI pengaturan dengan input durasi fokus telah diimplementasikan
**Baby Step 3 (Focus Write Text Logging):** SELESAI - Teks dari FocusWriteScreen berhasil disimpan ke Logcat saat sesi fokus berakhir

## Status Verifikasi Baby-Step "Project Foundation Setup"

### ✅ Yang Sudah Selesai:
1. **Repository Creation** - Repository GitHub sudah dibuat dan dikonfigurasi
2. **Android Studio Setup** - Proyek Android dengan Kotlin dan Jetpack Compose sudah dibuat
3. **Basic Theme System** - Sistem tema dasar sudah diimplementasikan di `ui/theme/`

### 🔄 Yang Perlu Diselesaikan:

#### 1. Konfigurasi Build untuk Performance (MANUAL)
**File:** `app/build.gradle.kts`

**Yang perlu diubah:**
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true  // Ubah dari false ke true
        isShrinkResources = true  // Tambahkan baris ini
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

**Langkah:**
1. Buka file `app/build.gradle.kts`
2. Cari bagian `buildTypes > release`
3. Ubah `isMinifyEnabled = false` menjadi `isMinifyEnabled = true`
4. Tambahkan baris `isShrinkResources = true`
5. Sync project

#### 2. Implementasi MVVM Architecture (DAPAT DIBANTU AI)
**Yang perlu dibuat:**
- `app/src/main/java/com/neimasilk/purefocus/data/PreferencesManager.kt`
- `app/src/main/java/com/neimasilk/purefocus/ui/MainViewModel.kt`
- `app/src/main/java/com/neimasilk/purefocus/util/PerformanceMonitor.kt`
- `app/src/main/java/com/neimasilk/purefocus/PureFocusApplication.kt`

**Struktur folder yang perlu dibuat:**
```
app/src/main/java/com/neimasilk/purefocus/
├── data/
│   └── PreferencesManager.kt
├── ui/
│   ├── MainViewModel.kt
│   └── theme/ (sudah ada)
├── util/
│   └── PerformanceMonitor.kt
├── MainActivity.kt (sudah ada)
└── PureFocusApplication.kt
```

#### 3. Update ProGuard Rules (MANUAL)
**File:** `app/proguard-rules.pro`

**Tambahkan konfigurasi berikut:**
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

**Yang perlu diubah:**
```xml
<application
    android:name=".PureFocusApplication"  <!-- Tambahkan baris ini -->
    android:allowBackup="true"
    ...
```

#### 5. Update MainActivity.kt (DAPAT DIBANTU AI)
**File:** `app/src/main/java/com/neimasilk/purefocus/MainActivity.kt`

Perlu diupdate untuk menggunakan ViewModel dan sistem tema yang persisten.

#### 6. Update Theme.kt untuk Persistence (DAPAT DIBANTU AI)
**File:** `app/src/main/java/com/neimasilk/purefocus/ui/theme/Theme.kt`

Perlu diupdate untuk mendukung persistensi tema.

## Tugas Manual yang Harus Dilakukan Developer

### Prioritas Tinggi (Harus dilakukan sekarang):

1. **Update build.gradle.kts** (5 menit)
   - Enable minify dan shrinkResources untuk release build

2. **Update proguard-rules.pro** (5 menit)
   - Tambahkan konfigurasi optimasi

3. **Buat struktur folder** (2 menit)
   - Buat folder `data/` dan `util/` di dalam package utama

4. **Testing Manual** (15 menit)
   - Build dan jalankan aplikasi
   - Verifikasi aplikasi berjalan tanpa error
   - Test di perangkat fisik jika memungkinkan

### Dapat Dibantu AI:

1. **Implementasi PreferencesManager.kt**
2. **Implementasi MainViewModel.kt**
3. **Implementasi PerformanceMonitor.kt**
4. **Implementasi PureFocusApplication.kt**
5. **Update MainActivity.kt**
6. **Update Theme.kt untuk persistence**
7. **Update AndroidManifest.xml**

## Validasi Setelah Implementasi

### Checklist Validasi:
- [ ] Aplikasi build tanpa error
- [ ] Aplikasi berjalan di emulator/perangkat
- [ ] Release build lebih kecil dari debug build
- [ ] Tema dapat diubah (jika sudah diimplementasi)
- [ ] Tema persisten setelah restart (jika sudah diimplementasi)
- [ ] Log performa muncul di debug console
- [ ] StrictMode aktif di debug build

### Performance Targets:
- Startup time: < 1 detik
- Memory usage: < 50MB
- APK size (release): < 10MB

## Langkah Selanjutnya

Setelah semua tugas di atas selesai:
1. Update `progress.md` dengan kemajuan yang dicapai
2. Hapus `baby-step.md` (sudah selesai)
3. Siap untuk baby-step berikutnya: "Text Editor Foundation"

---

**Catatan:** File ini akan dihapus setelah semua tugas manual selesai dilakukan.