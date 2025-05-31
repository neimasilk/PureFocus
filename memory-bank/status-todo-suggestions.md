# Status, TODO, dan Saran (Pasca-MVP)

## Status Saat Ini (Berdasarkan progress.md - Januari 2025)

*   **STATUS PROYEK: MVP SELESAI ✅**
*   **Milestone Utama Tercapai:** Aplikasi PureFocus dengan semua fitur inti MVP telah berhasil dibangun, diuji secara komprehensif, dan memenuhi target kinerja. Siap untuk deployment produksi.
*   **Fungsionalitas Inti MVP (Semua Terimplementasi & Teruji):**
    *   Mode "Focus Write" minimalis dengan editor teks layar penuh.
    *   Auto-save dan auto-load teks di Focus Write Screen menggunakan SharedPreferences dengan debouncing.
    *   Fungsionalitas "Clear All Text" dengan dialog konfirmasi.
    *   Word count dan character count real-time.
    *   Timer Pomodoro terintegrasi (Sesi Fokus, Istirahat Pendek). Logika dasar untuk Istirahat Panjang ada.
    *   `PomodoroService` sebagai foreground service memastikan timer berjalan di background.
    *   Pengaturan durasi timer (fokus) yang dapat dikonfigurasi pengguna.
    *   Notifikasi untuk akhir setiap sesi.
    *   Navigasi antar layar (Timer/Focus Write, Settings).
    *   Tema Terang/Gelap.
*   **Kualitas & Teknis:**
    *   Arsitektur MVVM dengan Jetpack Compose.
    *   Penggunaan Coroutines dan Flow untuk operasi asynchronous dan state management.
    *   Kode bersih, terstruktur, dan mengikuti praktik modern Android.
*   **Testing:**
    *   Cakupan tes komprehensif: 21 unit test dan 5 instrumented test, semuanya berhasil lolos.
    *   Semua isu terkait unit test dan instrumented test yang tercatat sebelumnya telah diselesaikan.

## TODO - Pekerjaan di Masa Depan (Fitur & Peningkatan Pasca-MVP)

Daftar ini diambil dari rencana sebelumnya dan disesuaikan untuk konteks pasca-MVP.

### Prioritas Utama Berikutnya:

1.  **✅ SELESAI - Penyempurnaan `PomodoroService` & State Management Timer:**
    *   **✅ Pindahkan Logika Timer ke `PomodoroService`:** Logika inti timer (countdown, transisi sesi) telah berhasil dipindahkan sepenuhnya ke `PomodoroService`. Timer sekarang independen dari UI lifecycle dan lebih robust.
    *   **✅ Sinkronisasi State antara Service dan ViewModel:** Implementasi `StateFlow` yang di-expose dari Service untuk mengupdate UI di `PomodoroTimerViewModel` telah berhasil.
    *   **Persistensi State Timer yang Komprehensif:** Pastikan semua aspek `PomodoroState` (termasuk `timeLeftInMillis`, `currentSessionType`, `pomodorosCompletedInCycle`, `isTimerRunning`) disimpan dan dipulihkan dengan benar jika service dihentikan dan dimulai ulang (misalnya oleh sistem). Pertimbangkan DataStore untuk ini.
2.  **✅ SELESAI - Implementasi Siklus Istirahat Panjang (Long Break) Sepenuhnya:**
    *   **✅ Aktifkan dan integrasikan sepenuhnya logika transisi ke `LONG_BREAK`:** Logika transisi ke `LONG_BREAK` setelah `POMODOROS_PER_CYCLE` (4) selesai telah diimplementasikan.
    *   **Durasi `LONG_BREAK` dapat dikonfigurasi di Settings:** Masih menggunakan konstanta default, perlu ditambahkan pengaturan di UI Settings.
3.  **Pengaturan Durasi Istirahat (Pendek & Panjang):**
    *   Tambahkan opsi di `SettingsScreen` untuk mengatur durasi Istirahat Pendek dan Istirahat Panjang.
    *   Simpan nilai ini di `PreferencesManager` dan gunakan di `PomodoroService`.
4.  **Peningkatan Suara Notifikasi & Timer:**
    *   Tambahkan opsi suara "tick-tock" halus selama sesi fokus (dapat diaktifkan/dinonaktifkan di settings).
    *   Suara notifikasi yang berbeda/khas untuk akhir sesi fokus dan akhir sesi istirahat.
    *   Opsi untuk memilih suara notifikasi dari sistem atau suara bawaan aplikasi.

### Prioritas Menengah:

5.  **Focus Write Screen - Peningkatan UX:**
    *   Undo/Redo functionality.
    *   Opsi format teks dasar (bold, italic, bullet points).
    *   Fungsi pencarian dan penggantian dalam teks.
    *   Mode full-screen opsional untuk pengalaman menulis yang lebih imersif.
    *   Fungsionalitas ekspor/berbagi teks (ke file, email, atau aplikasi lain).
6.  **Statistik & Riwayat Pomodoro:**
    *   Lacak jumlah sesi fokus yang diselesaikan per hari/minggu.
    *   Tampilkan statistik sederhana (misalnya, grafik batang atau ringkasan).
    *   Membutuhkan persistensi data yang lebih terstruktur (kemungkinan menggunakan Room Database).
7.  **Peningkatan UI/UX Lanjutan:**
    *   Animasi transisi antar sesi yang lebih halus.
    *   Opsi tema tambahan (misalnya, tema gelap yang lebih dapat disesuaikan, tema warna berbeda).
    *   Review dan tingkatkan aspek aksesibilitas (uji dengan TalkBack, pastikan kontras warna cukup).
    *   Gunakan Jetpack Compose Navigation untuk navigasi antar layar yang lebih standar dan manageable.
8.  **Pengaturan Lanjutan:**
    *   Opsi untuk menonaktifkan auto-start istirahat/fokus berikutnya.
    *   Opsi untuk suara notifikasi kustom per tipe sesi.
    *   "Daily Goal" untuk jumlah sesi Pomodoro.

### Prioritas Rendah / Ide Jangka Panjang:

9.  **Integrasi Kalender (Opsional):**
    *   Sinkronisasi sesi fokus dengan kalender pengguna.
10. **Mode "Strict" Pomodoro:**
    *   Mencegah pengguna keluar dari aplikasi atau membuka aplikasi lain selama sesi fokus.
11. **Backup & Restore Pengaturan/Statistik:**
    *   Menggunakan layanan backup Android atau ekspor/impor manual.

## Saran "Baby-Step Todolist" (Untuk Segera Dilakukan Pasca-MVP):

Berikut adalah saran langkah-langkah kecil dan konkret berdasarkan prioritas utama di atas:

1.  **Pindahkan Logika Inti Timer ke `PomodoroService`:**
    *   **Identifikasi & Pindahkan:** Di `PomodoroTimerViewModel`, identifikasi logika countdown, pembaruan `timeLeftInMillis`, dan penanganan akhir sesi. Pindahkan ke `PomodoroService`.
    *   **State Internal Service:** `PomodoroService` akan mengelola `PomodoroState` internalnya sendiri (misalnya, menggunakan `MutableStateFlow`).
    *   **Kontrol dari ViewModel:** `PomodoroTimerViewModel` akan mengirim command (Start, Pause, Reset, Skip, dll.) ke `PomodoroService` melalui `Intent`.
2.  **Implementasi Komunikasi Dua Arah Service ke UI (ViewModel/Activity):**
    *   **Expose State dari Service:** `PomodoroService` perlu mengirim update `PomodoroState` (atau setidaknya `timeLeftInMillis` dan `currentSessionType`) kembali ke UI. Gunakan `Flow` yang di-expose dari Service yang dapat di-collect oleh `PomodoroTimerViewModel`. Ini mungkin memerlukan binding ke service.
    *   **Update UI:** `PomodoroTimerViewModel` mengupdate `_uiState` berdasarkan data yang diterima dari service.
3.  **Implementasi Pengaturan Durasi Istirahat Pendek & Panjang di Settings:**
    *   **Update `PreferencesManager`:** Tambahkan key dan fungsi untuk menyimpan/mengambil durasi istirahat pendek dan panjang.
    *   **Update `SettingsViewModel`:** Tambahkan `StateFlow` untuk durasi tersebut dan fungsi untuk mengupdatenya.
    *   **Update `SettingsScreen`:** Tambahkan `OutlinedTextField` dan tombol `Save` untuk durasi istirahat pendek dan panjang.
    *   **Gunakan di `PomodoroService`:** `PomodoroService` harus membaca durasi ini dari `PreferencesManager` saat memulai sesi istirahat.
4.  **Integrasikan Siklus Istirahat Panjang (Long Break) Sepenuhnya di `PomodoroService`:**
    *   Lacak `pomodorosCompletedInCycle` di `PomodoroService`.
    *   Setelah `POMODOROS_PER_CYCLE` tercapai, transisikan ke `LONG_BREAK` menggunakan durasi yang sudah bisa diatur dari Settings.
    *   Reset `pomodorosCompletedInCycle` setelah `LONG_BREAK`.
5.  **Unit Test untuk Logika Timer di `PomodoroService`:** Setelah logika timer dipindahkan dan disempurnakan, tulis/update unit test untuk memastikan countdown, transisi sesi (termasuk long break), dan penanganan command di service berfungsi dengan benar.

---
## Status Tugas Kritis Pra-Rilis (Dari Daftar Sebelumnya)

Bagian ini mereview kembali item dari "Langkah Minimum Kritis Sebelum Rilis Publik" berdasarkan status MVP Complete.

*   **[TERATASI/RE-EVALUASI] ~~[TODO-RILIS-1.1] Amankan State Teks `FocusWriteScreen` dari Perubahan Konfigurasi:~~**
    *   *Catatan:* `progress.md` dan implementasi `MainViewModel` yang menangani penyimpanan teks melalui `PreferencesManager` (auto-save/load) kemungkinan besar sudah mengatasi masalah kehilangan teks akibat perubahan konfigurasi. Ini perlu diverifikasi kembali jika ada state teks lokal di `FocusWriteScreen.kt` yang tidak dikelola ViewModel.
*   **[SELESAI] ✅ Amankan State Input Field di `SettingsScreen` dari Perubahan Konfigurasi:**
    *   Penggunaan `rememberSaveable` untuk input fields di `SettingsScreen` telah diimplementasikan.
*   **[SELESAI] ✅ Implementasi Opsi Dasar Kontrol Suara Notifikasi:**
    *   Fitur kontrol suara notifikasi telah diimplementasikan (preferensi, UI switch, kondisional pemutaran suara).

---

Dengan menyelesaikan MVP, PureFocus memiliki fondasi yang kuat. Langkah-langkah berikutnya akan membangun di atas fondasi ini untuk menciptakan aplikasi yang lebih kaya fitur dan lebih matang. Selamat melanjutkan pengembangan!