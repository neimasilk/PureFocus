# Status, TODO, and Suggestions

## Current Status (Juni 2025)

* **Milestone Tercapai:** Fondasi aplikasi PureFocus dengan fitur inti telah berhasil dibangun, stabil, dan telah melalui refactoring untuk meningkatkan kualitas kode dan pemeliharaan.
* **Refactoring & Peningkatan Kualitas Kode:** Sebagian besar konstanta telah dipindahkan ke file `Constants.kt` terpusat, meningkatkan keterbacaan dan konsistensi. Penggunaan `viewModelFactory` dan `initializer` untuk pembuatan ViewModel telah distandarisasi.
* **Fungsionalitas Utama:**
    * Timer Pomodoro (Fokus, Istirahat Pendek) berfungsi penuh. Logika untuk Istirahat Panjang (`LONG_BREAK`) ada namun belum sepenuhnya terintegrasi dalam siklus otomatis.
    * Manajemen sesi (siklus pomodoro, transisi otomatis antar sesi) berjalan baik untuk sesi Fokus dan Istirahat Pendek.
    * `PomodoroService` sebagai foreground service memastikan timer berjalan reliabel di background, meskipun logika timer utama saat ini masih berada di `PomodoroTimerViewModel` dan service hanya bertindak sebagai penanda foreground.
    * State timer (waktu tersisa, sesi saat ini, jumlah pomodoro selesai dalam siklus) dikelola oleh `PomodoroTimerViewModel` dan sebagian disimpan melalui `PreferencesManager` (khususnya durasi fokus). Pemulihan state saat aplikasi ditutup sepenuhnya dan dibuka kembali untuk timer yang sedang berjalan belum sepenuhnya diimplementasikan di service.
    * Pengaturan durasi timer (fokus) dapat dikonfigurasi pengguna dan disimpan melalui `PreferencesManager`. Durasi istirahat pendek dan panjang saat ini di-hardcode di `PomodoroTimerViewModel`.
    * Notifikasi untuk akhir setiap sesi (fokus, istirahat) berfungsi dengan baik.
    * Layar "Focus Write" minimalis telah terimplementasi sebagai area menulis bebas distraksi.
    * **[BARU]** Fungsionalitas "Clear All Text" dengan dialog konfirmasi telah diimplementasi di Focus Write Screen.
    * **[BARU]** Auto-save dan auto-load teks di Focus Write Screen menggunakan SharedPreferences dengan debouncing 1 detik.
    * **[BARU]** Persistent storage untuk teks Focus Write - teks tersimpan otomatis dan dipulihkan saat aplikasi dibuka kembali.
    * Word count dan character count real-time di Focus Write Screen - menampilkan jumlah kata dan karakter secara live saat mengetik (dikelola oleh `MainViewModel`).
    * Navigasi antar layar (Timer/Focus Write gabungan, Settings) menggunakan logika kondisional di `MainActivity`.
* **Kualitas & Teknis:**
    * Arsitektur MVVM. Dependency Injection menggunakan `viewModelFactory` dan `initializer` secara manual, bukan Hilt.
    * UI dibangun sepenuhnya menggunakan Jetpack Compose.
    * Penggunaan Coroutines dan Flow untuk operasi asynchronous dan state management.
    * Kode relatif bersih, terstruktur, dan mengikuti praktik modern Android.
* **Testing:**
    * **[BARU]** Unit tests telah diperbaiki dan berjalan dengan sukses (50 tests passing).
    * Semua failing tests di MainViewModelTests dan PomodoroTimerViewModelTests telah diperbaiki.
    * Tests telah disesuaikan dengan arsitektur baru dimana timer logic dipindahkan ke PomodoroService.
    * Belum ada informasi mengenai status UI test saat ini.
    * Cakupan tes sudah mencakup core functionality dari ViewModels.

## TODO - Pekerjaan di Masa Depan (Fitur & Peningkatan)

### Prioritas Utama Berikutnya (Refined):

1.  **Penyempurnaan `PomodoroService` & State Management Timer:**
    * **Pindahkan Logika Timer ke `PomodoroService`:** Saat ini, logika inti timer (countdown, transisi sesi) berada di `PomodoroTimerViewModel`. Pindahkan ini ke `PomodoroService` agar timer benar-benar independen dari UI lifecycle dan lebih robust.
    * **Sinkronisasi State antara Service dan ViewModel:** Gunakan `BroadcastReceiver` atau `Flow` yang di-expose dari Service untuk mengupdate UI di `PomodoroTimerViewModel` dan `MainActivity`.
    * **Persistensi State Timer yang Komprehensif:** Pastikan semua aspek `PomodoroState` (termasuk `timeLeftInMillis`, `currentSessionType`, `pomodorosCompletedInCycle`, `isTimerRunning`) disimpan dan dipulihkan dengan benar jika service dihentikan dan dimulai ulang (misalnya oleh sistem). Pertimbangkan DataStore untuk ini.
2.  **Implementasi Siklus Istirahat Panjang (Long Break):**
    * Aktifkan logika transisi ke `LONG_BREAK` setelah sejumlah `POMODOROS_PER_CYCLE` (misalnya 4) selesai.
    * Pastikan durasi `LONG_BREAK` dapat dikonfigurasi di Settings (mirip `focusDuration`).
3.  **Pengaturan Durasi Istirahat:**
    * Tambahkan opsi di `SettingsScreen` untuk mengatur durasi Istirahat Pendek dan Istirahat Panjang.
    * Simpan nilai ini di `PreferencesManager` dan gunakan di `PomodoroService` (setelah logika timer dipindahkan).
4.  **Focus Write Screen - Peningkatan UX:**
    * **[SEBELUMNYA]** Undo/Redo functionality.
    * **[SEBELUMNYA]** Text formatting options (bold, italic, bullet points).
    * **[SEBELUMNYA]** Search and replace dalam teks.
    * Mode full-screen opsional untuk pengalaman menulis yang lebih imersif.
    * Export/share text functionality (ke file, email, atau aplikasi lain).
5.  **Suara Notifikasi & Timer (Lanjutan dari TODO-RILIS-3):**
    * **[SEBELUMNYA]** Tambahkan opsi suara "tick-tock" halus selama sesi fokus (dapat diaktifkan/dinonaktifkan di settings).
    * **[SEBELUMNYA]** Suara notifikasi yang berbeda/khas untuk akhir sesi fokus dan akhir sesi istirahat.
    * **[SEBELUMNYA]** Opsi untuk memilih suara notifikasi dari sistem atau suara bawaan aplikasi.

### Prioritas Menengah (Refined):

6.  **Statistik & Riwayat Pomodoro:**
    * **[SEBELUMNYA]** Lacak jumlah sesi fokus yang diselesaikan per hari/minggu.
    * **[SEBELUMNYA]** Tampilkan statistik sederhana (misalnya, grafik batang atau ringkasan).
    * **[SEBELUMNYA]** Membutuhkan persistensi data yang lebih terstruktur (kemungkinan Room Database).
7.  **Peningkatan UI/UX:**
    * **[SEBELUMNYA]** Animasi transisi antar sesi yang lebih halus.
    * **[SEBELUMNYA]** Opsi tema tambahan (misalnya, tema gelap yang lebih customizable, tema warna berbeda).
    * **[SEBELUMNYA]** Review dan tingkatkan aspek aksesibilitas (uji dengan TalkBack, pastikan kontras warna cukup).
    * Gunakan Jetpack Compose Navigation untuk navigasi antar layar yang lebih standar dan manageable.
8.  **Pengaturan Lanjutan:**
    * **[SEBELUMNYA]** Opsi untuk menonaktifkan auto-start break/focus berikutnya.
    * **[SEBELUMNYA]** Opsi untuk custom suara notifikasi per tipe sesi.
    * **[SEBELUMNYA]** "Daily Goal" untuk jumlah sesi Pomodoro.

### Prioritas Rendah / Ide Jangka Panjang (Sama seperti sebelumnya):

7.  **Integrasi Kalender (Opsional):**
    * Sinkronisasi sesi fokus dengan kalender pengguna.
8.  **Mode "Strict" Pomodoro:**
    * Mencegah pengguna keluar dari aplikasi atau membuka aplikasi lain selama sesi fokus (membutuhkan izin khusus dan mungkin tidak sepenuhnya bisa di semua device).
9.  **Backup & Restore Pengaturan/Statistik:**
    * Menggunakan layanan backup Android atau ekspor/impor manual.

## Saran "Baby-Step Todolist" (Untuk Segera Dilakukan - Refined):

1.  **Pindahkan Logika Timer ke `PomodoroService` (Langkah Awal):**
    * **Identifikasi Logika Inti:** Di `PomodoroTimerViewModel`, identifikasi bagian kode yang bertanggung jawab atas countdown (`timerJob`, loop `while`, `delay`), pembaruan `timeLeftInMillis`, dan pemanggilan `handleSessionFinish()`.
    * **Pindahkan ke Service:** Pindahkan logika ini ke dalam `PomodoroService`. Service akan membutuhkan `MutableStateFlow` sendiri untuk `PomodoroState` internal.
    * **Kontrol dari ViewModel:** `PomodoroTimerViewModel` akan mengirim command (Start, Pause, Reset, Skip) ke `PomodoroService` melalui `Intent` (seperti yang sudah ada untuk Start/Stop, namun perlu diperluas).
    * **Service Mengupdate Dirinya Sendiri:** Service akan mengelola `PomodoroState` internalnya sendiri berdasarkan command dan logika timer.
2.  **Dasar Komunikasi Service ke UI (ViewModel/Activity):**
    * **Gunakan `LocalBroadcastManager` atau `Flow`:** `PomodoroService` perlu mengirim update `PomodoroState` (atau setidaknya `timeLeftInMillis` dan `currentSessionType`) kembali ke UI.
        * **Pilihan 1 (Broadcast):** `PomodoroService` mengirim broadcast setiap detik dengan state terbaru. `MainActivity` atau `PomodoroTimerViewModel` mendaftarkan `BroadcastReceiver` untuk menerima update ini.
        * **Pilihan 2 (Flow - Lebih modern):** Jika memungkinkan, expose `StateFlow<PomodoroState>` dari `PomodoroService` yang dapat di-collect oleh `PomodoroTimerViewModel`. Ini mungkin memerlukan binding ke service atau cara lain untuk mendapatkan instance service.
    * **Update UI:** `PomodoroTimerViewModel` mengupdate `_uiState` berdasarkan data yang diterima dari service.
3.  **Implementasi Pengaturan Durasi Istirahat Pendek di Settings:**
    * **Update `PreferencesManager`:** Tambahkan key dan fungsi untuk menyimpan/mengambil durasi istirahat pendek.
    * **Update `SettingsViewModel`:** Tambahkan `StateFlow` untuk durasi istirahat pendek dan fungsi untuk mengupdatenya.
    * **Update `SettingsScreen`:** Tambahkan `OutlinedTextField` dan tombol `Save` untuk durasi istirahat pendek, mirip dengan yang sudah ada untuk durasi fokus.
    * **Gunakan di `PomodoroService`:** `PomodoroService` (setelah logika timer dipindahkan) harus membaca durasi ini dari `PreferencesManager` saat memulai sesi istirahat pendek.
4.  **Unit Test untuk Logika Timer di `PomodoroService`:** Setelah logika timer dipindahkan, tulis unit test untuk memastikan countdown, transisi sesi (awal), dan penanganan command di service berfungsi dengan benar.
5.  **Review dan Selesaikan TODOs dari `status-todo-suggestions.md` bagian "Langkah Minimum Kritis Sebelum Rilis Publik":**
    * **[TODO-RILIS-1.1] Amankan State Teks `FocusWriteScreen`:** Ganti `remember` dengan `rememberSaveable` untuk `textFieldValueState` di `FocusWriteScreenImpl` (atau `textState` jika merujuk ke versi lama file). *Catatan: Kode saat ini sudah menggunakan `textFieldValueState` yang di-pass dari `MainActivity` via `pomodoroViewModel.focusWriteText`, yang mana `MainViewModel` sudah menangani penyimpanan teks. Jadi, ini mungkin sudah teratasi atau perlu re-evaluasi.* Periksa `FocusWriteScreenImpl` untuk state lokal yang mungkin perlu `rememberSaveable`.
    * **[SELESAI] ✅ Amankan State Input Field di `SettingsScreen`:** `durationInput` telah diubah menggunakan `rememberSaveable` untuk mencegah kehilangan input saat rotasi layar.
    * **[SELESAI] ✅ Implementasi Opsi Dasar Kontrol Suara Notifikasi:** Fitur kontrol suara notifikasi telah diimplementasikan lengkap:
        * Preferensi `enableSoundNotifications` ditambahkan ke `PreferencesManager`
        * UI Switch ditambahkan di `SettingsScreen` dengan integrasi `SettingsViewModel`
        * `NotificationHelper` dimodifikasi untuk kondisional pemutaran suara
        * `MainActivity` diupdate untuk menggunakan preferensi suara saat memanggil notifikasi





## Baby-Step Todolist Berikutnya (Juni 2025)

Setelah menyelesaikan pemindahan logika timer ke service dan implementasi dasar kontrol suara notifikasi:

### Prioritas Tinggi (Lanjutan):
1.  **Implementasi Siklus Istirahat Panjang (Long Break) Sepenuhnya:**
    * Tambahkan logika di `PomodoroService` untuk melacak `pomodorosCompletedInCycle`.
    * Setelah `POMODOROS_PER_CYCLE` tercapai, transisikan ke `LONG_BREAK`.
    * Reset `pomodorosCompletedInCycle` setelah `LONG_BREAK`.
    * Tambahkan pengaturan durasi Istirahat Panjang di `SettingsScreen` dan `PreferencesManager`.
2.  **[SELESAI] ✅ Implementasi Kontrol Suara Notifikasi:** Telah selesai diimplementasikan dengan lengkap.
3.  **[SELESAI] ✅ Amankan State Input di `SettingsScreen`:** Telah selesai diimplementasikan menggunakan `rememberSaveable`.

### Prioritas Menengah (Lanjutan):
4.  **Peningkatan `FocusWriteScreen`:**
    * **Mode Full-Screen:** Tambahkan toggle untuk menyembunyikan UI sistem.
    * **Export/Share Text:** Implementasi fungsionalitas dasar untuk berbagi teks.
5.  **Refine Navigasi:** Pertimbangkan untuk mengadopsi Jetpack Compose Navigation untuk struktur navigasi yang lebih baik antara `FocusWriteScreen`/`PomodoroControlsView` dan `SettingsScreen`.

Dengan langkah-langkah ini, PureFocus akan menjadi lebih robust, fungsional, dan siap untuk pengguna yang lebih luas.

Selamat melanjutkan pengembangan PureFocus! Proyek ini menunjukkan kemajuan yang baik.

---
---
# File: status-todo-suggestions.md (Tambahan/Update)
---

## Langkah Minimum Kritis Sebelum Rilis Publik (Pasca-Penyelesaian Dokumentasi)

Bagian ini merinci tugas-tugas paling penting yang disarankan untuk diselesaikan setelah dokumentasi rampung, guna meningkatkan kelayakan aplikasi untuk rilis publik (v1.0). Fokusnya adalah pada stabilitas pengalaman pengguna inti dan kejelasan fungsionalitas.

### 1. Stabilisasi Perilaku Teks pada "Focus Write Mode"

**Masalah:** Teks yang ditulis di `FocusWriteScreen` saat ini dapat hilang saat perubahan konfigurasi (misalnya rotasi layar) atau jika pengguna tidak sengaja keluar dari mode tersebut sebelum sesi selesai. Meskipun desainnya mungkin "teks menghilang di akhir sesi", kehilangan teks *selama* sesi aktif karena rotasi adalah masalah UX.

**Tugas Minimum yang Harus Dilakukan:**

* **[TODO-RILIS-1.1] Amankan State Teks `FocusWriteScreen` dari Perubahan Konfigurasi:**
    * **File:** `app/src/main/java/com/neimasilk/purefocus/ui/screens/FocusWriteScreen.kt`
    * **Perubahan:** Modifikasi state `textState` agar menggunakan `rememberSaveable` bukan hanya `remember`.
        * Ganti `var textState by remember { mutableStateOf("") }`
        * Menjadi `var textState by rememberSaveable { mutableStateOf("") }`
    * **Tujuan:** Memastikan teks yang sedang ditulis tidak hilang saat pengguna memutar layar atau saat sistem Android menghentikan sementara dan membuat ulang Activity.
* **[TODO-RILIS-1.2] (Opsional, tapi Sangat Direkomendasikan) Klarifikasi UI tentang Perilaku Teks:**
    * **File:** `app/src/main/java/com/neimasilk/purefocus/ui/screens/FocusWriteScreen.kt`
    * **Perubahan:** Tambahkan teks non-intrusif (misalnya `Text` Composable dengan `fontSize` kecil di bawah area input) yang menjelaskan bahwa teks akan di-reset untuk sesi fokus baru. Contoh: *"Teks akan dikosongkan saat sesi fokus baru dimulai."*
    * **Tujuan:** Mengelola ekspektasi pengguna mengenai sifat "ephemeral" dari teks, jika diputuskan tidak ada opsi simpan permanen untuk MVP.

### 2. Amankan State Input pada Layar Pengaturan

**Masalah:** Mirip dengan Focus Write, input pengguna pada layar Pengaturan (misalnya saat sedang mengubah durasi) bisa hilang jika terjadi perubahan konfigurasi sebelum disimpan.

**Tugas Minimum yang Harus Dilakukan:**

* **[TODO-RILIS-2.1] Amankan State Input Field di `SettingsScreen` dari Perubahan Konfigurasi:**
    * **File:** `app/src/main/java/com/neimasilk/purefocus/ui/screens/SettingsScreen.kt`
    * **Perubahan:** Untuk setiap `TextField` yang state-nya dipegang oleh `remember { mutableStateOf(...) }`, ganti menjadi `rememberSaveable { mutableStateOf(...) }`. Ini berlaku untuk `focusDurationInput`, `shortBreakDurationInput`, `longBreakDurationInput`, dan `longBreakIntervalInput`.
        * Contoh: Ganti `var focusDurationInput by remember { mutableStateOf(settings.focusDurationMinutes.toString()) }`
        * Menjadi `var focusDurationInput by rememberSaveable { mutableStateOf(settings.focusDurationMinutes.toString()) }` (lakukan untuk semua input serupa).
    * **Tujuan:** Memastikan pengguna tidak kehilangan input yang sedang diketik di form pengaturan jika terjadi rotasi layar.

### 3. Implementasi Opsi Dasar Kontrol Suara Notifikasi

**Masalah:** Aplikasi saat ini selalu memainkan suara notifikasi. Pengguna tidak memiliki kontrol untuk menonaktifkannya, yang bisa menjadi sumber gangguan.

**Tugas Minimum yang Harus Dilakukan:**

* **[TODO-RILIS-3.1] Tambahkan Preferensi Pengaturan Suara:**
    * **File:** `app/src/main/proto/user_settings.proto` (atau file proto Anda jika namanya berbeda, dan `PreferencesManager.kt`)
    * **Perubahan:**
        * Tambahkan field baru `bool enable_sound_notifications = 5;` (atau nomor berikutnya yang tersedia) ke message `UserSettings`.
        * Update `PreferencesManager.kt` untuk mengenali dan mengelola preferensi baru ini (termasuk nilai default, misalnya `true`).
* **[TODO-RILIS-3.2] Tambahkan UI Switch di `SettingsScreen`:**
    * **File:** `app/src/main/java/com/neimasilk/purefocus/ui/SettingsViewModel.kt`
    * **File:** `app/src/main/java/com/neimasilk/purefocus/ui/screens/SettingsScreen.kt`
    * **Perubahan:**
        * Di `SettingsViewModel`, tambahkan fungsi untuk mengupdate `enableSoundNotifications`.
        * Di `SettingsScreen`, tambahkan `Switch` Composable yang terikat dengan state `enableSoundNotifications` dari `SettingsViewModel`. Beri label yang jelas seperti "Aktifkan Suara Notifikasi".
* **[TODO-RILIS-3.3] Kondisikan Pemutaran Suara Notifikasi:**
    * **File:** `app/src/main/java/com/neimasilk/purefocus/util/NotificationHelper.kt` (atau di `PomodoroService.kt` jika logika suara ada di sana).
    * **Perubahan:** Sebelum memanggil `setSound()` pada `NotificationCompat.Builder` atau sebelum memainkan suara notifikasi, baca nilai preferensi `enableSoundNotifications` (melalui `PreferencesManager` yang di-inject). Hanya mainkan suara jika preferensi tersebut `true`.
        * Contoh di `NotificationHelper` dalam `showTimerNotification` atau `showSessionFinishedNotification`:
            ```kotlin
            // ... builder setup ...
            // val userSettings = preferencesManager.userSettingsFlow.first() // Perlu cara untuk akses preferencesManager
            // if (userSettings.enableSoundNotifications) {
            // builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            // } else {
            // builder.setSound(null)
            // }
            // ...
            ```
            *Catatan: Akses `PreferencesManager` di `NotificationHelper` mungkin memerlukan sedikit refactoring atau penyediaan dependensi yang sesuai jika belum ada.*
---
**Catatan Penting:**
* Daftar di atas adalah **minimum absolut** untuk meningkatkan stabilitas dasar dan kenyamanan pengguna sebelum rilis.