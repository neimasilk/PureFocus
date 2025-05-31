# Status, Todolist, dan Saran Proyek PureFocus

Dokumen ini melacak status saat ini, daftar pekerjaan di masa depan, dan saran "baby-step todolist" untuk pengembangan PureFocus.

## Status Saat Ini (Per 31 Mei 2025)

* **Fitur Inti:**
    * Timer Pomodoro (Focus, Short Break, Long Break) dapat dimulai, dijeda, di-reset, dan otomatis beralih antar sesi.
    * Durasi sesi dapat dikonfigurasi melalui layar Pengaturan.
    * Notifikasi dikirim saat sesi Pomodoro berakhir.
    * Layar "Focus Write" menyediakan lingkungan menulis minimalis dan teks otomatis tersimpan.
* **Arsitektur & Teknologi:**
    * Aplikasi dibangun dengan Kotlin dan Jetpack Compose.
    * Menggunakan arsitektur MVVM.
    * Hilt digunakan untuk Dependency Injection.
    * DataStore Preferences digunakan untuk menyimpan pengaturan pengguna.
    * Coroutines digunakan untuk operasi asynchronous.
* **Pengujian:**
    * Tes unit untuk `PreferencesManager`, `MainViewModel`, dan `PomodoroTimerViewModel` sudah ada dan lolos.
    * Tes instrumentasi dasar untuk `FocusWriteScreen` sudah ada dan lolos.
    * Secara umum, semua tes yang ada saat ini lolos.
* **UI/UX:**
    * UI dasar untuk timer, pengaturan, dan focus write telah diimplementasikan.
    * Mode gelap/terang mengikuti sistem.
* **Dokumentasi Proyek:**
    * Dokumen desain produk, arsitektur, rencana implementasi, dan lain-lain tersedia di direktori `memory-bank`.

## Review & Kesiapan

Berdasarkan tinjauan kode terakhir, fondasi aplikasi sudah baik. Fitur inti berfungsi pada level dasar.

**Bisa lanjut ke step berikutnya? Ya, dengan catatan:**
* Tingkatkan cakupan tes (terutama UI test untuk alur Pomodoro & Settings, dan Service test).
* Pertimbangkan untuk melakukan refactoring minor pada beberapa area (konstanta, KDoc).
* Lakukan pengujian manual menyeluruh.

## Daftar Pekerjaan di Masa Depan (Fitur & Peningkatan)

### Prioritas Tinggi / MVP Lanjutan
* [ ] **Peningkatan UI/UX Timer:**
    * [ ] Visualisasi progres yang lebih menarik (misalnya, circular progress bar yang lebih smooth).
    * [ ] Indikator visual yang jelas untuk jumlah sesi Pomodoro yang telah selesai vs. target.
    * [ ] Tombol "Skip Break" atau "Start Focus" langsung dari notifikasi (jika memungkinkan dan UX baik).
* [ ] **Pengaturan Lanjutan:**
    * [ ] Opsi untuk mengaktifkan/menonaktifkan suara notifikasi.
    * [ ] Opsi untuk memilih suara notifikasi.
    * [ ] Opsi "Auto-start next session" (otomatis memulai sesi berikutnya tanpa intervensi).
    * [ ] Opsi "Auto-start break after focus" dan "Auto-start focus after break".
* [ ] **Peningkatan "Focus Write":**
    * [ ] Indikator jumlah kata/karakter.
    * [ ] Opsi ekspor teks ke file atau clipboard (jika belum ada atau mudah diakses).
    * [ ] Konfirmasi sebelum menghapus semua teks (jika ada tombol hapus).
* [ ] **Stabilitas & Performa:**
    * [ ] Optimasi `PomodoroService` untuk konsumsi baterai.
    * [ ] Pengujian menyeluruh pada berbagai versi Android dan perangkat.
    * [ ] Implementasi `PerformanceMonitor` yang lebih konkret jika diperlukan.
* [ ] **Peningkatan Tes:**
    * [ ] Tambah UI test untuk `PomodoroTimerScreen` (alur start, pause, reset, transisi sesi).
    * [ ] Tambah UI test untuk `SettingsScreen` (mengubah pengaturan dan memverifikasi efeknya).
    * [ ] Tes untuk `PomodoroService`.

### Prioritas Menengah
* [ ] **Fitur "Focus Read":**
    * [ ] Desain UI/UX untuk mode membaca bebas gangguan.
    * [ ] Implementasi dasar (mungkin load file teks sederhana atau input teks).
* [ ] **Statistik Sesi Sederhana:**
    * [ ] Pelacakan jumlah sesi fokus yang diselesaikan per hari.
    * [ ] Tampilan statistik sederhana (misalnya, kalender atau grafik bar).
    * **Catatan:** Jaga privasi pengguna, simpan data hanya secara lokal.
* [ ] **Kustomisasi Tema:**
    * [ ] Opsi tema tambahan (selain hanya gelap/terang sistem).
* [ ] **Peningkatan Aksesibilitas (A11y):**
    * [ ] Audit dan perbaikan aksesibilitas.
* [ ] **Refactoring & Pembersihan Kode:**
    * [ ] Tambahkan KDoc secara konsisten.
    * [ ] Tinjau ulang penggunaan konstanta dan sumber daya string.

### Prioritas Rendah / Ide Jangka Panjang
* [ ] Sinkronisasi antar perangkat (jika ada permintaan dan tetap menjaga privasi).
* [ ] Integrasi dengan kalender atau aplikasi to-do list (opsional).
* [ ] Suara ambien untuk membantu fokus.
* [ ] Gamifikasi sederhana (misalnya, streaks).

## Saran "Baby-Step Todolist" (Langkah-langkah Kecil Berikutnya)

1.  **[SELESAI] [Refactor] Definisikan Konstanta:**
    * Identifikasi *magic strings* (kunci preferensi, action intent) dan angka (durasi default) yang digunakan di beberapa tempat.
    * Pindahkan ke objek `companion object` atau berkas `Constants.kt`.
    * **Contoh:** `PreferencesManager.KEY_FOCUS_DURATION`, `PomodoroService.ACTION_START`.
2.  **[SELESAI] [Test] Tingkatkan UI Test untuk PomodoroTimer:**
    * Buat satu tes UI sederhana untuk `MainActivity` (atau layar utama Pomodoro) yang memverifikasi:
        * Timer ditampilkan dengan durasi default.
        * Menekan tombol "Start" mengubah teks tombol menjadi "Pause" (atau sebaliknya) dan timer mulai berjalan (cek perubahan teks waktu).
    * **Perkakas:** Espresso atau `createComposeRule()`.
3.  **[KDoc] Tambahkan KDoc untuk Satu ViewModel:**
    * Pilih satu ViewModel (misalnya, `PomodoroTimerViewModel`).
    * Tambahkan KDoc untuk kelas dan semua fungsi publiknya, menjelaskan apa yang dilakukannya, parameternya, dan apa yang dikembalikannya (jika ada).
4.  **[UX] Tinjau Alur Notifikasi:**
    * Uji secara manual bagaimana notifikasi `PomodoroService` muncul.
    * Apakah sudah jelas? Apakah ada yang bisa ditingkatkan (misalnya, teks notifikasi)?
5.  **[BugFix/Refactor] Tinjau Implementasi `PerformanceMonitor`:**
    * Pastikan hanya ada satu definisi *interface* `PerformanceMonitor` di `src/main`.
    * Jika ada implementasi default untuk produksi, letakkan di `src/main`.
    * Gunakan `MockPerformanceMonitor` hanya untuk tes.
6.  **[Refactor] `.gitignore`:**
    * Tambahkan `debug_log.txt` dan pola `*.log` (misalnya, dari direktori `.kotlin/errors/`) ke berkas `.gitignore` Anda.

Pilih salah satu dari "baby-steps" di atas untuk dikerjakan terlebih dahulu. Ini akan membantu menjaga momentum dan terus meningkatkan kualitas proyek secara bertahap.