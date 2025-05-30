# Status & To-Do List Suggestions

**Project:** PureFocus - Aplikasi Menulis Minimalis dengan Timer Pomodoro
**Last Updated:** 30 Mei 2025
**Author:** Gemini (berdasarkan input Neima Silk)
**Status:** Fase 1 (Editor Teks Inti) 50% Selesai. Semua tes unit lolos. Siap memulai Fase 2.

## Current Progress

**Current Task:** Mempersiapkan Baby-Step 2.1: "Implementasi Timer Dasar"
**Active Phase:** Fase 1 - Core Text Editor (50% Selesai, siap transisi ke Fase 2)

**Recently Completed (Recap dari Baby-Step 1.1 & 1.2):**
* Implementasi UI editor teks layar penuh (`FocusWriteScreen.kt`) menggunakan `BasicTextField`.
* Integrasi `MainViewModel` untuk mengelola state teks (`textFieldValue`).
* Fungsionalitas penyimpanan otomatis teks ke `PreferencesManager` dengan *debouncing* (500ms).
* Implementasi fitur "Salin Semua Teks" melalui menu konteks (tekan lama) dengan umpan balik Toast.
* Migrasi dari `String` ke `TextFieldValue` untuk mendukung pemulihan posisi kursor dan seleksi teks.
* Semua unit tes untuk `PreferencesManagerTests` dan `MainViewModelTests` berhasil dijalankan (PASS).

**Issues Resolved (Recap):**
* Masalah kegagalan unit tes di `MainViewModelTests.kt` terkait Mockito dan Coroutines Test telah diatasi.

## High-Priority To-Do List (Near Future)

Sesuai dengan `implementation-plan.md`:

1.  **Baby-Step 2.1: Implementasi Timer Dasar (Next)**
    * Buat `PomodoroTimerViewModel` untuk mengelola state dan logika timer.
    * Implementasikan logika inti untuk sesi kerja 25 menit dan istirahat pendek 5 menit (dapat dikonfigurasi nantinya).
    * Pastikan timer dapat berjalan secara akurat dan state-nya dapat dikelola (mulai, jeda, reset).
    * Pastikan timer dapat bertahan dari perubahan siklus hidup aplikasi (minimal selama aplikasi aktif).
2.  **Baby-Step 2.2: Integrasi UI Timer dan Notifikasi**
    * Desain dan implementasikan UI timer yang sangat minimalis sebagai *overlay* atau bagian dari `FocusWriteScreen` tanpa mengganggu area tulis.
    * Tambahkan kontrol dasar (mulai, jeda, reset) untuk timer yang mudah diakses.
    * Implementasikan notifikasi yang lembut dan tidak mengganggu untuk transisi antar sesi (misalnya, akhir sesi kerja, akhir sesi istirahat).
3.  **Verifikasi bahwa target performa inti tetap terpenuhi** setelah penambahan fitur timer.
4.  **Baby-Step 2.3 (dari implementation-plan.md adalah "Timer UI and Notifications", namun bisa dipecah lagi): Manajemen Sesi Lanjutan & Istirahat Panjang**
    * Implementasikan logika untuk istirahat panjang (misalnya, 15-30 menit) setelah beberapa sesi kerja (misalnya, 4 sesi).
    * Pastikan transisi antar semua jenis sesi (kerja, istirahat pendek, istirahat panjang) berjalan mulus.

## Baby-Step To-Do List Suggestion (Untuk Baby-Step 2.1: "Implementasi Timer Dasar")

**Objective:** Mengimplementasikan fungsionalitas inti dari timer Pomodoro, termasuk logika state, manajemen waktu, dan persistensi dasar state timer jika diperlukan, tanpa UI yang kompleks pada tahap ini. Fokus pada logika di ViewModel.

**Estimated Duration:** 2-3 hari

**Detailed Tasks:**

1.  **Buat `PomodoroTimerViewModel.kt` dan `PomodoroState.kt`**
    * **Deskripsi:** Membuat ViewModel baru untuk mengelola semua logika terkait timer Pomodoro dan data class untuk merepresentasikan state timer.
    * **Files to Create/Modify:**
        * `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModel.kt` (Baru)
        * `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroState.kt` (Baru)
    * **Implementation Steps:**
        1.  Definisikan `PomodoroState`:
            * `timeLeftInMillis: Long`
            * `currentSessionType: SessionType` (Enum: `WORK`, `SHORT_BREAK`, `LONG_BREAK`)
            * `timerRunning: Boolean`
            * `pomodorosCompletedInCycle: Int`
        2.  Buat `PomodoroTimerViewModel`:
            * Gunakan `MutableStateFlow<PomodoroState>` untuk mengelola state.
            * Sediakan fungsi `startTimer()`, `pauseTimer()`, `resetTimer()`, `skipSession()`.
            * Implementasikan logika countdown menggunakan `kotlinx.coroutines.flow.timer` atau `CountDownTimer` dalam `viewModelScope`.
            * Logika awal untuk transisi otomatis dari `WORK` ke `SHORT_BREAK`.
    * **Acceptance Criteria:**
        * `PomodoroState` dapat merepresentasikan semua kondisi dasar timer.
        * Fungsi dasar ViewModel (start, pause, reset) dapat memanipulasi `PomodoroState` dengan benar.
        * Countdown timer berjalan akurat dalam `viewModelScope`.
    * **Validation Method:**
        * Unit test untuk `PomodoroTimerViewModel` (lihat Task 4).
        * Logging manual dari state perubahan untuk verifikasi awal.

2.  **Integrasikan Durasi Default Timer dari `PreferencesManager`**
    * **Deskripsi:** Memungkinkan `PomodoroTimerViewModel` untuk mengambil durasi sesi default (misalnya, kerja, istirahat pendek) dari `PreferencesManager`. Untuk saat ini, bisa hardcode jika `PreferencesManager` belum mendukungnya, dengan TODO untuk mengambil dari preferensi nanti.
    * **Files to Create/Modify:**
        * `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModel.kt`
        * (Opsional, jika menambah sekarang) `app/src/main/java/com/neimasilk/purefocus/data/PreferencesManager.kt`
    * **Implementation Steps:**
        1.  Tambahkan konstanta untuk durasi default (misal, 25 menit kerja, 5 menit istirahat pendek).
        2.  Gunakan durasi ini saat memulai atau mereset sesi.
        3.  (Jika menambah ke `PreferencesManager`): Tambahkan key dan getter/setter untuk `workSessionDuration`, `shortBreakDuration`, `longBreakDuration`.
    * **Acceptance Criteria:**
        * Timer menggunakan durasi yang telah ditentukan untuk setiap jenis sesi.
    * **Validation Method:**
        * Verifikasi melalui logging atau unit test bahwa durasi sesi yang benar digunakan.

3.  **Sediakan `PomodoroTimerViewModel` di `MainActivity` (atau tingkat yang sesuai)**
    * **Deskripsi:** Menginisialisasi `PomodoroTimerViewModel` sehingga dapat diakses dan state-nya dapat diobservasi nantinya oleh UI.
    * **Files to Create/Modify:**
        * `app/src/main/java/com/neimasilk/purefocus/MainActivity.kt`
    * **Implementation Steps:**
        1.  Inisialisasi `PomodoroTimerViewModel` di `MainActivity` menggunakan `ViewModelProvider`.
        2.  Untuk saat ini, cukup inisialisasi. Pengg# Status & To-Do List Suggestions

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
_Pastikan untuk merujuk pada `baby-step.md` yang relevan untuk detail implementasi tugas saat ini._unaan state akan dilakukan di Baby-Step berikutnya saat membuat UI.
    * **Acceptance Criteria:**
        * ViewModel berhasil diinisialisasi tanpa *crash*.
    * **Validation Method:**
        * Jalankan aplikasi, pastikan tidak ada *crash* terkait inisialisasi ViewModel.

4.  **Unit Testing Awal untuk `PomodoroTimerViewModel`**
    * **Deskripsi:** Membuat unit test dasar untuk memverifikasi logika inti dari `PomodoroTimerViewModel`.
    * **Files to Create/Modify:**
        * `app/src/test/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModelTests.kt` (Baru)
    * **Implementation Steps:**
        1.  Gunakan `StandardTestDispatcher` seperti di `MainViewModelTests`.
        2.  Tes fungsi `startTimer()`: verifikasi `timerRunning` menjadi true dan `timeLeftInMillis` mulai berkurang.
        3.  Tes fungsi `pauseTimer()`: verifikasi `timerRunning` menjadi false dan `timeLeftInMillis` berhenti berkurang.
        4.  Tes fungsi `resetTimer()`: verifikasi state kembali ke kondisi awal sesi kerja.
        5.  Tes transisi dari sesi kerja ke istirahat pendek setelah waktu habis.
    * **Acceptance Criteria:**
        * Semua unit tes berhasil (PASS).
        * Cakupan tes mencakup skenario dasar start, pause, reset, dan transisi sesi.
    * **Validation Method:**
        * Jalankan `./gradlew testDebugUnitTest` dan pastikan semua tes baru lolos.

**Success Criteria untuk Baby-Step 2.1 secara keseluruhan:**
* Logika inti timer Pomodoro (start, pause, reset, countdown, transisi sesi dasar) berfungsi di dalam `PomodoroTimerViewModel`.
* State timer dapat dikelola dan diobservasi (meskipun belum ditampilkan di UI).
* Unit test memvalidasi fungsionalitas inti ViewModel.
* Tidak ada dampak negatif pada fungsionalitas editor teks yang sudah ada.
* Target performa aplikasi tetap terjaga.

**Validation Methods untuk Baby-Step 2.1 secara keseluruhan:**
* Jalankan semua unit test, pastikan PASS.
* Lakukan pengujian manual dengan logging untuk memverifikasi alur state timer.
* Pastikan aplikasi tetap berjalan lancar tanpa *crash* atau penurunan performa yang nyata.