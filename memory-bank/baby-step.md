# PureFocus - Baby Step Implementation Guide

**Document Version:** 1.0
**Date:** 30 Mei 2025
**Baby-Step Name:** 2.1 - Implementasi Timer Dasar
**Estimated Duration:** 2-3 hari

## Baby-Step Overview

**Objective:** Mengimplementasikan logika inti dan manajemen state untuk timer Pomodoro di dalam `PomodoroTimerViewModel`. Fokus pada fungsionalitas dasar timer (start, pause, reset, skip), countdown, dan transisi dari sesi kerja ke istirahat pendek. Durasi sesi akan di-hardcode untuk saat ini. Interaksi dengan UI belum menjadi fokus.

**Context:** Fase 1 (Editor Teks Inti) telah selesai 50%, dengan editor teks dasar dan penyimpanan otomatis berfungsi. Semua tes unit yang ada telah lolos. Proyek sekarang memasuki Fase 2, dimulai dengan implementasi logika timer Pomodoro.

**Success Criteria:**
* `PomodoroTimerViewModel` berhasil dibuat dan mengelola state timer Pomodoro.
* Fungsi `startTimer()`, `pauseTimer()`, `resetTimer()`, dan `skipSession()` bekerja sesuai harapan.
* Logika countdown mengurangi `timeLeftInMillis` setiap detik saat timer berjalan.
* Timer secara otomatis bertransisi dari sesi `WORK` ke `SHORT_BREAK` setelah waktu sesi `WORK` habis.
* `pomodorosCompletedInCycle` bertambah satu setelah sesi `WORK` selesai.
* Unit test untuk `PomodoroTimerViewModel` mencakup fungsionalitas inti dan semua skenario transisi dasar, dan semua tes lolos.
* Tidak ada *crash* atau dampak negatif pada fungsionalitas editor teks yang sudah ada.

## Detailed Implementation Tasks

### Task 1: Definisikan State dan Enum Timer
**Description:** Membuat data class `PomodoroState` untuk menampung semua informasi state timer dan Enum `SessionType` untuk jenis sesi.
**Purpose:** Menyediakan struktur data yang jelas untuk merepresentasikan kondisi timer.
**Files to Create/Modify:**
* `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModel.kt` (Buat baru atau modifikasi jika sudah ada kerangka)
    * Di dalam file ini atau sebagai file terpisah jika preferensi (misal `app/src/main/java/com/neimasilk/purefocus/model/TimerModels.kt`):
        * `PomodoroState.kt`
        * `SessionType.kt`

**Implementation Steps:**
1.  Buat Enum `SessionType`:
    ```kotlin
    package com.neimasilk.purefocus.model // atau package yang sesuai

    enum class SessionType {
        WORK,
        SHORT_BREAK,
        LONG_BREAK // Meskipun logika penuh belum, enum bisa ada
    }
    ```
2.  Buat data class `PomodoroState`:
    ```kotlin
    package com.neimasilk.purefocus.model // atau package yang sesuai

    data class PomodoroState(
        val timeLeftInMillis: Long = WORK_DURATION_MILLIS, // Durasi awal sesi kerja
        val currentSessionType: SessionType = SessionType.WORK,
        val isTimerRunning: Boolean = false,
        val pomodorosCompletedInCycle: Int = 0 // Jumlah pomodoro (sesi kerja) selesai dalam siklus saat ini
    ) {
        companion object {
            // Durasi default di-hardcode untuk baby-step ini
            const val WORK_DURATION_MINUTES = 25L
            const val SHORT_BREAK_DURATION_MINUTES = 5L
            const val LONG_BREAK_DURATION_MINUTES = 15L // Untuk masa depan

            const val WORK_DURATION_MILLIS = WORK_DURATION_MINUTES * 60 * 1000
            const val SHORT_BREAK_DURATION_MILLIS = SHORT_BREAK_DURATION_MINUTES * 60 * 1000
            const val LONG_BREAK_DURATION_MILLIS = LONG_BREAK_DURATION_MINUTES * 60 * 1000
        }
    }
    ```
    *(Catatan: `WORK_DURATION_MILLIS` dll. akan dipindahkan ke companion object di `PomodoroTimerViewModel` pada Task 2 untuk sentralisasi konstanta terkait ViewModel)*

**Acceptance Criteria:**
* Enum `SessionType` dan data class `PomodoroState` terdefinisi dengan benar sesuai spesifikasi.
* Nilai default untuk `PomodoroState` mencerminkan kondisi awal timer (sesi kerja, waktu penuh, dijeda).

**Validation Method:**
* Review kode untuk memastikan definisi sesuai.

### Task 2: Implementasi `PomodoroTimerViewModel`
**Description:** Membuat kelas `PomodoroTimerViewModel` yang akan berisi logika utama timer.
**Purpose:** Mengelola state dan transisi timer Pomodoro.
**Files to Create/Modify:**
* `app/src/main/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModel.kt` (Baru)

**Implementation Steps:**
1.  Buat kelas `PomodoroTimerViewModel` yang mewarisi `ViewModel`.
2.  Definisikan konstanta untuk durasi sesi di dalam `companion object` ViewModel (pindahkan dari `PomodoroState.Companion` jika dibuat di sana sebelumnya):
    ```kotlin
    companion object {
        const val WORK_DURATION_MINUTES = 25L
        const val SHORT_BREAK_DURATION_MINUTES = 5L
        const val LONG_BREAK_DURATION_MINUTES = 15L

        val WORK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(WORK_DURATION_MINUTES)
        val SHORT_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(SHORT_BREAK_DURATION_MINUTES)
        val LONG_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(LONG_BREAK_DURATION_MINUTES)
        // Pertimbangkan juga untuk menambahkan konstanta jumlah pomodoro sebelum istirahat panjang, misal:
        // const val POMODOROS_PER_CYCLE = 4
    }
    ```
3.  Inisialisasi `MutableStateFlow<PomodoroState>`:
    ```kotlin
    private val _uiState = MutableStateFlow(PomodoroState(timeLeftInMillis = WORK_DURATION_MILLIS))
    val uiState: StateFlow<PomodoroState> = _uiState.asStateFlow()
    ```
4.  Deklarasikan `private var timerJob: Job? = null`.
5.  Implementasikan fungsi `startTimer()`:
    * Jika timer sudah berjalan, jangan lakukan apa-apa.
    * Set `_uiState.update { it.copy(isTimerRunning = true) }`.
    * Buat `timerJob` baru:
        ```kotlin
        timerJob = viewModelScope.launch {
            var currentTime = _uiState.value.timeLeftInMillis
            while (currentTime > 0 && isActive) { // isActive untuk memastikan coroutine masih aktif
                delay(1000L) // Tunggu 1 detik
                currentTime -= 1000L
                _uiState.update { it.copy(timeLeftInMillis = currentTime) }
            }
            // Setelah loop selesai (waktu habis)
            if (isActive) { // Cek lagi karena coroutine bisa dibatalkan
                handleSessionFinish()
            }
        }
        ```
6.  Implementasikan fungsi `pauseTimer()`:
    * Batalkan `timerJob` (`timerJob?.cancel()`).
    * Set `_uiState.update { it.copy(isTimerRunning = false) }`.
7.  Implementasikan fungsi `private fun handleSessionFinish()`:
    * Panggil `pauseTimer()` untuk menghentikan job dan set `isTimerRunning = false`.
    * Logika transisi:
        * Jika `_uiState.value.currentSessionType == SessionType.WORK`:
            * `val newPomodorosCompleted = _uiState.value.pomodorosCompletedInCycle + 1`
            * `_uiState.update { it.copy(currentSessionType = SessionType.SHORT_BREAK, timeLeftInMillis = SHORT_BREAK_DURATION_MILLIS, pomodorosCompletedInCycle = newPomodorosCompleted) }`
            * *(TODO untuk baby-step selanjutnya: Tambahkan logika untuk `LONG_BREAK` setelah `POMODOROS_PER_CYCLE`)*
        * Jika `_uiState.value.currentSessionType == SessionType.SHORT_BREAK` (atau `LONG_BREAK` nantinya):
            * `_uiState.update { it.copy(currentSessionType = SessionType.WORK, timeLeftInMillis = WORK_DURATION_MILLIS) }`
    * Pertimbangkan untuk otomatis memulai timer sesi berikutnya atau biarkan pengguna yang memulai (Untuk baby-step ini, biarkan dijeda setelah transisi. Auto-start bisa jadi fitur tambahan). Kita pilih untuk **tidak auto-start** sesi berikutnya.
8.  Implementasikan fungsi `resetTimer()`:
    * Panggil `pauseTimer()`.
    * Reset state ke sesi kerja awal: `_uiState.update { PomodoroState(timeLeftInMillis = WORK_DURATION_MILLIS, currentSessionType = SessionType.WORK, pomodorosCompletedInCycle = it.pomodorosCompletedInCycle) }`.  Perhatikan `pomodorosCompletedInCycle` mungkin perlu dipertimbangkan apakah direset juga atau tidak tergantung definisi "reset". Untuk sekarang, reset timer hanya mereset sesi saat ini, bukan seluruh siklus. Jika ingin mereset siklus, `pomodorosCompletedInCycle` juga direset ke 0. Kita pilih **mereset hanya sesi saat ini**, `pomodorosCompletedInCycle` tetap.
9.  Implementasikan fungsi `skipSession()`:
    * Panggil `pauseTimer()`.
    * Langsung panggil `handleSessionFinish()` untuk memicu logika transisi ke sesi berikutnya.
    * Pastikan `timeLeftInMillis` di state sudah sesuai dengan durasi sesi baru tersebut.

**Acceptance Criteria:**
* ViewModel memiliki `StateFlow<PomodoroState>` yang bisa diobservasi.
* `startTimer()` memulai countdown dan mengubah `isTimerRunning` menjadi `true`.
* `pauseTimer()` menghentikan countdown dan mengubah `isTimerRunning` menjadi `false`.
* `resetTimer()` mengembalikan timer ke durasi awal sesi `WORK` dan kondisi `isTimerRunning = false`.
* `skipSession()` memindahkan timer ke sesi berikutnya dalam keadaan `isTimerRunning = false`.
* Setelah sesi `WORK` selesai, state otomatis bertransisi ke `SHORT_BREAK` dengan durasi yang benar, dan `pomodorosCompletedInCycle` bertambah. `isTimerRunning` menjadi `false`.

**Validation Method:**
* Unit testing (Task 4).
* Logging internal di ViewModel untuk memantau perubahan state selama pengembangan manual.

### Task 3: Inisialisasi `PomodoroTimerViewModel` di `MainActivity`
**Description:** Memastikan `PomodoroTimerViewModel` diinisialisasi dengan benar saat aplikasi berjalan.
**Purpose:** Membuat ViewModel tersedia untuk digunakan oleh UI di baby-step selanjutnya.
**Files to Create/Modify:**
* `app/src/main/java/com/neimasilk/purefocus/MainActivity.kt`

**Implementation Steps:**
1.  Tambahkan deklarasi properti untuk `PomodoroTimerViewModel`:
    ```kotlin
    private lateinit var pomodoroViewModel: PomodoroTimerViewModel
    ```
2.  Di dalam `onCreate`, setelah inisialisasi `MainViewModel`, inisialisasikan `PomodoroTimerViewModel`:
    ```kotlin
    pomodoroViewModel = ViewModelProvider(this).get(PomodoroTimerViewModel::class.java)
    // Atau jika butuh factory:
    // val pomodoroFactory = viewModelFactory { initializer { PomodoroTimerViewModel(/* dependencies if any */) } }
    // pomodoroViewModel = ViewModelProvider(this, pomodoroFactory)[PomodoroTimerViewModel::class.java]
    // Untuk saat ini, PomodoroTimerViewModel tidak punya dependensi.
    ```
    *(Catatan: Untuk saat ini, kita tidak akan melakukan apa pun dengan `pomodoroViewModel.uiState` di `MainActivity`. Ini hanya untuk memastikan inisialisasi.)*

**Acceptance Criteria:**
* Aplikasi berjalan tanpa *crash* setelah penambahan inisialisasi `PomodoroTimerViewModel`.
* Instance `PomodoroTimerViewModel` berhasil dibuat.

**Validation Method:**
* Jalankan aplikasi pada emulator/perangkat.
* Gunakan debugger untuk memastikan `pomodoroViewModel` terinisialisasi.

### Task 4: Unit Testing untuk `PomodoroTimerViewModel`
**Description:** Membuat unit test yang komprehensif untuk memverifikasi semua fungsionalitas inti `PomodoroTimerViewModel`.
**Purpose:** Memastikan keandalan logika timer.
**Files to Create/Modify:**
* `app/src/test/java/com/neimasilk/purefocus/ui/PomodoroTimerViewModelTests.kt` (Baru)

**Implementation Steps:**
1.  Setup `TestCoroutineRule` atau gunakan `StandardTestDispatcher` seperti pada `MainViewModelTests.kt`.
    ```kotlin
    @ExperimentalCoroutinesApi
    class PomodoroTimerViewModelTests {

        private val testDispatcher = StandardTestDispatcher()
        private lateinit var viewModel: PomodoroTimerViewModel

        @Before
        fun setup() {
            Dispatchers.setMain(testDispatcher)
            viewModel = PomodoroTimerViewModel()
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        // ... test cases ...
    }
    ```
2.  **Test Case: Initial State**
    * Verifikasi `uiState.value` awal: `timeLeftInMillis` sesuai `WORK_DURATION_MILLIS`, `currentSessionType` adalah `WORK`, `isTimerRunning` adalah `false`, `pomodorosCompletedInCycle` adalah `0`.
3.  **Test Case: Start Timer**
    * Panggil `startTimer()`.
    * Verifikasi `isTimerRunning` menjadi `true`.
    * Gunakan `testDispatcher.scheduler.advanceTimeBy(1000L); testDispatcher.scheduler.runCurrent()`
    * Verifikasi `timeLeftInMillis` berkurang 1000ms.
    * Ulangi `advanceTimeBy` beberapa kali untuk memastikan countdown berjalan.
4.  **Test Case: Pause Timer**
    * Panggil `startTimer()`, `advanceTimeBy(5000L)`.
    * Panggil `pauseTimer()`.
    * Verifikasi `isTimerRunning` menjadi `false`.
    * Simpan `timeLeftInMillis` saat ini.
    * `advanceTimeBy(2000L)`.
    * Verifikasi `timeLeftInMillis` tidak berubah (tetap sama seperti saat dijeda).
5.  **Test Case: Resume Timer (Start after Pause)**
    * Panggil `startTimer()`, `advanceTimeBy(3000L)`, `pauseTimer()`.
    * Simpan `timeLeftInMillis` (misal `T_paused`).
    * Panggil `startTimer()` lagi (untuk resume).
    * Verifikasi `isTimerRunning` menjadi `true`.
    * `advanceTimeBy(2000L)`.
    * Verifikasi `timeLeftInMillis` adalah `T_paused - 2000L`.
6.  **Test Case: Reset Timer (saat berjalan dan saat dijeda)**
    * (Saat berjalan): `startTimer()`, `advanceTimeBy(5000L)`. Panggil `resetTimer()`. Verifikasi state kembali ke initial WORK state, `isTimerRunning` false.
    * (Saat dijeda): `startTimer()`, `advanceTimeBy(5000L)`, `pauseTimer()`. Panggil `resetTimer()`. Verifikasi state kembali ke initial WORK state, `isTimerRunning` false.
7.  **Test Case: Work Session Finishes -> Transisi ke Short Break**
    * Panggil `startTimer()`.
    * `advanceTimeBy(WORK_DURATION_MILLIS); testDispatcher.scheduler.runCurrent()`.
    * Verifikasi `currentSessionType` menjadi `SHORT_BREAK`.
    * Verifikasi `timeLeftInMillis` menjadi `SHORT_BREAK_DURATION_MILLIS`.
    * Verifikasi `isTimerRunning` menjadi `false`.
    * Verifikasi `pomodorosCompletedInCycle` menjadi `1`.
8.  **Test Case: Skip Session (dari Work ke Short Break)**
    * Pastikan state awal adalah Work.
    * Panggil `skipSession()`.
    * Verifikasi `currentSessionType` menjadi `SHORT_BREAK`.
    * Verifikasi `timeLeftInMillis` menjadi `SHORT_BREAK_DURATION_MILLIS`.
    * Verifikasi `isTimerRunning` menjadi `false`.
    * Verifikasi `pomodorosCompletedInCycle` menjadi `1` (karena sesi kerja dianggap selesai/dilewati).
9.  **Test Case: Skip Session (dari Short Break ke Work)**
    * Set state manual atau jalankan timer hingga masuk Short Break.
    * Panggil `skipSession()`.
    * Verifikasi `currentSessionType` menjadi `WORK`.
    * Verifikasi `timeLeftInMillis` menjadi `WORK_DURATION_MILLIS`.
    * Verifikasi `isTimerRunning` menjadi `false`.
    * Verifikasi `pomodorosCompletedInCycle` tetap (tidak bertambah saat skip dari break).

**Acceptance Criteria:**
* Semua skenario tes di atas terimplementasi.
* Semua tes berhasil (`./gradlew testDebugUnitTest` PASS).
* Tes mencakup kondisi batas dan transisi state yang krusial.

**Validation Method:**
* Eksekusi suite unit test.

## Testing and Validation (Baby-Step Keseluruhan)

### Unit Tests Required
* Semua tes yang didefinisikan dalam Task 4.

### Manual Testing Steps
1.  (Opsional, lebih untuk debugging awal) Tambahkan logging sementara di `PomodoroTimerViewModel` untuk memantau perubahan `_uiState.value`.
2.  Jalankan aplikasi di emulator/perangkat.
3.  Gunakan Debugger di Android Studio untuk memanggil fungsi `startTimer()`, `pauseTimer()`, `resetTimer()`, `skipSession()` secara manual pada instance `pomodoroViewModel` (misalnya melalui "Evaluate Expression") dan amati perubahan `_uiState.value` di Variables panel.
    * Panggil `startTimer()`, amati `timeLeftInMillis` berkurang dan `isTimerRunning` true.
    * Panggil `pauseTimer()`, amati `timeLeftInMillis` berhenti dan `isTimerRunning` false.
    * Panggil `resetTimer()`, amati state kembali ke awal.
    * Panggil `skipSession()`, amati transisi sesi.

### Performance Validation
* Untuk baby-step ini, dampak performa seharusnya minimal karena belum ada integrasi UI. Pastikan aplikasi tetap berjalan lancar dan tidak ada peningkatan signifikan pada penggunaan memori atau CPU yang tidak wajar saat ViewModel diinisialisasi. Metrik utama akan divalidasi lebih lanjut saat integrasi UI.

## Dependencies and Prerequisites

**Required Before Starting:**
* Fase 1 (Editor Teks Inti) 50% selesai, artinya `MainActivity.kt` dan struktur dasar proyek sudah ada.
* Unit testing environment sudah terkonfigurasi dan berfungsi (seperti yang digunakan untuk `MainViewModelTests.kt`).

**External Dependencies:**
* `kotlinx-coroutines-core` (sudah ada sebagai bagian dari dependensi Android).
* `androidx.lifecycle.viewModelScope` (sudah ada).

## Risk Mitigation

**Potential Issues:**
* **Logika Countdown Kompleks/Salah:** Jika `delay` dan `Job.cancel` tidak dikelola dengan benar, bisa menyebabkan timer tidak akurat atau *resource leak*. Solusi: Gunakan `isActive` di dalam `while` loop coroutine, pastikan `job?.cancelAndJoin()` atau `job?.cancel()` dipanggil dengan benar. Unit test yang ketat akan membantu.
* **Kesalahan Transisi State:** Logika `handleSessionFinish` bisa menjadi kompleks. Solusi: Pecah menjadi fungsi-fungsi kecil jika perlu, dan uji setiap jalur transisi secara menyeluruh dengan unit test.

**Performance Risks:**
* Meskipun belum ada UI, pastikan coroutine timer tidak memblokir thread utama secara tidak sengaja. Penggunaan `viewModelScope.launch` dengan `Dispatchers.Main` (default untuk `viewModelScope`) atau `Dispatchers.Default` jika ada komputasi berat (tidak ada di sini) harus benar. Untuk `delay`, `Dispatchers.Main` aman.

## Completion Checklist

- [ ] Task 1: `PomodoroState` dan `SessionType` terdefinisi.
- [ ] Task 2: `PomodoroTimerViewModel` dengan logika inti (start, pause, reset, skip, countdown, transisi Work->ShortBreak) terimplementasi.
- [ ] Task 3: `PomodoroTimerViewModel` berhasil diinisialisasi di `MainActivity`.
- [ ] Task 4: Unit test untuk `PomodoroTimerViewModel` dibuat dan semua tes PASS.
- [ ] (Manual) Debugging awal dengan memanggil fungsi ViewModel melalui debugger berhasil memverifikasi perilaku dasar.
- [ ] Kode telah direview untuk kejelasan dan potensi masalah.
- [ ] Dokumentasi (jika ada komentar kode yang signifikan) telah diperbarui.
- [ ] Perubahan telah di-commit ke Git dengan pesan yang jelas.
- [ ] Siap untuk baby-step berikutnya (Integrasi UI Timer).

---
**Catatan untuk Developer (Anda):**
* File ini berisi panduan yang sangat detail. Ikuti langkah-langkahnya dengan cermat.
* Fokus pada implementasi logika di ViewModel terlebih dahulu.
* Pastikan semua unit test lolos sebelum menganggap baby-step ini selesai.
* Setelah selesai, update `progress.md` dan `architecture.md` (jika ada perubahan arsitektural, meskipun untuk baby-step ini kemungkinannya kecil selain penambahan ViewModel baru). Hapus atau arsipkan file `baby-step.md` ini.