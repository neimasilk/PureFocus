# Baby-Step Todolist untuk Pengembangan PureFocus

Dokumen ini merinci langkah-langkah pengembangan yang sangat detail (baby steps) untuk fitur-fitur yang direncanakan dalam aplikasi PureFocus. Setiap langkah dirancang agar jelas, tidak ambigu, dan dapat dikerjakan oleh junior developer. 

**PENTING:** Sebelum melanjutkan ke langkah berikutnya, **PASTIKAN SEMUA TES YANG RELEVAN TELAH LOLOS (PASSING)**. Jika ada tes yang gagal, perbaiki masalahnya terlebih dahulu sebelum melanjutkan.

---

## Tahap 1: Refactoring Logika Timer ke `PomodoroService` dan Komunikasi Dasar

### Langkah 1.1: Pindahkan Logika Inti Timer dari `PomodoroTimerViewModel` ke `PomodoroService` ✅ **SELESAI**

**Tujuan:** Memisahkan tanggung jawab pengelolaan timer dari ViewModel ke Service agar timer dapat berjalan secara independen di background.

**Status:** ✅ **COMPLETED** - Timer logic sudah dipindahkan ke PomodoroService, unit tests sudah diperbaiki dan passing.

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/PomodoroTimerViewModel.kt`
*   `app/src/main/java/com/neimasilk/purefocus/service/PomodoroService.kt`

**Detail Langkah:**

1.  **Di `PomodoroService.kt`:**
    *   Buat `MutableStateFlow` internal untuk merepresentasikan state Pomodoro saat ini. State ini bisa berupa data class yang mirip dengan `PomodoroState` di ViewModel, atau minimal mencakup `timeLeftInMillis: Long`, `currentSessionType: SessionType`, dan `isRunning: Boolean`.
        ```kotlin
        // Contoh di dalam PomodoroService
        private val _serviceState = MutableStateFlow(PomodoroServiceState()) // Definisikan PomodoroServiceState
        val serviceState: StateFlow<PomodoroServiceState> = _serviceState.asStateFlow()

        data class PomodoroServiceState(
            val timeLeftInMillis: Long = 0L,
            val currentSessionType: SessionType = SessionType.FOCUS,
            val isRunning: Boolean = false,
            // tambahkan field lain jika perlu
        )
        ```
    *   Pindahkan logika countdown (loop `while`, `delay`, pembaruan `timeLeftInMillis`) dari `PomodoroTimerViewModel` ke dalam sebuah fungsi di `PomodoroService`, misalnya `startTimerInternal()`.
    *   Fungsi ini harus di-launch dalam `CoroutineScope` milik service.
    *   Setiap detik, update `_serviceState` dengan `timeLeftInMillis` yang baru.
    *   Pindahkan logika `handleSessionFinish()` (atau yang serupa untuk menangani akhir sesi) dari ViewModel ke Service. Fungsi ini akan dipanggil ketika timer mencapai nol.
    *   Pastikan Service dapat menerima durasi sesi (fokus, istirahat pendek) dari `PreferencesManager` saat memulai timer.

2.  **Di `PomodoroTimerViewModel.kt`:**
    *   Hapus logika countdown (`timerJob`, loop `while`, `delay`) dan pemanggilan `handleSessionFinish()` yang sudah dipindahkan.
    *   ViewModel sekarang akan bertugas mengirim *command* ke `PomodoroService` untuk mengontrol timer.

3.  **Tes:** Belum ada tes spesifik di langkah ini, fokus pada pemindahan kode. Pastikan aplikasi masih bisa di-compile.

### Langkah 1.2: Implementasikan Kontrol Timer dari `PomodoroTimerViewModel` ke `PomodoroService` melalui Intent

**Tujuan:** Memungkinkan ViewModel untuk memulai, menjeda, mengatur ulang, dan melewati sesi timer yang dikelola oleh `PomodoroService`.

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/PomodoroTimerViewModel.kt`
*   `app/src/main/java/com/neimasilk/purefocus/service/PomodoroService.kt`

**Detail Langkah:**

1.  **Di `PomodoroService.kt`:**
    *   Dalam `onStartCommand()`, tambahkan penanganan untuk action `Intent` baru seperti `ACTION_PAUSE_TIMER`, `ACTION_RESET_TIMER`, `ACTION_SKIP_SESSION` selain `ACTION_START_SERVICE` dan `ACTION_STOP_SERVICE` yang mungkin sudah ada.
    *   Implementasikan fungsi internal di Service untuk menangani setiap command ini:
        *   `pauseTimerInternal()`: Menghentikan coroutine timer (jika berjalan) dan mengupdate `_serviceState` (misalnya `isRunning = false`).
        *   `resetTimerInternal()`: Menghentikan timer, mereset `timeLeftInMillis` ke durasi sesi saat ini, dan mengupdate `_serviceState`.
        *   `skipSessionInternal()`: Memanggil logika `handleSessionFinish()` untuk langsung beralih ke sesi berikutnya.
        *   `startTimerInternal()` (yang sudah dibuat/dipindahkan): Memulai atau melanjutkan countdown.

2.  **Di `PomodoroTimerViewModel.kt`:**
    *   Modifikasi fungsi seperti `startTimer()`, `pauseTimer()`, `resetTimer()`, `skipSession()` untuk membuat `Intent` dengan action yang sesuai dan mengirimkannya ke `PomodoroService` menggunakan `context.startService(intent)`.

3.  **Tes:** Belum ada tes otomatis. Jalankan aplikasi secara manual. Coba tekan tombol Start, Pause, Reset, dan Skip (jika ada UI-nya). Periksa Logcat untuk memastikan Service menerima Intent dan (secara teori) mengubah state internalnya. UI belum akan terupdate.

### Langkah 1.3: Implementasikan Komunikasi Dasar dari `PomodoroService` ke UI (ViewModel) menggunakan `LocalBroadcastManager`

**Tujuan:** Memungkinkan UI (melalui ViewModel) untuk menampilkan state timer terbaru (waktu tersisa, jenis sesi) yang dikelola oleh `PomodoroService`.

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/PomodoroTimerViewModel.kt` (atau `MainActivity.kt` jika receiver lebih cocok di sana)
*   `app/src/main/java/com/neimasilk/purefocus/service/PomodoroService.kt`

**Detail Langkah:**

1.  **Di `PomodoroService.kt`:**
    *   Setiap kali `_serviceState` internal di Service diupdate (misalnya setiap detik oleh timer, atau setelah command pause/reset), kirim `Intent` broadcast menggunakan `LocalBroadcastManager`.
    *   `Intent` ini harus berisi data dari `_serviceState` (misalnya `timeLeftInMillis`, `currentSessionType`, `isRunning`) sebagai extra.
    *   Definisikan action string yang unik untuk broadcast ini, misalnya `ACTION_TIMER_STATE_UPDATE`.

2.  **Di `PomodoroTimerViewModel.kt` (atau `MainActivity.kt`):**
    *   Buat `BroadcastReceiver`.
    *   Dalam `onReceive()` milik receiver, ekstrak data state timer dari `Intent` yang diterima.
    *   Update `_uiState` di `PomodoroTimerViewModel` dengan data yang diterima dari Service.
    *   Daftarkan receiver ini (misalnya di `init` block ViewModel atau `onCreate` Activity) untuk mendengarkan action `ACTION_TIMER_STATE_UPDATE`.
    *   Jangan lupa unregister receiver saat ViewModel di-cleared atau Activity di-destroy untuk mencegah memory leak.

3.  **Tes:** Jalankan aplikasi. UI sekarang seharusnya mencerminkan perubahan state timer (countdown, perubahan sesi setelah selesai) yang dikelola oleh Service. Verifikasi bahwa Start, Pause, Reset, Skip (jika ada) berfungsi dan UI terupdate dengan benar.

---

## Tahap 2: Pengaturan Durasi Istirahat dan Unit Testing

### Langkah 2.1: Implementasi Pengaturan Durasi Istirahat Pendek di Layar Pengaturan

**Tujuan:** Memungkinkan pengguna untuk mengkonfigurasi durasi sesi istirahat pendek.

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/data/PreferencesManager.kt`
*   `app/src/main/java/com/neimasilk/purefocus/ui/SettingsViewModel.kt`
*   `app/src/main/java/com/neimasilk/purefocus/ui/screens/SettingsScreen.kt`
*   `app/src/main/java/com/neimasilk/purefocus/service/PomodoroService.kt`

**Detail Langkah:**

1.  **Di `PreferencesManager.kt`:**
    *   Tambahkan konstanta untuk key preferensi durasi istirahat pendek (misalnya `KEY_SHORT_BREAK_DURATION`).
    *   Tambahkan fungsi untuk menyimpan (`saveShortBreakDuration(minutes: Int)`) dan mengambil (`getShortBreakDuration(): Flow<Int>`) nilai durasi istirahat pendek. Gunakan nilai default yang masuk akal (misalnya 5 menit).

2.  **Di `SettingsViewModel.kt`:**
    *   Tambahkan `StateFlow` untuk menampung nilai input durasi istirahat pendek dari UI (misalnya `shortBreakDurationInput: MutableStateFlow<String>`).
    *   Tambahkan `StateFlow` untuk menampung nilai durasi istirahat pendek yang tersimpan (misalnya `currentShortBreakDuration: StateFlow<Int>`), yang di-collect dari `PreferencesManager`.
    *   Tambahkan fungsi untuk menyimpan durasi istirahat pendek baru yang diinput pengguna ke `PreferencesManager` (misalnya `updateShortBreakDuration()`). Lakukan validasi input sebelum menyimpan.

3.  **Di `SettingsScreen.kt`:**
    *   Tambahkan `OutlinedTextField` yang terikat dengan `shortBreakDurationInput` dari `SettingsViewModel`.
    *   Tambahkan `Button` "Simpan" atau integrasikan dengan tombol simpan yang sudah ada untuk memanggil `updateShortBreakDuration()` di ViewModel.
    *   Pastikan UI menampilkan nilai durasi istirahat pendek yang saat ini tersimpan.

4.  **Di `PomodoroService.kt`:**
    *   Saat memulai sesi istirahat pendek (dalam logika `handleSessionFinish()` atau yang serupa), baca durasi istirahat pendek dari `PreferencesManager` dan gunakan nilai tersebut untuk mengatur `timeLeftInMillis`.

5.  **Tes:** Buka layar Pengaturan. Ubah nilai durasi istirahat pendek dan simpan. Mulai sesi Pomodoro, selesaikan sesi fokus. Verifikasi bahwa sesi istirahat pendek berikutnya menggunakan durasi yang baru saja Anda atur.

### Langkah 2.2: Tulis Unit Test untuk Logika Timer di `PomodoroService`

**Tujuan:** Memastikan logika inti timer (countdown, transisi sesi awal) dan penanganan command di `PomodoroService` berfungsi dengan benar secara terisolasi.

**File yang Dibuat/Dimodifikasi:**
*   Buat file test baru di `app/src/test/java/com/neimasilk/purefocus/service/PomodoroServiceTest.kt`

**Detail Langkah:**

1.  **Setup Test Environment:**
    *   Gunakan `Robolectric` jika Service memiliki dependensi Android Context (misalnya untuk `PreferencesManager` atau `LocalBroadcastManager`). Jika tidak, tes JVM standar mungkin cukup.
    *   Mock dependensi seperti `PreferencesManager` menggunakan Mockito atau framework mocking lainnya.

2.  **Tes untuk Countdown:**
    *   Buat tes yang memverifikasi bahwa setelah timer dimulai, `timeLeftInMillis` di `serviceState` (atau mekanisme observasi state lainnya) berkurang dengan benar setiap detiknya.
    *   Gunakan `TestCoroutineDispatcher` atau `runTest` dari `kotlinx-coroutines-test` untuk mengontrol waktu dalam coroutine.

3.  **Tes untuk Transisi Sesi (Awal):**
    *   Konfigurasi `PreferencesManager` mock untuk mengembalikan durasi fokus yang singkat (misalnya 1 detik).
    *   Mulai timer untuk sesi fokus.
    *   Maju-kan waktu virtual hingga timer selesai.
    *   Verifikasi bahwa `currentSessionType` di `serviceState` berubah dari `FOCUS` ke `SHORT_BREAK` (atau sesuai logika transisi awal Anda).
    *   Verifikasi bahwa `timeLeftInMillis` direset ke durasi istirahat pendek yang sesuai (dari `PreferencesManager` mock).

4.  **Tes untuk Penanganan Command:**
    *   **Start:** Verifikasi bahwa memanggil `onStartCommand` dengan `ACTION_START_SERVICE` (atau action start Anda) memulai countdown.
    *   **Pause:** Verifikasi bahwa memanggil `onStartCommand` dengan `ACTION_PAUSE_TIMER` menghentikan countdown dan `isRunning` menjadi false.
    *   **Reset:** Verifikasi bahwa memanggil `onStartCommand` dengan `ACTION_RESET_TIMER` menghentikan timer, mereset `timeLeftInMillis` ke durasi sesi saat ini, dan `isRunning` menjadi false.
    *   **Skip:** Verifikasi bahwa memanggil `onStartCommand` dengan `ACTION_SKIP_SESSION` memicu transisi ke sesi berikutnya.

5.  **Jalankan Tes:** Pastikan semua unit test yang ditulis lolos.

---

## Tahap 3: Menyelesaikan TODOs Kritis Sebelum Rilis

### Langkah 3.1: Amankan State Teks `FocusWriteScreen` dari Perubahan Konfigurasi

**Tujuan:** Memastikan teks yang sedang ditulis pengguna di `FocusWriteScreen` tidak hilang saat terjadi perubahan konfigurasi (misalnya rotasi layar).

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/ui/screens/FocusWriteScreen.kt` (atau file implementasi `FocusWriteScreenImpl` jika terpisah)
*   `app/src/main/java/com/neimasilk/purefocus/MainActivity.kt`
*   `app/src/main/java/com/neimasilk/purefocus/MainViewModel.kt`

**Detail Langkah:**

1.  **Analisis:** Periksa bagaimana state teks (`textFieldValueState` atau `textState`) saat ini dikelola. Jika state di-hoist ke `MainViewModel` dan `MainViewModel` sudah menggunakan `SavedStateHandle` atau mekanisme persistensi lainnya untuk teks tersebut, maka langkah ini mungkin sudah sebagian besar teratasi.
2.  **Jika State Lokal di `FocusWriteScreen`:** Jika `FocusWriteScreen` memiliki state teks lokal yang dikelola dengan `remember { mutableStateOf(...) }`, ubah menjadi `rememberSaveable { mutableStateOf(...) }`.
    ```kotlin
    // Di FocusWriteScreen.kt, jika ada state teks lokal
    // var textState by remember { mutableStateOf("") } // GANTI INI
    var textState by rememberSaveable { mutableStateOf("") } // MENJADI INI
    ```
3.  **Jika State di-Hoist ke `MainViewModel` (Skenario Umum):**
    *   Pastikan `MainViewModel` menyimpan dan memulihkan `focusWriteText` (atau nama variabel yang sesuai) menggunakan `SavedStateHandle`.
        ```kotlin
        // Di MainViewModel.kt
        class MainViewModel(private val savedStateHandle: SavedStateHandle, /* ... */) : ViewModel() {
            val focusWriteText: StateFlow<String> = savedStateHandle.getStateFlow("focusWriteText", "")
            
            fun updateFocusWriteText(newText: String) {
                savedStateHandle["focusWriteText"] = newText
            }
            // ...
        }
        ```
    *   Pastikan `MainActivity` meneruskan `focusWriteText` dari `MainViewModel` ke `FocusWriteScreen` dan juga callback untuk mengupdatenya.

4.  **Tes:** Jalankan aplikasi. Buka Focus Write Mode, ketik beberapa teks. Putar layar (jika menggunakan emulator, ada tombol rotasi). Verifikasi bahwa teks yang Anda ketik tetap ada dan tidak hilang. Uji juga dengan menekan tombol home lalu kembali ke aplikasi.

### Langkah 3.2: Amankan State Input Field di `SettingsScreen` dari Perubahan Konfigurasi

**Tujuan:** Memastikan input pengguna di field-field pada `SettingsScreen` (misalnya durasi fokus, durasi istirahat) tidak hilang saat terjadi perubahan konfigurasi sebelum disimpan.

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/ui/screens/SettingsScreen.kt`

**Detail Langkah:**

1.  **Identifikasi State Input:** Di `SettingsScreen.kt`, cari semua `mutableStateOf` yang digunakan untuk menampung nilai input dari `TextField` (misalnya `focusDurationInput`, `shortBreakDurationInput`, dll.).
2.  **Gunakan `rememberSaveable`:** Ganti setiap `remember { mutableStateOf(...) }` dengan `rememberSaveable { mutableStateOf(...) }` untuk state input tersebut.
    ```kotlin
    // Di SettingsScreen.kt
    // var focusDurationInput by remember { mutableStateOf(settingsViewModel.currentFocusDuration.value.toString()) } // GANTI INI
    var focusDurationInput by rememberSaveable { mutableStateOf(settingsViewModel.currentFocusDuration.value.toString()) } // MENJADI INI
    
    // Lakukan hal yang sama untuk shortBreakDurationInput, longBreakDurationInput, dll.
    ```
    *   **PENTING:** Pastikan nilai awal untuk `rememberSaveable` diambil dari `ViewModel` atau `PreferencesManager` agar saat layar pertama kali dibuat atau dibuat ulang setelah proses mati, nilai yang benar ditampilkan. Jika `rememberSaveable` digunakan untuk state yang juga di-observe dari ViewModel, pastikan ada mekanisme untuk menginisialisasi `rememberSaveable` dengan nilai dari ViewModel saat komposisi awal dan saat nilai ViewModel berubah (misalnya menggunakan `LaunchedEffect` untuk sinkronisasi jika diperlukan, atau cukup inisialisasi dengan nilai ViewModel saat deklarasi).

3.  **Tes:** Buka layar Pengaturan. Ubah beberapa nilai di input field TAPI JANGAN TEKAN SIMPAN. Putar layar. Verifikasi bahwa nilai yang Anda ketik di input field tetap ada dan tidak kembali ke nilai yang tersimpan sebelumnya.

### Langkah 3.3: Implementasi Opsi Dasar Kontrol Suara Notifikasi

**Tujuan:** Memberikan pengguna opsi untuk mengaktifkan atau menonaktifkan suara notifikasi aplikasi.

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/data/PreferencesManager.kt` (dan file proto jika menggunakan Proto DataStore)
*   `app/src/main/java/com/neimasilk/purefocus/ui/SettingsViewModel.kt`
*   `app/src/main/java/com/neimasilk/purefocus/ui/screens/SettingsScreen.kt`
*   `app/src/main/java/com/neimasilk/purefocus/util/NotificationHelper.kt` (atau di `PomodoroService.kt` jika logika notifikasi ada di sana)

**Detail Langkah:**

1.  **[TODO-RILIS-3.1] Tambahkan Preferensi Pengaturan Suara:**
    *   **Di `PreferencesManager.kt`:**
        *   Tambahkan konstanta untuk key preferensi suara notifikasi (misalnya `KEY_ENABLE_SOUND_NOTIFICATIONS`).
        *   Tambahkan fungsi untuk menyimpan (`saveEnableSoundNotifications(enable: Boolean)`) dan mengambil (`getEnableSoundNotifications(): Flow<Boolean>`) preferensi ini. Default ke `true`.
        *   Jika menggunakan Proto DataStore, update file `.proto` dengan field boolean baru.

2.  **[TODO-RILIS-3.2] Tambahkan UI Switch di `SettingsScreen`:**
    *   **Di `SettingsViewModel.kt`:**
        *   Tambahkan `StateFlow` untuk `enableSoundNotifications` yang di-collect dari `PreferencesManager`.
        *   Tambahkan fungsi untuk mengupdate preferensi `enableSoundNotifications` melalui `PreferencesManager` (misalnya `toggleSoundNotifications()`).
    *   **Di `SettingsScreen.kt`:**
        *   Tambahkan `Row` yang berisi `Text` ("Aktifkan Suara Notifikasi") dan `Switch` Composable.
        *   State `checked` pada `Switch` diikat ke `enableSoundNotifications` dari `SettingsViewModel`.
        *   `onCheckedChange` pada `Switch` memanggil fungsi `toggleSoundNotifications()` di ViewModel.

3.  **[TODO-RILIS-3.3] Kondisikan Pemutaran Suara Notifikasi:**
    *   **Di `NotificationHelper.kt` (atau di mana notifikasi dibuat, misal `PomodoroService`):**
        *   Sebelum membangun notifikasi (khususnya sebelum memanggil `setSound()` pada `NotificationCompat.Builder` atau memainkan suara secara manual), inject atau dapatkan instance `PreferencesManager`.
        *   Baca nilai `enableSoundNotifications` dari `PreferencesManager` (sebaiknya di-collect sebagai `Flow` dan diambil nilai terakhirnya, atau dibaca secara sinkron jika konteks memungkinkan dan tidak memblokir UI thread).
        *   Hanya panggil `builder.setSound(soundUri)` atau mainkan suara jika preferensi tersebut `true`.
            ```kotlin
            // Contoh di NotificationHelper atau PomodoroService
            // Asumsikan Anda memiliki akses ke scope coroutine dan preferencesManager
            val playSound = preferencesManager.getEnableSoundNotifications().first() // Ambil nilai terbaru

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                // ... pengaturan notifikasi lainnya ...
            
            if (playSound) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                notificationBuilder.setSound(soundUri)
            }
            // ... bangun dan tampilkan notifikasi
            ```

4.  **Tes:** 
    *   Buka Pengaturan, nonaktifkan suara notifikasi. Mulai sesi Pomodoro. Saat sesi berakhir (fokus atau istirahat), verifikasi bahwa notifikasi muncul TAPI TIDAK ADA SUARA.
    *   Kembali ke Pengaturan, aktifkan suara notifikasi. Mulai sesi Pomodoro lagi. Saat sesi berakhir, verifikasi bahwa notifikasi muncul DAN ADA SUARA.

---

## Tahap 4: Implementasi Siklus Istirahat Panjang (Long Break)

### Langkah 4.1: Implementasi Penuh Siklus Istirahat Panjang

**Tujuan:** Mengimplementasikan fungsionalitas istirahat panjang setelah sejumlah sesi fokus tertentu selesai.

**File yang Dimodifikasi:**
*   `app/src/main/java/com/neimasilk/purefocus/data/PreferencesManager.kt`
*   `app/src/main/java/com/neimasilk/purefocus/service/PomodoroService.kt`
*   `app/src/main/java/com/neimasilk/purefocus/ui/SettingsViewModel.kt`
*   `app/src/main/java/com/neimasilk/purefocus/ui/screens/SettingsScreen.kt`
*   `app/src/main/java/com/neimasilk/purefocus/PomodoroTimerViewModel.kt` (untuk menampilkan jenis sesi yang benar)

**Detail Langkah:**

1.  **Update `PreferencesManager`:**
    *   Tambahkan key dan fungsi untuk menyimpan/mengambil durasi istirahat panjang (misalnya `KEY_LONG_BREAK_DURATION`, `saveLongBreakDuration`, `getLongBreakDuration`).
    *   Tambahkan key dan fungsi untuk menyimpan/mengambil interval istirahat panjang (jumlah sesi fokus sebelum istirahat panjang, misalnya `KEY_POMODOROS_PER_CYCLE`, `savePomodorosPerCycle`, `getPomodorosPerCycle`). Default ke 4.

2.  **Update `SettingsScreen` dan `SettingsViewModel`:**
    *   Tambahkan input field dan logika penyimpanan di `SettingsScreen` dan `SettingsViewModel` untuk durasi istirahat panjang dan interval istirahat panjang, mirip dengan yang sudah ada untuk durasi fokus dan istirahat pendek.

3.  **Modifikasi Logika di `PomodoroService`:**
    *   Tambahkan state internal di `PomodoroService` untuk melacak jumlah sesi fokus yang telah selesai dalam satu siklus (misalnya `pomodorosCompletedInCycle: Int`). Simpan state ini agar persisten antar restart service jika memungkinkan (misalnya via `PreferencesManager` atau `SavedStateHandle` jika Service bisa menggunakannya, atau minimal simpan di memory service).
    *   Dalam logika `handleSessionFinish()` (atau yang serupa):
        *   Jika sesi yang baru selesai adalah `FOCUS`:
            *   Increment `pomodorosCompletedInCycle`.
            *   Cek apakah `pomodorosCompletedInCycle` >= nilai `POMODOROS_PER_CYCLE` dari `PreferencesManager`.
                *   Jika ya: Mulai sesi `LONG_BREAK`. Reset `pomodorosCompletedInCycle` ke 0.
                *   Jika tidak: Mulai sesi `SHORT_BREAK`.
        *   Jika sesi yang baru selesai adalah `SHORT_BREAK` atau `LONG_BREAK`: Mulai sesi `FOCUS` berikutnya.
    *   Pastikan `PomodoroService` membaca durasi istirahat panjang yang benar dari `PreferencesManager` saat memulai sesi `LONG_BREAK`.
    *   Update `_serviceState` (dan kirim broadcast) dengan `currentSessionType` yang benar (`LONG_BREAK`).

4.  **Update `PomodoroTimerViewModel` dan UI:**
    *   Pastikan `PomodoroTimerViewModel` dapat menerima dan menampilkan `SessionType.LONG_BREAK` dengan benar di UI (misalnya teks "Istirahat Panjang").

5.  **Tes:** 
    *   Atur interval istirahat panjang ke 1 atau 2 di Pengaturan untuk memudahkan testing.
    *   Atur durasi fokus, istirahat pendek, dan istirahat panjang ke nilai yang singkat (misalnya beberapa detik).
    *   Jalankan beberapa siklus Pomodoro. Verifikasi bahwa setelah jumlah sesi fokus yang ditentukan, aplikasi beralih ke sesi istirahat panjang dengan durasi yang benar. Verifikasi bahwa setelah istirahat panjang, siklus kembali ke sesi fokus dan hitungan `pomodorosCompletedInCycle` direset.

---

*Dokumen ini akan terus diperbarui seiring dengan perkembangan proyek.*