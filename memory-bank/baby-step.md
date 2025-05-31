# Baby Steps Pengembangan PureFocus

Dokumen ini berisi panduan langkah-demi-langkah (baby steps) untuk pengembangan fitur aplikasi PureFocus. Setiap langkah dirancang agar sekecil mungkin untuk memastikan progres yang jelas dan meminimalkan risiko.

**PERINGATAN KERAS:** JANGAN PERNAH MELANJUTKAN KE BABY-STEP BERIKUTNYA SEBELUM SEMUA TES (UNIT TEST DAN INSTRUMENTED TEST) YANG BERKAITAN DENGAN BABY-STEP YANG SEDANG DIKERJAKAN BENAR-BENAR LOLOS DAN SEMUA FUNGSIONALITAS YANG DIHARAPKAN TELAH TERVERIFIKASI SECARA MANUAL. Pastikan kode bersih, terdokumentasi jika perlu, dan tidak ada *regression* (fitur lama yang rusak). Disiplin dalam pengujian adalah kunci keberhasilan proyek ini.

## Status Keseluruhan Baby Steps

✅ **Baby Step 1: Notifikasi Sederhana untuk Akhir Sesi Fokus** - SELESAI
✅ **Baby Step 2: Input Durasi Sesi Fokus di UI Pengaturan Sederhana** - SELESAI
✅ **Baby Step 3: Simpan Teks dari FocusWriteScreen ke Logcat** - SELESAI
✅ **Baby Step 4: Membuat Dasar Foreground Service** - SELESAI
🔄 **Baby Step 5: Menulis Satu Instrumented Test untuk FocusWriteScreen** - BELUM DIKERJAKAN

## Daftar Baby Steps

### Baby Step 1: Notifikasi Sederhana untuk Akhir Sesi Fokus ✅

**STATUS: SELESAI** - Sistem notifikasi telah berhasil diimplementasikan dengan:
- `NotificationHelper` untuk menampilkan notifikasi
- Event-driven notification melalui `showFocusEndNotificationEvent` di `PomodoroTimerViewModel`
- Integrasi notifikasi di `MainActivity` dengan collection dari Flow event
- Channel notifikasi dan izin runtime untuk Android 13+

---

### Baby Step 2: Input Durasi Sesi Fokus di UI Pengaturan Sederhana ✅

**STATUS: SELESAI** - UI Pengaturan telah berhasil diimplementasikan dengan:
- `SettingsScreen` dengan input untuk durasi sesi fokus
- `SettingsViewModel` untuk manajemen state pengaturan
- Navigasi antara `FocusWriteScreen` dan `SettingsScreen` di `MainActivity`
- Integrasi dengan `PreferencesManager` untuk penyimpanan pengaturan

---

### Baby Step 3: Simpan Teks dari `FocusWriteScreen` ke Logcat Saat Sesi Fokus Berakhir

**STATUS: SELESAI ✅**

**Tujuan:** Sebagai langkah awal sebelum implementasi penyimpanan permanen, log teks yang diketik pengguna di `FocusWriteScreen` ke Logcat ketika sesi Fokus Pomodoro berakhir.

**IMPLEMENTASI YANG TELAH DISELESAIKAN:**
- ✅ State `focusWriteText` ditambahkan ke `PomodoroTimerViewModel`
- ✅ Integrasi teks dari `FocusWriteScreen` ke ViewModel melalui `updateFocusWriteText()`
- ✅ Logging teks ke Logcat saat sesi fokus berakhir atau dilewati
- ✅ Reset teks otomatis setelah logging
- ✅ Unit test lengkap dengan MockK untuk mocking Android Log
- ✅ Semua test berhasil lolos (BUILD SUCCESSFUL)

**Detail Tugas:**

1.  **Modifikasi `PomodoroTimerViewModel.kt`:**
    * **Akses ke State Teks:** ViewModel ini perlu cara untuk mengetahui teks apa yang sedang diketik di `FocusWriteScreen`. Jika state teks (`textState` di `FocusWriteScreen`) dikelola secara lokal di Composable, Anda perlu mengangkatnya (hoist) ke `PomodoroTimerViewModel` atau menyediakan cara bagi `FocusWriteScreen` untuk memberitahu ViewModel tentang teks saat ini.
    * **Akses ke State Teks:** ViewModel ini perlu cara untuk mengetahui teks apa yang sedang diketik di `FocusWriteScreen`. Jika state teks (`textState` di `FocusWriteScreen`) dikelola secara lokal di Composable, Anda perlu mengangkatnya (hoist) ke `PomodoroTimerViewModel` atau menyediakan cara bagi `FocusWriteScreen` untuk memberitahu ViewModel tentang teks saat ini.
        * **Opsi 1 (Hoist State):** Pindahkan `textState` (MutableState) dari `FocusWriteScreen` ke `PomodoroTimerViewModel`. `FocusWriteScreen` akan menerima `textValue: String` dan `onTextChange: (String) -> Unit` dari ViewModel.
            ```kotlin
            // Di PomodoroTimerViewModel
            private val _focusWriteText = MutableStateFlow("")
            val focusWriteText: StateFlow<String> = _focusWriteText.asStateFlow()

            fun updateFocusWriteText(newText: String) {
                _focusWriteText.value = newText
            }
            ```
            Di `FocusWriteScreen`:
            ```kotlin
            val textState by pomodoroTimerViewModel.focusWriteText.collectAsState()
            // ...
            TextField(
                value = textState,
                onValueChange = { pomodoroTimerViewModel.updateFocusWriteText(it) },
                // ...
            )
            ```
        * **Opsi 2 (Callback - kurang ideal untuk state yang sering berubah):** Tambahkan fungsi di ViewModel yang dipanggil oleh `FocusWriteScreen` saat teks berubah. Ini kurang umum untuk state UI dibandingkan hoisting.

    * **Log Teks Saat Sesi Fokus Berakhir:**
        * Di dalam logika `PomodoroTimerViewModel` yang menangani akhir sesi Fokus (tempat Anda juga memicu notifikasi dari Baby Step 1), dapatkan nilai teks saat ini dari `_focusWriteText.value`.
        * Log teks ini ke Logcat menggunakan `Log.d("FocusWrite", "Sesi Fokus Selesai. Teks: ${_focusWriteText.value}")`.
        * **PENTING:** Pertimbangkan untuk mereset `_focusWriteText.value = ""` setelah di-log, agar sesi fokus berikutnya dimulai dengan field teks kosong, atau tentukan perilaku yang diinginkan. Untuk baby step ini, mereset bisa jadi pilihan sederhana.

2.  **Update `FocusWriteScreen.kt`:**
    * Jika Anda memilih Opsi 1 (Hoist State), sesuaikan `FocusWriteScreen` untuk menggunakan `focusWriteText` dari `PomodoroTimerViewModel` dan memanggil `updateFocusWriteText`.

**Kriteria Lolos (Verifikasi Manual & Tes):**
* [ ] Teks yang diketik di `TextField` pada `FocusWriteScreen` terkelola oleh `PomodoroTimerViewModel` (jika hoisting state dilakukan).
* [ ] Ketika sesi FOKUS berakhir (timer mencapai 0), teks yang ada di `TextField` pada saat itu tercetak di Logcat dengan tag dan pesan yang sesuai.
* [ ] Periksa apakah teks di-reset (dihapus) dari `TextField` setelah sesi fokus berakhir dan di-log (jika ini perilaku yang diimplementasikan).
* [ ] (Jika ada) Unit test untuk `PomodoroTimerViewModel` dapat memverifikasi bahwa `_focusWriteText` diupdate dan bahwa ada upaya untuk "memproses" teks tersebut saat sesi fokus berakhir (misalnya, dengan memverifikasi pemanggilan fungsi internal atau perubahan state lain yang menandakan teks telah ditangani).
* [ ] Tidak ada error atau crash.

---

### Baby Step 4: Membuat Dasar Foreground Service (Tanpa Logika Timer Komplit)

**STATUS: SELESAI ✅**

**Tujuan:** Membuat struktur dasar untuk `ForegroundService` yang akan bertanggung jawab menjaga timer tetap berjalan akurat di background. Untuk baby step ini, fokus pada setup service dan notifikasi persistennya saja, belum memindahkan logika timer.

**Detail Tugas:**

1.  **Buat Kelas `PomodoroService.kt` Baru:**
    * Lokasi: `app/src/main/java/com/neimasilk/purefocus/service/PomodoroService.kt` (buat package `service` jika belum ada).
    * Buat kelas yang mewarisi dari `android.app.Service`.
        ```kotlin
        // Di PomodoroService.kt
        class PomodoroService : Service() {

            companion object {
                const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
                const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
                private const val NOTIFICATION_ID = 2 // ID berbeda dari notifikasi sesi akhir
                private const val NOTIFICATION_CHANNEL_ID = "purefocus_foreground_service_channel"
            }

            override fun onBind(intent: Intent?): IBinder? {
                return null // Tidak menggunakan binding untuk saat ini
            }

            override fun onCreate() {
                super.onCreate()
                createNotificationChannel()
            }

            override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
                when (intent?.action) {
                    ACTION_START_SERVICE -> {
                        startForegroundService()
                    }
                    ACTION_STOP_SERVICE -> {
                        stopForegroundService()
                    }
                }
                return START_STICKY // Atau START_NOT_STICKY, tergantung kebutuhan
            }

            private fun startForegroundService() {
                val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("PureFocus Timer Aktif")
                    .setContentText("Sesi Pomodoro sedang berjalan...")
                    .setSmallIcon(R.drawable.ic_notification_pomodoro) // Gunakan ikon yang sama atau yang lain
                    .setOngoing(true) // Penting untuk notifikasi foreground service
                    .build()

                startForeground(NOTIFICATION_ID, notification)
                // TODO: Di baby step selanjutnya, pindahkan logika timer ke sini
                Log.d("PomodoroService", "Foreground service dimulai.")
            }

            private fun stopForegroundService() {
                Log.d("PomodoroService", "Foreground service dihentikan.")
                stopForeground(STOP_FOREGROUND_REMOVE) // Hapus notifikasi saat service berhenti
                stopSelf()
            }

            private fun createNotificationChannel() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val serviceChannel = NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        "PureFocus Timer Service",
                        NotificationManager.IMPORTANCE_LOW // Atau DEFAULT, tapi LOW lebih baik untuk notif persisten
                    )
                    val manager = getSystemService(NotificationManager::class.java)
                    manager?.createNotificationChannel(serviceChannel)
                }
            }

            override fun onDestroy() {
                super.onDestroy()
                Log.d("PomodoroService", "Service dihancurkan.")
            }
        }
        ```
    * Buat drawable `ic_notification_pomodoro.xml` (atau gunakan yang sudah ada) di `res/drawable`. Ini bisa berupa ikon sederhana yang merepresentasikan timer atau fokus.

2.  **Deklarasikan Service di `AndroidManifest.xml`:**
    * Tambahkan deklarasi service di dalam tag `<application>`:
        ```xml
        <service
            android:name=".service.PomodoroService"
            android:foregroundServiceType="shortService" />
        ```
    * Tambahkan izin untuk foreground service (jika belum ada dan menargetkan API level yang lebih baru):
        `<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />`
        Jika menargetkan Android 14 (API 34) atau lebih tinggi, Anda mungkin perlu mendeklarasikan tipe foreground service yang lebih spesifik jika `shortService` tidak cukup (misalnya, `specialUse` atau `mediaPlayback` jika relevan, namun `shortService` mungkin cukup untuk timer). Untuk saat ini `shortService` adalah awal yang baik.

3.  **Picu Start/Stop Service dari `PomodoroTimerViewModel.kt` (atau `MainActivity`):**
    * Ketika timer Pomodoro dimulai (misalnya, saat tombol "Start" ditekan dan state berubah menjadi `Running`), kirim Intent untuk memulai `PomodoroService`.
    * Ketika timer dihentikan secara eksplisit (misalnya, "Reset" atau semua siklus selesai dan tidak ada sesi lagi), atau aplikasi ditutup, kirim Intent untuk menghentikan `PomodoroService`.
        ```kotlin
        // Di tempat Anda memulai/menghentikan timer, misal di PomodoroTimerViewModel
        // Anda memerlukan Context untuk mengirim Intent.
        // Ini bisa jadi masalah jika ViewModel tidak boleh tahu Context.
        // Alternatif: Gunakan SharedFlow untuk mengirim event ke View (Activity/Fragment),
        // dan View yang akan memanggil startService/stopService.
        // Untuk baby step ini, anggap ada cara untuk mendapatkan Context (misal melalui Application context).

        fun startPomodoroService(context: Context) {
            val serviceIntent = Intent(context, PomodoroService::class.java).apply {
                action = PomodoroService.ACTION_START_SERVICE
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }

        fun stopPomodoroService(context: Context) {
            val serviceIntent = Intent(context, PomodoroService::class.java).apply {
                action = PomodoroService.ACTION_STOP_SERVICE
            }
            context.startService(serviceIntent) // atau stopService(serviceIntent) jika sudah berjalan
        }
        ```
    * **Strategi Pengelolaan Context di ViewModel:** Cara terbaik adalah ViewModel mengirim event ke UI (Activity/Composable), dan UI (yang memiliki akses ke Context) yang bertanggung jawab untuk memulai/menghentikan service.
        * Di `PomodoroTimerViewModel`:
            ```kotlin
            private val _serviceCommandEvent = MutableSharedFlow<ServiceAction>()
            val serviceCommandEvent: SharedFlow<ServiceAction> = _serviceCommandEvent.asSharedFlow()

            // ... saat start timer ...
            _serviceCommandEvent.emit(ServiceAction.START)
            // ... saat stop timer ...
            _serviceCommandEvent.emit(ServiceAction.STOP)

            enum class ServiceAction { START, STOP }
            ```
        * Di `MainActivity` (atau Composable yang sesuai):
            ```kotlin
            LaunchedEffect(key1 = pomodoroTimerViewModel) {
                pomodoroTimerViewModel.serviceCommandEvent.collect { action ->
                    val serviceIntent = Intent(applicationContext, PomodoroService::class.java)
                    when (action) {
                        PomodoroTimerViewModel.ServiceAction.START -> {
                            serviceIntent.action = PomodoroService.ACTION_START_SERVICE
                            ContextCompat.startForegroundService(applicationContext, serviceIntent)
                        }
                        PomodoroTimerViewModel.ServiceAction.STOP -> {
                            serviceIntent.action = PomodoroService.ACTION_STOP_SERVICE
                            startService(serviceIntent) // Untuk menghentikan, startService dengan action stop juga bisa
                        }
                    }
                }
            }
            ```

**Kriteria Lolos (Verifikasi Manual & Tes):**
* [ ] Channel notifikasi untuk foreground service berhasil dibuat.
* [ ] Saat timer Pomodoro dimulai dari UI, `PomodoroService` dimulai.
* [ ] Sebuah notifikasi persisten (foreground service notification) muncul dengan judul, teks, dan ikon yang benar.
* [ ] Notifikasi ini tidak bisa di-dismiss dengan swipe.
* [ ] Di Logcat, terlihat log "Foreground service dimulai."
* [ ] Saat timer dihentikan atau direset dari UI (atau kondisi lain yang ditentukan untuk menghentikan service), notifikasi foreground service hilang, dan di Logcat terlihat log "Foreground service dihentikan." dan "Service dihancurkan."
* [ ] Aplikasi tidak crash saat memulai atau menghentikan service.
* [ ] Periksa di Pengaturan Aplikasi -> Aplikasi Berjalan bahwa service Anda muncul saat aktif dan hilang saat dihentikan.

---

### Baby Step 5: Menulis Satu Instrumented Test untuk `FocusWriteScreen`

**STATUS: BELUM DIKERJAKAN 🔄**

**Tujuan:** Memulai pengujian UI dengan menulis satu instrumented test sederhana untuk `FocusWriteScreen` guna memverifikasi keberadaan elemen UI dasar.

**Detail Tugas:**

1.  **Pastikan Dependensi Testing UI Sudah Ada:**
    * Di `app/build.gradle.kts`, pastikan Anda memiliki dependensi yang diperlukan:
        ```kotlin
        androidTestImplementation("androidx.compose.ui:ui-test-junit4:...") // Versi Compose Anda
        debugImplementation("androidx.compose.ui:ui-tooling:...")
        debugImplementation("androidx.compose.ui:ui-test-manifest:...")
        ```
    * Sinkronkan Gradle.

2.  **Buat File Test Baru (jika belum ada yang spesifik untuk `FocusWriteScreen`):**
    * Lokasi: `app/src/androidTest/java/com/neimasilk/purefocus/ui/screens/FocusWriteScreenTest.kt`
        ```kotlin
        // Di FocusWriteScreenTest.kt
        package com.neimasilk.purefocus.ui.screens

        import androidx.compose.ui.test.assertIsDisplayed
        import androidx.compose.ui.test.junit4.createAndroidComposeRule // Atau createComposeRule jika tidak butuh Activity
        import androidx.compose.ui.test.onNodeWithText
        import androidx.test.ext.junit.runners.AndroidJUnit4
        import com.neimasilk.purefocus.MainActivity // Jika FocusWriteScreen dihost di MainActivity
        import com.neimasilk.purefocus.data.PreferencesManager
        import com.neimasilk.purefocus.ui.PomodoroTimerViewModel
        import com.neimasilk.purefocus.ui.theme.PureFocusTheme
        import com.neimasilk.purefocus.util.PerformanceMonitor
        import dagger.hilt.android.testing.HiltAndroidRule
        import dagger.hilt.android.testing.HiltAndroidTest
        import kotlinx.coroutines.flow.MutableStateFlow
        import org.junit.Before
        import org.junit.Rule
        import org.junit.Test
        import org.junit.runner.RunWith
        import org.mockito.Mockito // Jika menggunakan Mockito untuk mock dependensi
        import javax.inject.Inject

        @HiltAndroidTest // Jika menggunakan Hilt dalam tes
        @RunWith(AndroidJUnit4::class)
        class FocusWriteScreenTest {

            @get:Rule(order = 0)
            val hiltRule = HiltAndroidRule(this)

            // Menggunakan createAndroidComposeRule jika ingin meluncurkan Activity
            @get:Rule(order = 1)
            val composeTestRule = createAndroidComposeRule<MainActivity>()
            // Alternatif jika hanya ingin menguji Composable secara terisolasi (membutuhkan setup ViewModel manual):
            // @get:Rule
            // val composeTestRule = createComposeRule()

            // Jika Anda perlu mock ViewModel atau dependensinya:
            // lateinit var mockViewModel: PomodoroTimerViewModel
            // lateinit var mockPrefsManager: PreferencesManager
            // lateinit var mockPerfMonitor: PerformanceMonitor

            @Before
            fun setUp() {
                hiltRule.inject() // Penting untuk Hilt

                // Jika tidak menggunakan Hilt untuk inject ViewModel di test atau perlu mock
                // mockPrefsManager = Mockito.mock(PreferencesManager::class.java)
                // Mockito.`when`(mockPrefsManager.focusDurationMinutes).thenReturn(MutableStateFlow(25 * 60 * 1000L))
                // // ... mock aliran data lain dari prefsManager
                // mockPerfMonitor = Mockito.mock(PerformanceMonitor::class.java)
                // mockViewModel = PomodoroTimerViewModel(mockPrefsManager, mockPerfMonitor)

                // Jika menggunakan createComposeRule() dan tidak createAndroidComposeRule:
                // composeTestRule.setContent {
                //     PureFocusTheme {
                //         FocusWriteScreen(pomodoroTimerViewModel = mockViewModel /* atau ViewModel dari Hilt */)
                //     }
                // }
            }

            @Test
            fun startButton_isDisplayed_byDefault() {
                // Pastikan MainActivity (atau host Composable Anda) menampilkan FocusWriteScreen
                // dan tombol "Start" memang ada di sana secara default.
                // Teks tombol mungkin berbeda ("Mulai", "Start Timer", dll.), sesuaikan.
                composeTestRule.onNodeWithText("Start", ignoreCase = true).assertIsDisplayed()
                // Jika tombol Start hanya muncul setelah beberapa kondisi, sesuaikan setup tes.
            }

            @Test
            fun timerDisplay_isDisplayed_byDefault() {
                // Asumsi ada teks yang menampilkan waktu, misal "25:00"
                // Ini mungkin perlu penyesuaian berdasarkan format waktu Anda.
                // Anda mungkin perlu menggunakan onNode(hasText(...)) atau semantic properties.
                // Untuk awal, cari saja salah satu tombol jika timer belum ada.
                // Atau, jika timer selalu ada dengan nilai default:
                // composeTestRule.onNodeWithText("25:00").assertIsDisplayed()
                // Jika teks timer dinamis, lebih baik cari berdasarkan testTag atau deskripsi konten.
                // Untuk baby step, verifikasi keberadaan tombol saja sudah cukup jika timer kompleks.
            }

            @Test
            fun textField_isDisplayed_forFocusWrite() {
                // Asumsi TextField memiliki label atau placeholder "Tulis fokus Anda di sini..."
                // composeTestRule.onNodeWithText("Tulis fokus Anda di sini...", ignoreCase = true).assertIsDisplayed()
                // Atau jika menggunakan testTag:
                // composeTestRule.onNodeWithTag("focusWriteTextField").assertIsDisplayed()
                // Pastikan Anda menambahkan Modifier.testTag("focusWriteTextField") ke TextField di FocusWriteScreen.kt
            }
        }
        ```
    * **Catatan Penting tentang `createAndroidComposeRule` vs `createComposeRule`:**
        * `createAndroidComposeRule<MainActivity>()`: Meluncurkan `MainActivity` Anda. Ini lebih cocok untuk tes integrasi UI yang lebih besar. Anda perlu memastikan `MainActivity` akan menampilkan `FocusWriteScreen` dalam keadaan default atau setelah navigasi minimal.
        * `createComposeRule()`: Hanya menyiapkan lingkungan untuk menjalankan Composable secara terisolasi. Anda harus memanggil `composeTestRule.setContent { ... }` secara manual dan menyediakan semua dependensi (seperti ViewModel). Ini lebih cepat untuk tes unit Composable murni.
        * Untuk baby step ini, `createAndroidComposeRule` mungkin lebih mudah jika `FocusWriteScreen` adalah layar utama atau mudah diakses.

3.  **Tulis Test Case Sederhana:**
    * Verifikasi bahwa tombol "Start" (atau teks yang sesuai untuk memulai timer) ditampilkan di `FocusWriteScreen`.
        ```kotlin
        @Test
        fun startButton_isDisplayed_byDefault() {
            composeTestRule.onNodeWithText("Start", ignoreCase = true).assertIsDisplayed()
            // Ganti "Start" dengan teks tombol yang sebenarnya jika berbeda.
        }
        ```
    * (Opsional untuk baby step ini, tapi bagus) Tambahkan `Modifier.testTag("myStartButton")` ke tombol Start di `FocusWriteScreen.kt` dan gunakan `onNodeWithTag("myStartButton")` di tes Anda. Ini lebih robust terhadap perubahan teks.

4.  **Jalankan Instrumented Test:**
    * Anda bisa menjalankan tes ini dari Android Studio dengan mengklik kanan pada nama fungsi tes atau nama kelas tes dan memilih "Run ...".
    * Pastikan emulator atau perangkat fisik terhubung.

**Kriteria Lolos (Verifikasi Manual & Tes):**
* [ ] Instrumented test `startButton_isDisplayed_byDefault` (atau nama yang Anda pilih) berhasil dijalankan dan lolos (berwarna hijau).
* [ ] Test lain yang Anda tambahkan (seperti `timerDisplay_isDisplayed_byDefault` atau `textField_isDisplayed_forFocusWrite`) juga lolos jika Anda mengimplementasikannya.
* [ ] Proses setup tes (Hilt, peluncuran Activity/Composable) berjalan tanpa error.

