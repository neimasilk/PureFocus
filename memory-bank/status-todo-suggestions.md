# Status, To-Do, and Suggestions for PureFocus

## Status Saat Ini (per 31 Mei 2025)

**Konteks Proyek:** PureFocus adalah aplikasi Android yang dirancang untuk meningkatkan fokus pengguna melalui teknik Pomodoro dan fitur "Focus Write". Arsitektur mengikuti MVVM dengan Hilt untuk DI, Coroutines & Flow untuk asynchronous programming, DataStore untuk preferensi, dan Jetpack Compose untuk UI.

**Pencapaian Utama:**
* Fondasi aplikasi (struktur proyek, DI, navigasi dasar) telah dibangun.
* Logika inti untuk Pomodoro Timer (`PomodoroTimerViewModel`) telah diimplementasikan, termasuk:
    * Manajemen state (Idle, Running, Paused).
    * Transisi antar sesi (Focus, Short Break, Long Break).
    * Fungsi timer (start, pause, reset, skip).
    * Penghitungan siklus Pomodoro.
* Manajemen preferensi untuk durasi sesi (`PreferencesManager` menggunakan DataStore) telah diimplementasikan.
* Layar awal untuk "Focus Write" (`FocusWriteScreen`) telah dibuat menggunakan Jetpack Compose, menampilkan timer dan area input teks dasar.
* Unit test untuk `PreferencesManager` dan `PomodoroTimerViewModel` telah dibuat dan **semua lolos**. Pengujian Flow menggunakan `Turbine`.
* Dependensi proyek telah dikonfigurasi dengan baik.
* Dasar `PerformanceMonitor` telah ada.

**Kesimpulan Status:**
Proyek berada pada tahap di mana fungsionalitas inti dari Pomodoro timer dan layar dasar untuk Focus Write sudah terimplementasi dan teruji pada level ViewModel dan Data. Aplikasi siap untuk pengembangan fitur lebih lanjut, penyempurnaan UI/UX, dan penambahan fungsionalitas pendukung.

## Daftar Pekerjaan di Masa Depan (Future To-Do List)

### Prioritas Tinggi (Fitur Inti & Fungsionalitas Dasar)

1.  **Implementasi Notifikasi Timer:**
    * [ ] Buat notifikasi untuk akhir setiap sesi Pomodoro (Focus, Short Break, Long Break).
    * [ ] Pastikan notifikasi berfungsi bahkan ketika aplikasi di background.
    * [ ] Tambahkan opsi suara notifikasi (bisa di iterasi berikutnya).
2.  **Foreground Service untuk Timer Akurat:**
    * [ ] Implementasikan Foreground Service untuk memastikan timer Pomodoro berjalan terus meskipun aplikasi tidak aktif atau layar terkunci.
    * [ ] Tampilkan notifikasi persisten saat timer berjalan di service.
    * [ ] Kelola lifecycle service dengan benar.
3.  **Penyimpanan Teks untuk Fitur "Focus Write":**
    * [ ] Tentukan mekanisme penyimpanan teks (misalnya, file lokal per sesi, database sederhana).
    * [ ] Implementasikan fungsi simpan otomatis atau manual untuk teks yang diketik di `FocusWriteScreen`.
    * [ ] Pikirkan tentang bagaimana pengguna akan mengakses/melihat kembali tulisan yang disimpan.
4.  **UI Pengaturan (Settings Screen):**
    * [ ] Buat layar Pengaturan baru.
    * [ ] Izinkan pengguna untuk mengubah durasi sesi Focus, Short Break, dan Long Break.
    * [ ] Hubungkan UI Pengaturan dengan `PreferencesManager`.
5.  **Penyempurnaan UI/UX `FocusWriteScreen`:**
    * [ ] Perjelas interaksi antara timer Pomodoro dan input teks (misalnya, apakah input dinonaktifkan saat break?).
    * [ ] Tingkatkan feedback visual (misalnya, transisi state timer, progress bar yang lebih informatif).
    * [ ] Pertimbangkan mode "distraction-free" yang lebih immersive.

### Prioritas Menengah (Penyempurnaan & Fitur Tambahan)

6.  **State Restoration yang Lebih Kuat:**
    * [ ] Pastikan `PomodoroTimerViewModel` dan `FocusWriteScreen` (terutama teks yang belum disimpan) dapat memulihkan state setelah process death (menggunakan `SavedStateHandle`).
7.  **Instrumented Tests untuk UI:**
    * [ ] Tulis instrumented tests untuk `FocusWriteScreen` untuk memverifikasi interaksi tombol, tampilan timer, dan input teks.
8.  **Estetika dan Tema Aplikasi:**
    * [ ] Sempurnakan tema warna dan tipografi aplikasi.
    * [ ] Pertimbangkan mode gelap/terang.
9.  **Pelacakan Progres dan Statistik Sederhana:**
    * [ ] Simpan jumlah sesi Pomodoro yang diselesaikan per hari.
    * [ ] Tampilkan statistik sederhana kepada pengguna (misalnya, di layar utama atau layar statistik baru).
10. **Suara Notifikasi yang Dapat Disesuaikan:**
    * [ ] Izinkan pengguna memilih suara notifikasi atau mengunggah suara sendiri.
11. **Handling Izin Runtime (jika diperlukan di masa depan):**
    * [ ] Jika ada fitur yang memerlukan izin tambahan (misalnya, akses file lebih lanjut), implementasikan alur permintaan izin yang benar.

### Prioritas Rendah (Fitur Lanjutan & Polish)

12. **Tujuan Sesi Harian:**
    * [ ] Izinkan pengguna mengatur target jumlah sesi Pomodoro per hari.
    * [ ] Tampilkan progres menuju target tersebut.
13. **Integrasi Kalender (Opsional Lanjutan):**
    * [ ] Izinkan pengguna untuk menjadwalkan sesi fokus di kalender mereka.
14. **Mode "Focus Write" dengan Opsi Ekspor Teks:**
    * [ ] Tambahkan opsi untuk mengekspor teks dari sesi "Focus Write" ke format file (misalnya, .txt, .md).
15. **Panduan Pengguna / Onboarding:**
    * [ ] Buat layar perkenalan singkat untuk pengguna baru.
16. **Lokalisasi:**
    * [ ] Persiapkan aplikasi untuk mendukung berbagai bahasa.

## Saran "Baby-Step To-Do List" (Langkah Kecil Berikutnya)

Pilih salah satu atau beberapa dari langkah kecil berikut untuk progres yang terukur:

1.  **Baby Step: Notifikasi Sederhana untuk Akhir Sesi Fokus.**
    * **Tugas:** Implementasikan notifikasi dasar (tanpa suara kustom, tanpa service dulu) yang muncul ketika sesi Fokus berakhir.
    * **Detail:** Gunakan `NotificationManagerCompat`. Fokus pada fungsionalitas dasar notifikasi.
    * **File Terkait:** `PomodoroTimerViewModel.kt`, `MainActivity.kt` (atau service di masa depan).
2.  **Baby Step: Input Durasi Sesi Fokus di `PreferencesManager` dan UI Pengaturan Sederhana.**
    * **Tugas:** Tambahkan satu field input di layar Pengaturan (buat layar baru yang sangat sederhana) untuk mengubah durasi sesi Fokus. Simpan dan ambil nilai ini menggunakan `PreferencesManager`.
    * **Detail:** Buat Composable baru untuk Settings, gunakan `TextField` untuk input, dan pastikan `PomodoroTimerViewModel` membaca durasi ini.
    * **File Terkait:** `PreferencesManager.kt`, `PomodoroTimerViewModel.kt`, buat file baru untuk `SettingsScreen.kt` & `SettingsViewModel.kt` (opsional untuk VM jika logikanya simpel).
3.  **Baby Step: Simpan Teks dari `FocusWriteScreen` ke Logcat Saat Sesi Berakhir (Placeholder untuk Penyimpanan Nyata).**
    * **Tugas:** Modifikasi `FocusWriteScreen` atau `PomodoroTimerViewModel` sehingga ketika sesi fokus berakhir, teks yang ada di `textState` di-log ke Logcat.
    * **Detail:** Ini hanya langkah sementara untuk memikirkan alur penyimpanan sebelum implementasi penyimpanan file/database yang sebenarnya.
    * **File Terkait:** `FocusWriteScreen.kt`, `PomodoroTimerViewModel.kt`.
4.  **Baby Step: Membuat Dasar Foreground Service (Tanpa Logika Timer Komplit).**
    * **Tugas:** Buat kelas `Service` baru, deklarasikan di `AndroidManifest.xml` sebagai foreground service. Implementasikan `onStartCommand` untuk memulai service dalam mode foreground dengan notifikasi placeholder.
    * **Detail:** Fokus pada setup dasar service dan notifikasi persistennya. Belum perlu memindahkan semua logika timer ke sini.
    * **File Terkait:** Buat `PomodoroService.kt`, `AndroidManifest.xml`.
5.  **Baby Step: Menulis Satu Instrumented Test untuk `FocusWriteScreen`.**
    * **Tugas:** Buat satu tes UI sederhana yang memeriksa apakah tombol "Start" ditampilkan di `FocusWriteScreen`.
    * **Detail:** Gunakan `createComposeRule` dan `onNodeWithText("Start").assertIsDisplayed()`.
    * **File Terkait:** `ExampleInstrumentedTest.kt` atau buat file test baru untuk `FocusWriteScreen`.

Pilih baby step yang paling sesuai dengan prioritas Anda saat ini. Ini akan membantu menjaga momentum dan memberikan hasil yang terlihat dengan cepat.