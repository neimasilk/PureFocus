# Status & To-Do List Suggestions

**Project:** PureFocus - Aplikasi Menulis Minimalis dengan Timer Pomodoro
**Last Updated:** 5 Juni 2025 
**Author:** Neima Silk
**Status:** Phase 1 Completed (50%), Phase 2 In Progress (Baby-Step 2.1 Selesai, Baby-Step 2.2 Dimulai)

## Current Progress

**Current Task:** Baby-Step 2.2: "Timer UI Integration with FocusWriteScreen"

**Phase 1 (Core Text Editor) Progress:** 50% (Baby-Step 1.1 dan 1.2 selesai)
**Phase 2 (Pomodoro Timer) Progress:** Baby-Step 2.1 Selesai.

**Recently Completed:**
* **Baby-Step 2.1: Basic Timer Implementation**
    * Implementasi `PomodoroTimerViewModel` dengan logika inti timer (start, pause, reset, skip).
    * Definisi `PomodoroState` dan `SessionType`.
    * Inisialisasi ViewModel di `MainActivity`.
    * Penulisan unit test untuk `PomodoroTimerViewModel` (eksekusi terkendala masalah SDK).
* Implementasi fungsionalitas "Salin Teks" dengan menu konteks dan Toast feedback.
* Migrasi dari String ke TextFieldValue untuk manajemen posisi kursor.
* Verifikasi seleksi teks dasar berfungsi dengan baik.
* Optimasi penanganan teks untuk dokumen besar dengan profiling dan pengujian performa.

**Issues Resolved:**
* Fixed MainViewModelTests dengan mengupdate mockito-kotlin ke versi yang kompatibel dengan Kotlin 1.8.10.
* Resolved issue dengan posisi kursor yang hilang saat rotasi layar atau navigasi dengan mengimplementasikan TextFieldValue di MainViewModel dan FocusWriteScreen.
* Ditambahkan unit test untuk memverifikasi fungsionalitas TextFieldValue dan manajemen posisi kursor.
* (Catatan Tambahan) Eksekusi test untuk Baby-Step 2.1 terkendala masalah environment SDK worker.

**Next Task (Baby-Step 2.2):** "Timer UI Integration with FocusWriteScreen" 
* Desain dan implementasikan UI timer yang minimalis di FocusWriteScreen.
* Tambahkan kontrol dasar (mulai, jeda, reset) untuk timer.
* Implementasikan notifikasi sederhana untuk transisi antar sesi.

## High-Priority To-Do List (Near Future)

1.  **Baby-Step 2.2: Integrasi UI Timer dengan FocusWriteScreen (Current)**
    * Tambahkan UI minimal untuk timer di `FocusWriteScreen`.
    * Implementasikan notifikasi sederhana untuk transisi sesi.

2.  **Baby-Step 2.3: Manajemen Sesi**
    * Implementasikan pelacakan sesi yang telah diselesaikan.
    * Tambahkan statistik dasar (jumlah sesi, total waktu fokus).

3.  **Verifikasi bahwa target performa inti tetap terpenuhi.** (Setelah integrasi UI Timer)

4.  **(SELESAI) Baby-Step 2.1: Implementasi Timer Dasar**
    * Buat `PomodoroViewModel` untuk mengelola state dan logika timer.
    * Implementasikan timer dasar dengan sesi kerja 25 menit dan istirahat 5 menit.
    * Pastikan timer dapat berjalan di background (minimal selama aplikasi aktif).

## Baby-Step To-Do List Suggestion (Untuk Baby-Step 2.2: "Timer UI Integration with FocusWriteScreen")

**Objective:** Mengintegrasikan UI Timer yang minimalis ke dalam `FocusWriteScreen` dan menyediakan kontrol dasar serta notifikasi transisi sesi.

**Estimated Duration:** (Akan ditentukan)

**Detailed Tasks:** (Akan dirinci dalam baby-step.md berikutnya)

1.  **Desain UI Timer Minimalis di `FocusWriteScreen`**
    * **Deskripsi:** Merancang dan mengimplementasikan tampilan visual untuk timer (misalnya, teks waktu tersisa, indikator sesi).
    * **Files to Create/Modify:** `app/src/main/java/com/neimasilk/purefocus/ui/FocusWriteScreen.kt`
    * **Acceptance Criteria:** UI timer terlihat jelas namun tidak mengganggu area tulis.

2.  **Implementasi Kontrol Timer (Start, Pause, Reset)**
    * **Deskripsi:** Menambahkan tombol atau gestur untuk mengontrol timer dari `FocusWriteScreen`.
    * **Files to Create/Modify:** `app/src/main/java/com/neimasilk/purefocus/ui/FocusWriteScreen.kt`, `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModel.kt` (jika perlu interaksi tambahan).
    * **Acceptance Criteria:** Kontrol timer responsif dan berfungsi sesuai harapan.

3.  **Implementasi Notifikasi Transisi Sesi**
    * **Deskripsi:** Memberikan feedback (misalnya, suara lembut atau getaran singkat) saat sesi kerja berakhir dan sesi istirahat dimulai, atau sebaliknya.
    * **Files to Create/Modify:** `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModel.kt` (untuk memicu notifikasi), `MainActivity.kt` (potensial untuk setup channel notifikasi).
    * **Acceptance Criteria:** Notifikasi disampaikan dengan tepat waktu dan tidak intrusif.

**(Bagian detail untuk Baby-Step 2.1 yang lama telah dihapus karena sudah selesai.)**

---
_Dokumen ini akan diupdate seiring progres proyek._
_Pastikan untuk merujuk pada `baby-step.md` yang relevan untuk detail implementasi tugas saat ini._
