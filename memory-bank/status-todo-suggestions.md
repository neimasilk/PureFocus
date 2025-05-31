# Status, TODO, and Suggestions

## Current Status (Mei 2025)

* **Milestone Tercapai:** Fondasi aplikasi PureFocus dengan fitur inti telah berhasil dibangun dan stabil.
* **Fungsionalitas Utama:**
    * Timer Pomodoro (Fokus, Istirahat Pendek, Istirahat Panjang) berfungsi penuh.
    * Manajemen sesi (siklus pomodoro, transisi otomatis/manual) berjalan baik.
    * `PomodoroService` sebagai foreground service memastikan timer berjalan reliabel di background.
    * State timer (waktu tersisa, sesi saat ini, jumlah pomodoro selesai) berhasil disimpan dan dipulihkan menggunakan `PreferencesManager` (DataStore), membuat aplikasi resilient.
    * Pengaturan durasi timer (fokus, istirahat pendek, istirahat panjang, interval istirahat panjang) dapat dikonfigurasi pengguna dan disimpan.
    * Notifikasi untuk akhir setiap sesi (fokus, istirahat) berfungsi dengan baik.
    * Layar "Focus Write" minimalis telah terimplementasi sebagai area menulis bebas distraksi.
    * **[BARU]** Fungsionalitas "Clear All Text" dengan dialog konfirmasi telah diimplementasi di Focus Write Screen.
    * **[BARU]** Auto-save dan auto-load teks di Focus Write Screen menggunakan SharedPreferences dengan debouncing 1 detik.
    * **[BARU]** Persistent storage untuk teks Focus Write - teks tersimpan otomatis dan dipulihkan saat aplikasi dibuka kembali.
    * **[BARU - JANUARI 2025]** Word count dan character count real-time di Focus Write Screen - menampilkan jumlah kata dan karakter secara live saat mengetik.
    * Navigasi antar layar (Timer, Settings, Focus Write) menggunakan Jetpack Compose Navigation.
* **Kualitas & Teknis:**
    * Arsitektur MVVM dengan Hilt untuk Dependency Injection.
    * UI dibangun sepenuhnya menggunakan Jetpack Compose.
    * Penggunaan Coroutines dan Flow untuk operasi asynchronous dan state management.
    * Kode relatif bersih, terstruktur, dan mengikuti praktik modern Android.
* **Testing:**
    * Semua unit test untuk ViewModels dan Data Management (PreferencesManager) telah lolos.
    * Semua UI test untuk layar utama (PomodoroTimerScreen, FocusWriteScreen) telah lolos.
    * Cakupan tes memberikan kepercayaan yang baik terhadap fungsionalitas inti.

## TODO - Pekerjaan di Masa Depan (Fitur & Peningkatan)

### Prioritas Utama Berikutnya:

1.  **Focus Write Screen - Fitur Tambahan:**
    * **[SELESAI]** ~~Word count / character count display.~~
    * Mode full-screen opsional untuk pengalaman menulis yang lebih imersif.
    * Export/share text functionality (ke file, email, atau aplikasi lain).
    * Multiple text documents/notes management.
2.  **Focus Write Screen - Peningkatan UX:**
    * Undo/Redo functionality.
    * Text formatting options (bold, italic, bullet points).
    * Search and replace dalam teks.
3.  **Suara Notifikasi & Timer:**
    * Tambahkan opsi suara "tick-tock" halus selama sesi fokus (dapat diaktifkan/dinonaktifkan di settings).
    * Suara notifikasi yang berbeda/khas untuk akhir sesi fokus dan akhir sesi istirahat.
    * Opsi untuk memilih suara notifikasi dari sistem atau suara bawaan aplikasi.

### Prioritas Menengah:

4.  **Statistik & Riwayat Pomodoro:**
    * Lacak jumlah sesi fokus yang diselesaikan per hari/minggu.
    * Tampilkan statistik sederhana (misalnya, grafik batang atau ringkasan).
    * Membutuhkan persistensi data yang lebih terstruktur (kemungkinan Room Database).
5.  **Peningkatan UI/UX:**
    * Animasi transisi antar sesi yang lebih halus.
    * Opsi tema tambahan (misalnya, tema gelap yang lebih customizable, tema warna berbeda).
    * Review dan tingkatkan aspek aksesibilitas (uji dengan TalkBack, pastikan kontras warna cukup).
6.  **Pengaturan Lanjutan:**
    * Opsi untuk menonaktifkan auto-start break/focus berikutnya.
    * Opsi untuk custom suara notifikasi per tipe sesi.
    * "Daily Goal" untuk jumlah sesi Pomodoro.

### Prioritas Rendah / Ide Jangka Panjang:

7.  **Integrasi Kalender (Opsional):**
    * Sinkronisasi sesi fokus dengan kalender pengguna.
8.  **Mode "Strict" Pomodoro:**
    * Mencegah pengguna keluar dari aplikasi atau membuka aplikasi lain selama sesi fokus (membutuhkan izin khusus dan mungkin tidak sepenuhnya bisa di semua device).
9.  **Backup & Restore Pengaturan/Statistik:**
    * Menggunakan layanan backup Android atau ekspor/impor manual.

## Saran "Baby-Step Todolist" (Untuk Segera Dilakukan):

1.  **[DONE] Review `PomodoroService` state persistence:** Pastikan semua aspek state `PomodoroState` (termasuk `pomodorosCompletedThisCycle` dan `isLongBreakNext`) tersimpan dan terpulihkan dengan benar saat service di-restart atau aplikasi dibuka kembali. *(Sepertinya ini sudah cukup baik berdasarkan review kode, namun double check tidak ada salahnya).*
2.  **Desain Penyimpanan Teks untuk Focus Write:**
    * Putuskan mekanisme penyimpanan (file teks sederhana di internal storage, DataStore, atau field di `PreferencesManager` jika hanya satu catatan kecil).
    * Buat fungsi di `PreferencesManager` atau repositori baru untuk menyimpan dan mengambil teks Focus Write.
3.  **Implementasi Awal Simpan/Load Teks Focus Write:**
    * Tambahkan logic di `FocusWriteViewModel` (buat jika belum ada, atau di `MainViewModel`) untuk memanggil fungsi simpan/load.
    * Update `FocusWriteScreen` untuk memuat teks saat masuk dan menyimpan saat keluar (atau dengan tombol).
    * Tambahkan unit test untuk logika simpan/load teks.
4.  **UI Test untuk Simpan/Load Teks Focus Write:** Pastikan teks yang disimpan muncul kembali.
5.  **[SELESAI]** ~~Rencanakan Penambahan Word Count:~~
    * ~~Identifikasi bagaimana word count akan dihitung (real-time saat mengetik atau saat teks berubah).~~
    * ~~Desain bagaimana word count akan ditampilkan di UI `FocusWriteScreen`.~~

## Baby-Step Todolist Berikutnya (Januari 2025)

Setelah berhasil mengimplementasi word count feature, berikut adalah langkah-langkah kecil berikutnya yang bisa dikerjakan:

### Prioritas Tinggi:
1. **Mode Full-Screen untuk Focus Write:**
   * Tambahkan toggle button untuk masuk/keluar mode full-screen
   * Sembunyikan navigation bar dan status bar saat full-screen aktif
   * Pastikan word count tetap terlihat dalam mode full-screen

2. **Export/Share Text Functionality:**
   * Implementasi share text ke aplikasi lain (email, messaging, notes)
   * Tambahkan opsi export ke file .txt di storage device
   * Buat dialog untuk memilih format export (plain text, dengan metadata)

3. **Undo/Redo Functionality:**
   * Implementasi text history stack untuk undo/redo
   * Tambahkan gesture atau button untuk undo/redo
   * Optimasi memory usage untuk text history

### Prioritas Menengah:
4. **Multiple Text Documents Management:**
   * Desain UI untuk mengelola multiple notes/documents
   * Implementasi database lokal (Room) untuk menyimpan multiple documents
   * Fitur create, rename, delete documents

5. **Text Formatting Options:**
   * Implementasi basic formatting (bold, italic)
   * Bullet points dan numbering
   * Simple markdown support

Dengan langkah-langkah kecil ini, Anda bisa terus mengembangkan Focus Write Screen menjadi editor teks yang lebih powerful sambil tetap mempertahankan filosofi minimalis PureFocus.

Selamat melanjutkan pengembangan PureFocus! Proyek ini semakin matang dan menjanjikan.

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