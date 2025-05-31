# PureFocus: Status, To-Do & Saran

## Status Saat Ini (Per 31 Mei 2025)

Aplikasi PureFocus telah mencapai tonggak penting. Fungsionalitas inti telah berhasil diimplementasikan dan diverifikasi melalui serangkaian unit test dan UI test yang semuanya lolos.

**Fitur yang Telah Diimplementasikan & Diverifikasi:**

* **Timer Pomodoro Inti:**
    * Sesi Fokus, Istirahat Pendek, dan Istirahat Panjang.
    * Timer menghitung mundur secara akurat.
    * Fungsionalitas untuk Memulai, Jeda, Atur Ulang, dan Lewati sesi.
    * Siklus otomatis antar sesi (Fokus -> Istirahat Pendek, dengan Istirahat Panjang setelah sejumlah siklus tertentu).
* **Pengaturan:**
    * Durasi yang dapat disesuaikan untuk sesi Fokus, Istirahat Pendek, Istirahat Panjang.
    * Interval Istirahat Panjang yang dapat disesuaikan.
    * Pengaturan disimpan secara persisten menggunakan DataStore.
* **Layar Focus Write:**
    * Area input teks bebas gangguan.
    * Teks disimpan dan dimuat kembali sesuai dengan state sesi fokus (disimpan melalui DataStore).
* **Foreground Service:**
    * Timer Pomodoro berjalan sebagai foreground service, memastikan kelangsungan bahkan saat aplikasi di latar belakang atau layar mati.
    * Menampilkan notifikasi berkelanjutan untuk timer.
* **Notifikasi:**
    * Notifikasi penyelesaian sesi (Fokus, Istirahat Pendek, Istirahat Panjang berakhir).
* **Arsitektur & Teknologi:**
    * Arsitektur MVVM diterapkan secara konsisten.
    * Jetpack Compose untuk UI.
    * Hilt untuk Dependency Injection.
    * Kotlin Coroutines dan Flow untuk operasi asinkron dan manajemen state.
    * Proto DataStore untuk preferensi.
* **Pengujian:**
    * Unit test untuk ViewModels (`MainViewModel`, `PomodoroTimerViewModel`, `SettingsViewModel`).
    * Unit test untuk lapisan data (`PreferencesManager`).
    * UI test untuk `FocusWriteScreen`.
    * Semua tes yang ada dilaporkan lolos.

**Secara Keseluruhan:** Aplikasi ini fungsional dan memenuhi tujuan utama yang dituangkan dalam dokumentasi proyek (alat Pomodoro minimalis dan Focus Write). Basis kode terstruktur dengan baik.

## Daftar Pekerjaan di Masa Depan & Potensi Peningkatan

Berdasarkan kondisi saat ini dan filosofi proyek, berikut adalah area potensial untuk pengembangan dan penyempurnaan di masa depan:

### Fungsionalitas Inti & Penyempurnaan UX:

1.  **Notifikasi Suara & Kustomisasi:**
    * Implementasikan isyarat suara yang berbeda untuk penyelesaian sesi (misalnya, opsi suara detak jam, suara berbeda untuk akhir fokus vs. akhir istirahat).
    * Izinkan pengguna untuk memilih/menonaktifkan suara di pengaturan.
    * Pastikan manajemen fokus audio yang tepat jika suara kustom dimainkan.
2.  **Pemolesan UI/UX:**
    * **Umpan Balik Visual:** Isyarat visual yang ditingkatkan untuk transisi sesi (misalnya, animasi halus, perubahan warna yang lebih jelas).
    * **Tampilan Timer:** Pertimbangkan opsi untuk tampilan timer (misalnya, gaya progress bar, timer melingkar).
    * **Tinjauan Aksesibilitas:** Lakukan audit aksesibilitas menyeluruh (rasio kontras, dukungan TalkBack, ukuran target sentuh).
3.  **Peningkatan Focus Write (Pendekatan Minimalis):**
    * **Penghitung Kata Opsional:** Tampilan penghitung kata yang sederhana dan tidak mengganggu.
    * **Ekspor/Salin Dasar:** Cara yang lebih mudah untuk mengeluarkan teks (misalnya, tombol "salin semua"). (Saat ini pengguna dapat memilih dan menyalin secara manual).
4.  **Penanganan Error & Ketahanan:**
    * Tinjau `PomodoroService` untuk kasus-kasus tepi selama peristiwa sistem (misalnya, situasi memori rendah, meskipun foreground service membantu).
    * Pastikan pemulihan state yang konsisten jika proses aplikasi dimatikan dan dimulai ulang.
5.  **Mode "Selalu di Atas" (Desktop/Versi Android Tertentu):**
    * Untuk lingkungan seperti desktop (misalnya, Samsung DeX) atau jika diinginkan, opsi untuk menjaga jendela mini timer selalu terlihat (mungkin memerlukan izin/API khusus). Ini adalah fitur yang lebih canggih.

### Hutang Teknis & Pemeliharaan:

1.  **Cakupan Tes yang Diperluas:**
    * Pengujian yang lebih komprehensif terhadap logika `PomodoroService`, terutama siklus sesi dan interval istirahat panjang dalam berbagai kondisi.
    * Uji interaksi antara `MainActivity` dan `MainViewModel`.
    * Pertimbangkan untuk menambahkan tes untuk kemunculan notifikasi.
2.  **Dokumentasi Kode (KDoc):**
    * Tingkatkan cakupan KDoc untuk API publik di ViewModels, Services, dan kelas utilitas.
3.  **Pemantauan Kinerja:**
    * Profil aplikasi untuk penggunaan baterai dan CPU, terutama foreground service dan pembaruan timer, untuk memastikannya tetap efisien. `PerformanceMonitor.kt` yang ada adalah titik awal.
4.  **Pembaruan Dependensi:**
    * Secara teratur periksa dan perbarui dependensi.

### Fitur Tambahan (Pertimbangkan dengan hati-hati terhadap minimalisme):

1.  **Tema:**
    * Variasi tema terang/gelap sederhana di luar default sistem jika belum sepenuhnya kuat.
2.  **Statistik Dasar:**
    * Lacak Pomodoro yang diselesaikan per hari/minggu (statistik yang sangat sederhana, hanya lokal). Ini bisa memotivasi tetapi menambah kompleksitas.

## "Baby-Step" To-Do List (Langkah Berikutnya yang Segera)

Mengingat fungsionalitas inti stabil dan telah diuji:

1.  **✅ Gabungkan ke Branch Main/Stabil (jika belum):**
    * Pastikan semua perubahan terbaru dan tes yang lolos berada di branch yang stabil.
2.  **Sesi Pengujian Manual Menyeluruh:**
    * Lakukan pengujian manual ekstensif di berbagai skenario:
        * Siklus Pomodoro penuh (beberapa fokus -> istirahat pendek -> istirahat panjang).
        * Menjeda/melanjutkan selama berbagai jenis sesi.
        * Melewatkan sesi.
        * Mengatur ulang timer.
        * Mengubah pengaturan saat timer berjalan/dijeda/diam.
        * Aplikasi di latar belakang, layar mati untuk waktu yang lama.
        * Aplikasi ditutup dan dibuka kembali – periksa pemulihan state timer dan teks Focus Write.
        * Paksa henti aplikasi dan periksa pemulihan.
3.  **Tindak Lanjuti Temuan `debug_log.txt`:**
    * Selidiki log yang tidak biasa di `debug_log.txt` (seperti entri "Force stopping") untuk memastikan itu bukan indikasi masalah mendasar. Bersihkan dan pantau entri baru selama pengujian menyeluruh.
4.  **Pemolesan UI Kecil (Yang Mudah Dikerjakan):**
    * Tinjau label teks, penempatan tombol, dan padding untuk inkonsistensi kecil atau area untuk perbaikan segera yang selaras dengan minimalisme.
    * Contoh: Pastikan jenis sesi saat ini (Fokus, Pendek, Panjang) selalu jelas terlihat di layar timer.
5.  **✅ SELESAI - Notifikasi Suara Dasar:**
    * ✅ Implementasi notifikasi suara untuk penyelesaian sesi telah selesai:
        * ✅ Modifikasi `NotificationHelper` untuk memainkan suara notifikasi default sistem.
        * ✅ Menambahkan event terpisah untuk notifikasi akhir sesi fokus dan istirahat.
        * ✅ Integrasi dengan `PomodoroTimerViewModel` dan `MainActivity`.
        * ✅ Semua unit test dan instrumented test lolos.
    * **Catatan:** Menggunakan suara notifikasi default sistem untuk kesederhanaan. File audio kustom dapat ditambahkan di masa depan jika diperlukan.
    * **Opsional untuk masa depan:** Menambahkan sakelar pengaturan untuk mengaktifkan/menonaktifkan suara.
6.  **Sprint Dokumentasi (KDoc):**
    * Tinjau `PomodoroService.kt`, `PomodoroTimerViewModel.kt`, dan `PreferencesManager.kt` dan tambahkan/perbaiki komentar KDoc untuk semua fungsi publik dan bagian logika penting. Ini membuat pengembangan di masa depan lebih mudah.

Dengan berfokus pada langkah-langkah kecil ini, Anda dapat memperkuat versi yang sudah kuat saat ini dan kemudian secara strategis memilih dari daftar "Pekerjaan di Masa Depan" berdasarkan umpan balik pengguna (jika sudah ada) atau visi Anda untuk evolusi aplikasi, selalu dengan mengingat filosofi minimalis "PureFocus".