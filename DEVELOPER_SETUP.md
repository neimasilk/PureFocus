# Panduan Setup Developer PureFocus

Dokumen ini menjelaskan cara menyiapkan lingkungan pengembangan untuk proyek PureFocus.

## Prasyarat

Pastikan Anda telah menginstal perangkat lunak berikut:

1.  **Android Studio:** Versi stabil terbaru (misalnya, Hedgehog atau yang lebih baru). Unduh dari [situs resmi Android Studio](https://developer.android.com/studio).
2.  **Java Development Kit (JDK):** Android Studio biasanya menyertakan JDK sendiri, tetapi memiliki JDK sistem yang terinstal (misalnya, JDK 17 atau 11) bisa berguna. Pastikan `JAVA_HOME` environment variable tersetting dengan benar jika Anda mengelola JDK secara manual.
3.  **Git:** Untuk kloning repositori dan manajemen versi. Unduh dari [git-scm.com](https://git-scm.com/).

## Langkah-langkah Setup

1.  **Kloning Repositori:**
    Buka terminal atau Git Bash, navigasi ke direktori tempat Anda ingin menyimpan proyek, dan jalankan perintah berikut:
    ```bash
    git clone [URL Repositori PureFocus Anda]
    cd PureFocus
    ```
    Ganti `[URL Repositori PureFocus Anda]` dengan URL aktual repositori Git proyek ini.

2.  **Buka Proyek di Android Studio:**
    *   Luncurkan Android Studio.
    *   Pilih "Open an Existing Project".
    *   Navigasi ke direktori tempat Anda mengkloning repositori PureFocus dan pilih folder root proyek.
    *   Android Studio akan mulai mengimpor proyek dan mengunduh dependensi Gradle yang diperlukan. Proses ini mungkin memakan waktu beberapa menit tergantung pada koneksi internet Anda.

3.  **Konfigurasi Emulator atau Perangkat Fisik:**
    *   **Emulator:**
        *   Di Android Studio, buka AVD Manager (Tools > AVD Manager).
        *   Buat Virtual Device baru jika Anda belum memilikinya. Pilih perangkat keras dan versi sistem Android yang sesuai (disarankan API level terbaru atau yang mendekati target SDK proyek).
    *   **Perangkat Fisik:**
        *   Aktifkan "Developer Options" dan "USB Debugging" di perangkat Android Anda.
        *   Hubungkan perangkat ke komputer Anda melalui USB.
        *   Izinkan debugging USB jika diminta di perangkat.

4.  **Build dan Jalankan Aplikasi:**
    *   Setelah Gradle sync selesai dan perangkat/emulator Anda siap, pilih konfigurasi run "app" dari dropdown di toolbar Android Studio.
    *   Pilih target perangkat (emulator atau perangkat fisik yang terhubung).
    *   Klik tombol "Run" (ikon segitiga hijau) atau tekan `Shift + F10`.
    *   Android Studio akan membangun aplikasi dan menginstalnya di perangkat/emulator yang dipilih.

5.  **Verifikasi Instalasi:**
    *   Aplikasi PureFocus seharusnya diluncurkan secara otomatis.
    *   Periksa fungsionalitas dasar untuk memastikan semuanya berjalan dengan benar.

## Struktur Proyek Utama

Berikut adalah gambaran singkat tentang direktori penting dalam proyek:

*   `app/src/main/java/com/neimasilk/purefocus/`: Berisi kode sumber utama aplikasi (Kotlin).
    *   `data/`: Kelas terkait manajemen data (misalnya, `PreferencesManager`).
    *   `di/`: Modul Dependency Injection (misalnya, Hilt/Koin jika digunakan).
    *   `model/`: Kelas model data.
    *   `service/`: Layanan latar belakang (misalnya, `PomodoroService`).
    *   `ui/`: Komponen UI (Composable functions, ViewModel, Navigasi).
        *   `screens/`: Layar-layar spesifik dalam aplikasi.
        *   `theme/`: Definisi tema aplikasi.
    *   `util/`: Kelas utilitas.
*   `app/src/main/res/`: Sumber daya aplikasi (layout XML (jika ada), drawable, string, dll.).
*   `app/src/test/`: Unit test.
*   `app/src/androidTest/`: Instrumented test.
*   `build.gradle.kts` (Project level): Konfigurasi build untuk seluruh proyek.
*   `app/build.gradle.kts` (Module level): Konfigurasi build untuk modul `app`, termasuk dependensi.

## Perintah Gradle Umum

(Lihat juga `manual-tasks-guide.md` untuk detail lebih lanjut)

*   Membersihkan build: `./gradlew clean`
*   Membangun APK debug: `./gradlew assembleDebug`
*   Menjalankan unit test: `./gradlew testDebugUnitTest`
*   Menjalankan instrumented test: `./gradlew connectedDebugAndroidTest`

## Troubleshooting Setup

*   **Gradle Sync Failed:**
    *   Pastikan koneksi internet Anda stabil.
    *   Coba "File > Invalidate Caches / Restart..." di Android Studio.
    *   Periksa pesan error di tab "Build" untuk petunjuk spesifik.
    *   Pastikan versi Gradle dan Android Gradle Plugin kompatibel.
*   **Emulator Tidak Berjalan atau Lambat:**
    *   Pastikan HAXM (untuk prosesor Intel) atau AMD Hypervisor diinstal dan diaktifkan.
    *   Alokasikan RAM yang cukup untuk emulator.
    *   Coba "Cold Boot Now" dari AVD Manager.
*   **Perangkat Fisik Tidak Terdeteksi:**
    *   Pastikan driver USB perangkat terinstal dengan benar di komputer Anda.
    *   Coba kabel USB yang berbeda atau port USB yang berbeda.
    *   Pastikan USB Debugging diaktifkan dan diizinkan.

Jika Anda mengalami masalah lain, silakan merujuk ke dokumentasi Android Studio atau cari solusi secara online berdasarkan pesan error yang Anda terima.