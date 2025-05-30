# Status & To-Do List Suggestions

**Project:** PureFocus - Aplikasi Menulis Minimalis dengan Timer Pomodoro
**Last Updated:** 3 Juni 2025
**Author:** Neima Silk
**Status:** Phase 1 Completed (50%), Phase 2 In Progress (Baby-Step 2.1 Dimulai)

## Current Progress

**Current Task:** Baby-Step 2.1: "Basic Timer Implementation"

**Phase 1 (Core Text Editor) Progress:** 50% (Baby-Step 1.1 dan 1.2 selesai)

**Recently Completed:**
* Implementasi fungsionalitas "Salin Teks" dengan menu konteks dan Toast feedback.
* Migrasi dari String ke TextFieldValue untuk manajemen posisi kursor.
* Verifikasi seleksi teks dasar berfungsi dengan baik.
* Optimasi penanganan teks untuk dokumen besar dengan profiling dan pengujian performa.

**Issues Resolved:**
* Fixed MainViewModelTests dengan mengupdate mockito-kotlin ke versi yang kompatibel dengan Kotlin 1.8.10.
* Resolved issue dengan posisi kursor yang hilang saat rotasi layar atau navigasi dengan mengimplementasikan TextFieldValue di MainViewModel dan FocusWriteScreen.
* Ditambahkan unit test untuk memverifikasi fungsionalitas TextFieldValue dan manajemen posisi kursor.

**Next Task (Baby-Step 2.2):** "Timer UI Integration with FocusWriteScreen"
* Desain dan implementasikan UI timer yang minimalis di FocusWriteScreen.
* Tambahkan kontrol dasar (mulai, jeda, reset) untuk timer.
* Implementasikan notifikasi sederhana untuk transisi antar sesi.

## High-Priority To-Do List (Near Future)

1.  **Baby-Step 2.1: Implementasi Timer Dasar (Current)**
    * Buat `PomodoroViewModel` untuk mengelola state dan logika timer.
    * Implementasikan timer dasar dengan sesi kerja 25 menit dan istirahat 5 menit.
    * Pastikan timer dapat berjalan di background (minimal selama aplikasi aktif).

2.  **Baby-Step 2.2: Integrasi UI Timer dengan FocusWriteScreen**
    * Tambahkan UI minimal untuk timer di `FocusWriteScreen`.
    * Implementasikan notifikasi sederhana untuk transisi sesi.

3.  **Baby-Step 2.3: Manajemen Sesi**
    * Implementasikan pelacakan sesi yang telah diselesaikan.
    * Tambahkan statistik dasar (jumlah sesi, total waktu fokus).

4.  **Verifikasi bahwa target performa inti tetap terpenuhi.**

## Baby-Step To-Do List Suggestion (Untuk Baby-Step 2.1: "Basic Timer Implementation")

**Objective:** Mengimplementasikan fungsionalitas timer Pomodoro dasar yang dapat berjalan di background (minimal selama aplikasi aktif) dan mendukung sesi kerja 25 menit dan istirahat pendek 5 menit.

**Estimated Duration:** 3-4 hari

**Detailed Tasks:**

1.  **Implementasi ViewModel untuk Timer Pomodoro (`PomodoroViewModel.kt`)**
    * **Deskripsi:** Membuat ViewModel yang mengelola state dan logika timer Pomodoro.
    * **Files to Create/Modify:** `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroViewModel.kt`
    * **Implementation Steps:**
        1.  Buat data class `PomodoroState` untuk menyimpan state timer (waktu tersisa, status sesi, dll).
        2.  Implementasikan logika untuk menghitung waktu mundur menggunakan coroutine flow.
        3.  Tambahkan fungsi untuk memulai, menjeda, dan mereset timer.
        4.  Implementasikan transisi otomatis antara sesi kerja dan istirahat.
    * **Acceptance Criteria:**
        * Timer dapat dihitung mundur dengan akurat.
        * Transisi antara sesi kerja dan istirahat berfungsi dengan benar.
        * State timer dipertahankan selama aplikasi aktif.
    * **Validation Method:**
        * Uji timer dengan berbagai durasi dan verifikasi akurasi waktu.
        * Verifikasi transisi otomatis antara sesi kerja dan istirahat.

2.  **Integrasi Timer dengan Lifecycle Aplikasi (`MainActivity.kt`)**
    * **Deskripsi:** Memastikan timer tetap berjalan bahkan ketika aplikasi berada di background (minimal selama aplikasi aktif).
    * **Files to Create/Modify:** `app/src/main/java/com/neimasilk/purefocus/MainActivity.kt`
    * **Implementation Steps:**
        1.  Inisialisasi `PomodoroViewModel` di `MainActivity`.
        2.  Implementasikan logika untuk menangani lifecycle events (onPause, onResume) untuk mempertahankan state timer.
        3.  Gunakan `LifecycleObserver` untuk mengelola timer berdasarkan lifecycle aplikasi.
    * **Acceptance Criteria:**
        * Timer tetap berjalan ketika aplikasi berada di background (minimal selama aplikasi aktif).
        * State timer dipertahankan ketika aplikasi kembali ke foreground.
    * **Validation Method:**
        * Uji dengan menempatkan aplikasi di background dan kemudian kembali ke foreground.
        * Verifikasi bahwa timer tetap berjalan dengan akurat.

3.  **Implementasi Penyimpanan Preferensi Timer (`PreferencesManager.kt`)**
    * **Deskripsi:** Menyimpan dan memulihkan preferensi timer (durasi sesi kerja, durasi istirahat) di `PreferencesManager`.
    * **Files to Create/Modify:** `app/src/main/java/com/neimasilk/purefocus/data/PreferencesManager.kt`
    * **Implementation Steps:**
        1.  Tambahkan properti untuk menyimpan durasi sesi kerja dan istirahat.
        2.  Implementasikan metode untuk mengakses dan memperbarui preferensi timer.
    * **Acceptance Criteria:**
        * Preferensi timer disimpan dan dipulihkan dengan benar.
        * Perubahan preferensi segera tercermin dalam timer.
    * **Validation Method:**
        * Ubah preferensi timer, tutup aplikasi, dan buka kembali.
        * Verifikasi bahwa preferensi dipertahankan.

4.  **Unit Testing untuk Timer (`PomodoroViewModelTests.kt`)**
    * **Deskripsi:** Membuat unit test untuk memverifikasi fungsionalitas timer.
    * **Files to Create/Modify:** `app/src/test/java/com/neimasilk/purefocus/ui/PomodoroViewModelTests.kt`
    * **Implementation Steps:**
        1.  Buat test untuk memverifikasi perhitungan waktu mundur.
        2.  Buat test untuk memverifikasi transisi antara sesi kerja dan istirahat.
        3.  Buat test untuk memverifikasi interaksi dengan `PreferencesManager`.
    * **Acceptance Criteria:**
        * Semua test berhasil (PASS).
        * Test mencakup semua fungsionalitas inti timer.
    * **Validation Method:**
        * Jalankan test dan verifikasi bahwa semua test berhasil.
        3.  Pulihkan posisi scroll saat `FocusWriteScreen` pertama kali ditampilkan atau saat teks dimuat.
    * **Acceptance Criteria:**
        * Posisi scroll dipulihkan saat kembali ke aplikasi setelah dijeda.
        * Posisi scroll dipulihkan saat aplikasi dibuka kembali (jika dipersistensi).
    * **Validation Method:**
        * Gulir ke posisi tertentu dalam teks panjang, tinggalkan aplikasi (misalnya, tekan tombol home), lalu kembali. Verifikasi posisi scroll.
        * Jika dipersistensi, tutup paksa aplikasi, buka kembali, dan verifikasi.

**Success Criteria untuk Baby-Step 1.2 secara keseluruhan:**
* Fungsionalitas salin teks bekerja dengan andal.
* Aplikasi menangani teks besar (misalnya, 10.000+ karakter) tanpa penurunan performa yang nyata atau penggunaan memori yang berlebihan.
* Seleksi teks dasar berfungsi dengan baik.
* (Jika diimplementasikan) Posisi scroll dapat dipulihkan.
* Semua operasi mempertahankan target performa aplikasi (<16ms per frame, penggunaan memori <50MB).
* Semua unit test yang ada tetap PASS, dan test baru (jika ada) juga PASS.

**Validation Methods untuk Baby-Step 1.2 secara keseluruhan:**
* Uji manual semua fungsionalitas baru pada emulator dan perangkat fisik (jika memungkinkan).
* Gunakan Android Studio Profiler secara ekstensif untuk memantau CPU, memori, dan rekomposisi Compose selama pengujian dengan teks berbagai ukuran.
* Verifikasi bahwa target performa inti tetap terpenuhi.