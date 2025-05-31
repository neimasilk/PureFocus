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
    * Word count / character count display.
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
5.  **Rencanakan Penambahan Word Count:**
    * Identifikasi bagaimana word count akan dihitung (real-time saat mengetik atau saat teks berubah).
    * Desain bagaimana word count akan ditampilkan di UI `FocusWriteScreen`.

Dengan langkah-langkah kecil ini, Anda bisa mulai mengerjakan fitur penyimpanan teks untuk `FocusWriteScreen` yang merupakan langkah logis berikutnya berdasarkan rencana dan status saat ini.

Selamat melanjutkan pengembangan PureFocus! Proyek ini terlihat menjanjikan.