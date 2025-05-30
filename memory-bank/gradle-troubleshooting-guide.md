# Panduan Troubleshooting Gradle - PureFocus Project

**Tanggal Pembuatan:** 3 Juni 2025  
**Status:** Rekomendasi Perbaikan  
**Terkait:** [debugging-notes.md](./debugging-notes.md)

## Masalah yang Ditemukan

### 1. Kesalahan Sintaks di `settings.gradle.kts`

**Gejala:**
- Error saat menjalankan perintah Gradle: `Unresolved reference: content`, `Unresolved reference: includeGroupByRegex`, dll.
- Build gagal dengan pesan error di file `settings.gradle.kts`

**Penyebab:**
- Struktur repositori tidak benar, dengan blok `content` yang berada di luar blok `google()`
- Referensi yang tidak terselesaikan seperti `content`, `includeGroupByRegex`, `mavenCentral()`, dll.

**Perbaikan yang Dilakukan:**
- Menyederhanakan konfigurasi repositori
- Menghapus blok `content` yang bermasalah
- Memastikan struktur yang benar untuk `pluginManagement` dan `dependencyResolutionManagement`

```kotlin
// Konfigurasi settings.gradle.kts yang benar
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PureFocus"
include(":app")
```

### 2. Dependensi Testing yang Dinonaktifkan

**Gejala:**
- Dependensi testing di `app/build.gradle.kts` dikomentari
- Test tidak dapat dijalankan karena dependensi yang diperlukan tidak tersedia

**Perbaikan yang Dilakukan:**
- Mengaktifkan kembali dependensi testing yang diperlukan
- Menggunakan Robolectric untuk testing dengan komponen Android yang nyata
- Memperbarui versi coroutines-test ke 1.10.2

```kotlin
// Konfigurasi dependensi testing yang benar
// Testing
testImplementation(libs.junit)
// Menggunakan Robolectric untuk testing dengan real Android components
testImplementation("org.robolectric:robolectric:4.11.1") // For Android unit testing
testImplementation("androidx.test:core:1.5.0") // For ApplicationProvider
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2") // For testing coroutines
testImplementation("app.cash.turbine:turbine:0.12.1") // For StateFlow testing

androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.ui.test.junit4)

// Debug
debugImplementation(libs.androidx.ui.tooling)
debugImplementation(libs.androidx.ui.test.manifest)
```

### 3. Masalah dengan Java Toolchain

**Gejala:**
- Error saat mencoba mengunduh JDK 17 selama build
- Build gagal dengan pesan error terkait `JavaToolchainQueryService.downloadToolchain`

**Perbaikan yang Dilakukan:**
- Menghapus konfigurasi Java toolchain untuk menggunakan Java default sistem

```kotlin
// Menghapus konfigurasi Java toolchain
// java {
//     toolchain {
//         languageVersion.set(JavaLanguageVersion.of(17))
//     }
// }
```

### 4. Masalah AbstractManagedExecutor

**Gejala:**
- Error `java.util.concurrent.RejectedExecutionException` terkait `org.gradle.internal.concurrent.AbstractManagedExecutor`
- Build gagal dengan cepat (sekitar 2-3 detik)

**Perbaikan yang Dilakukan:**
- Mengoptimalkan konfigurasi Gradle di `gradle.properties`:
  - Mengurangi memori JVM dari 2048m menjadi 1024m
  - Menambahkan batasan MaxMetaspaceSize
  - Menonaktifkan Gradle daemon dan eksekusi paralel

```properties
# Konfigurasi gradle.properties yang dioptimalkan
org.gradle.jvmargs=-Xmx1024m -Dfile.encoding=UTF-8 -XX:MaxMetaspaceSize=512m
# Disable daemon to avoid executor issues
org.gradle.daemon=false
# Disable parallel execution
org.gradle.parallel=false
```

## Error Baru: JDK Image Transform

**Gejala:**
- Error saat menjalankan Gradle dengan hak administrator
- Build gagal dengan pesan error terkait `JdkImageTransform` dan `jlink.exe`
- Error spesifik: `Error while executing process C:\Program Files\Android\Android Studio\jbr\bin\jlink.exe with arguments`

**Penyebab Potensial:**
- Masalah dengan transformasi core-for-system-modules.jar dari Android SDK
- Konflik antara JDK yang digunakan oleh Android Studio dan yang dibutuhkan oleh Gradle
- Masalah izin atau konfigurasi path pada jlink.exe

**Perbaikan yang Dicoba:**
- Menjalankan Gradle dengan hak administrator (tidak berhasil menyelesaikan masalah)

## Rekomendasi untuk Langkah Selanjutnya

Meskipun perbaikan di atas telah dilakukan, masalah dengan `AbstractManagedExecutor` dan sekarang `JdkImageTransform` masih terjadi. Berdasarkan file debugging-notes.md dan pengalaman terbaru, masalah ini memerlukan tindakan manual:

### 1. Perbaiki Masalah JDK Image Transform

- Periksa instalasi JDK dan pastikan path yang benar:
  ```powershell
  # Periksa versi Java yang terinstal
  java -version
  
  # Periksa variabel lingkungan JAVA_HOME
  echo $env:JAVA_HOME
  ```

- Pastikan Android SDK terinstal dengan benar dan variabel lingkungan diatur:
  ```powershell
  # Periksa variabel lingkungan ANDROID_HOME
  echo $env:ANDROID_HOME
  ```

- Coba gunakan JDK yang sama dengan yang digunakan oleh Android Studio:
  ```powershell
  # Atur JAVA_HOME ke JDK yang digunakan oleh Android Studio
  $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
  ./gradlew test --stacktrace
  ```

### 2. Perbaiki Masalah Transformasi Android SDK

- Periksa dan perbaiki instalasi Android SDK:
  ```powershell
  # Buka Android Studio dan buka SDK Manager
  # Reinstall Android SDK Platform 34
  ```

- Coba gunakan versi Android SDK yang berbeda dalam build.gradle.kts:
  ```kotlin
  android {
      compileSdk = 33 // Coba gunakan versi yang lebih lama
      // ...
  }
  ```

### 3. Bersihkan Cache Gradle Secara Menyeluruh

- Hapus direktori `.gradle` di proyek
- Hapus cache global Gradle di `C:\Users\[username]\.gradle`
- Hapus cache transformasi Gradle:
  ```powershell
  Remove-Item -Recurse -Force "C:\Users\neima\.gradle\caches\transforms-*"
  ```
- Jalankan `./gradlew --stop` untuk menghentikan semua daemon Gradle

### 4. Periksa Perangkat Lunak Keamanan

- Jika masalah berlanjut, periksa apakah ada antivirus atau firewall yang mungkin mengganggu
- Nonaktifkan sementara perangkat lunak keamanan untuk menguji apakah itu adalah penyebab masalah

### 5. Pertimbangkan Lingkungan Alternatif

- Jika memungkinkan, coba jalankan proyek di mesin atau lingkungan Windows yang berbeda
- Coba gunakan versi Gradle yang berbeda (misalnya downgrade ke versi yang lebih stabil)

## Kesimpulan

Masalah yang dihadapi proyek PureFocus tampaknya merupakan kombinasi dari beberapa faktor:

1. **Masalah Izin Sistem**: Error `AbstractManagedExecutor` yang awalnya ditemukan menunjukkan masalah terkait izin sistem, namun menjalankan dengan hak administrator tidak sepenuhnya menyelesaikan masalah.

2. **Konflik JDK/Android SDK**: Error baru terkait `JdkImageTransform` dan `jlink.exe` menunjukkan adanya konflik antara JDK yang digunakan oleh Android Studio dan yang dibutuhkan oleh Gradle, atau masalah dengan instalasi Android SDK.

3. **Masalah Cache Gradle**: Transformasi yang gagal di cache Gradle dapat menyebabkan error yang persisten bahkan setelah perubahan konfigurasi.

Pendekatan yang direkomendasikan adalah menyelesaikan masalah secara sistematis dengan fokus pada konfigurasi JDK dan Android SDK terlebih dahulu, kemudian membersihkan cache Gradle secara menyeluruh jika masalah berlanjut.

Jika semua langkah yang direkomendasikan di atas tidak berhasil, pertimbangkan untuk:

1. Membuat proyek Android baru dengan konfigurasi yang sama dan memigrasikan kode secara bertahap
2. Menggunakan Docker atau lingkungan terisolasi untuk pengembangan
3. Membuat laporan bug ke Gradle dan/atau Android dengan informasi sistem yang detail

---

**Catatan:** Dokumen ini diperbarui pada 3 Juni 2025 setelah menemukan error baru terkait JDK Image Transform. Dokumen akan terus diperbarui jika ada perkembangan baru atau solusi alternatif ditemukan.