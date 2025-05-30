# Status, To-Do, dan Saran Proyek PureFocus

Dokumen ini melacak status proyek saat ini, daftar pekerjaan yang direncanakan, dan saran untuk langkah-langkah kecil (baby steps) dalam pengembangan.

## Status Saat Ini (per [Tanggal Update Terakhir - misal, 30 Mei 2025])

### Yang Sudah Selesai (Done):

* **V0.1 - Fondasi & UI Timer Dasar:**
    * [x] Inisialisasi proyek Android dengan Kotlin & Jetpack Compose.
    * [x] Implementasi UI dasar untuk timer Pomodoro (tampilan waktu, tombol start/pause/reset/skip).
    * [x] Implementasi logika dasar ViewModel untuk timer (`PomodoroTimerViewModel`).
    * [x] Pengaturan Dependency Injection dengan Hilt.
    * [x] Pembuatan model data dasar (`PomodoroState`, `SessionType`).
* **V0.2 - Manajemen Preferensi:**
    * [x] Implementasi `PreferencesManager` menggunakan DataStore untuk menyimpan durasi sesi (fokus, istirahat pendek, istirahat panjang, siklus).
    * [x] Integrasi `PreferencesManager` dengan `PomodoroTimerViewModel` untuk menggunakan durasi yang disimpan.
    * [x] Pembuatan UI sederhana untuk mengatur preferensi durasi (mungkin belum ada, tapi logikanya sudah).
* **V0.3 - Logika Inti Timer Pomodoro:**
    * [x] Implementasi penuh logika `CountDownTimer` di `PomodoroTimerViewModel`.
    * [x] Penanganan transisi otomatis antar sesi (Fokus -> Istirahat Pendek -> Fokus -> Istirahat Panjang).
    * [x] Pelacakan siklus Pomodoro.
    * [x] Fungsionalitas tombol: Start, Pause, Reset, Skip.
* **V0.4 - Pengujian Unit Awal:**
    * [x] Penulisan unit test untuk `PreferencesManager`.
    * [x] Penulisan unit test untuk `PomodoroTimerViewModel` (logika timer, transisi sesi).
    * [x] Penulisan unit test untuk `MainViewModel` (jika ada logika spesifik).
* **Lainnya:**
    * [x] Penyiapan struktur proyek (MVVM).
    * [x] Dokumentasi awal di `memory-bank` (proposal, arsitektur, tech stack, dll.).
    * [x] UI Dasar untuk `MainScreen` yang menampilkan timer dan kontrol.
    * [x] Placeholder awal untuk `FocusWriteScreen.kt`.

### Yang Sedang Dikerjakan (In Progress):

* [ ] Penyempurnaan UI `MainScreen` berdasarkan feedback atau kebutuhan lebih lanjut.
* [ ] Integrasi awal `FocusWriteScreen` (navigasi atau tampilan kondisional).

## Daftar Pekerjaan di Masa Depan (Future To-Do)

### Prioritas Tinggi (MVP Lanjutan):

1.  **Implementasi Penuh Fitur "Focus Write Mode":**
    * Integrasi `TextField` untuk input teks di `FocusWriteScreen`.
    * Mekanisme penyimpanan otomatis teks yang ditulis pengguna (misalnya, per sesi atau secara periodik).
    * Mekanisme pemuatan teks terakhir saat membuka kembali sesi atau aplikasi.
    * Transisi UI yang mulus antara tampilan timer dan mode tulis fokus.
    * Menyembunyikan/menampilkan elemen UI yang tidak relevan saat dalam mode tulis.
2.  **Notifikasi Akhir Sesi:**
    * Implementasi notifikasi sistem untuk memberitahu pengguna akhir setiap sesi Pomodoro (fokus, istirahat).
    * Memungkinkan pengguna untuk memulai sesi berikutnya dari notifikasi (opsional).
3.  **UI Pengaturan Preferensi yang Lebih Baik:**
    * Jika belum ada, buat layar atau dialog khusus untuk pengguna mengatur durasi sesi dengan mudah.

### Prioritas Menengah:

4.  **Penyempurnaan UI/UX Keseluruhan:**
    * Animasi transisi.
    * Tema (terang/gelap) jika diinginkan.
    * Responsifitas layout untuk berbagai ukuran layar.
    * Iconography yang lebih baik.
5.  **Sound Notification/Alerts:**
    * Suara penanda akhir sesi (opsional, dapat dikonfigurasi pengguna).
6.  **Statistik Dasar (Sederhana):**
    * Pelacakan jumlah sesi fokus yang diselesaikan per hari (opsional, jika ada waktu).
7.  **Pengujian Instrumentasi/UI:**
    * Menulis tes UI untuk alur utama (misalnya, menjalankan satu siklus Pomodoro, interaksi dengan Focus Write Mode).

### Prioritas Rendah (Nice to Have):

8.  **Integrasi dengan Kalender/Tugas (Eksternal):** Sangat opsional.
9.  **Fitur Kustomisasi Lebih Lanjut:** Warna tema, suara notifikasi kustom.
10. **Tutorial/Onboarding Pengguna.**

## Saran "Baby-Step Todolist" (Fokus Berikutnya)

Berikut adalah langkah-langkah kecil yang disarankan untuk dikerjakan selanjutnya, berfokus pada **Implementasi Penuh Fitur "Focus Write Mode"**:

1.  **Baby Step 1: State Management untuk Teks di `FocusWriteScreen`**
    * **Tugas:** Tentukan bagaimana state teks di `FocusWriteScreen` akan dikelola.
    * **Detail:** Apakah akan ada `ViewModel` khusus untuk `FocusWriteScreen` (`FocusWriteViewModel`) atau state teks akan dikelola di `MainViewModel` atau `PomodoroTimerViewModel`?
    * **Rekomendasi Awal:** Buat `FocusWriteViewModel` untuk enkapsulasi logika terkait penulisan.
    * **Deliverable:** Keputusan desain dan pembuatan file `FocusWriteViewModel.kt` (kosong atau dengan struktur dasar).
2.  **Baby Step 2: UI Dasar `FocusWriteScreen` dengan `TextField` Aktif**
    * **Tugas:** Buat `TextField` yang berfungsi penuh di `FocusWriteScreen.kt`.
    * **Detail:** `TextField` harus bisa menerima input pengguna. State teks dari `TextField` ini harus terhubung ke `ViewModel` yang diputuskan di Baby Step 1.
    * **Deliverable:** `FocusWriteScreen.kt` yang menampilkan `TextField` dan input pengguna tercermin dalam state `ViewModel`.
3.  **Baby Step 3: Tampilan Kondisional `FocusWriteScreen`**
    * **Tugas:** Atur logika di `MainActivity` atau `MainScreen` untuk menampilkan `FocusWriteScreen` (atau kontennya) hanya selama sesi "Fokus" Pomodoro.
    * **Detail:** Sembunyikan timer utama atau elemen lain yang tidak relevan saat `FocusWriteScreen` aktif. `PomodoroTimerViewModel` menyediakan `currentSessionType`.
    * **Deliverable:** Aplikasi beralih antara tampilan timer dan tampilan tulis fokus sesuai dengan tipe sesi Pomodoro.
4.  **Baby Step 4: Penyimpanan Teks Sederhana (Misalnya ke DataStore atau File Lokal)**
    * **Tugas:** Implementasikan mekanisme untuk menyimpan teks dari `FocusWriteScreen` saat sesi fokus berakhir atau saat aplikasi ditutup.
    * **Detail:** Pilih mekanisme penyimpanan (DataStore untuk teks sederhana bisa jadi pilihan awal, atau file `.txt` di penyimpanan internal aplikasi). Simpan teks saat `onStop` di Activity/Fragment atau saat transisi sesi dari Fokus.
    * **Deliverable:** Teks yang ditulis pengguna disimpan.
5.  **Baby Step 5: Pemuatan Teks Sederhana**
    * **Tugas:** Implementasikan mekanisme untuk memuat teks yang tersimpan saat `FocusWriteScreen` ditampilkan atau saat sesi fokus dimulai.
    * **Detail:** Baca dari DataStore atau file lokal yang digunakan di Baby Step 4.
    * **Deliverable:** Teks yang sebelumnya disimpan muncul kembali di `FocusWriteScreen`.
6.  **Baby Step 6: Unit Test untuk `FocusWriteViewModel` (jika dibuat)**
    * **Tugas:** Tulis unit test untuk logika dasar di `FocusWriteViewModel` (misalnya, update state teks, trigger simpan/muat).
    * **Deliverable:** Suite pengujian untuk `FocusWriteViewModel`.