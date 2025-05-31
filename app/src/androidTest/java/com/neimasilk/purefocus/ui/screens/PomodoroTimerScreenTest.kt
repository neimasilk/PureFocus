package com.neimasilk.purefocus.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag // Import tambahan
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.neimasilk.purefocus.MainActivity // Pastikan MainActivity adalah host untuk PomodoroTimerScreen
import org.junit.Rule
import org.junit.Test

class PomodoroTimerScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun timerScreen_initialDisplay_showsDefaultTimeAndStartButton() {
        // Verifikasi bahwa timer ditampilkan dengan waktu default (25:00)
        composeTestRule.onNodeWithTag("TimerText").assertIsDisplayed()
        composeTestRule.onNodeWithText("25:00").assertIsDisplayed() // Bisa juga cek teks spesifik jika perlu

        // Verifikasi bahwa tombol "Start" ditampilkan
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
    }

    @Test
    fun timerScreen_clickStartButton_updatesButtonToPauseAndTimerRuns() {
        // Klik tombol "Start"
        composeTestRule.onNodeWithTag("StartButton").performClick()

        // Verifikasi bahwa tombol berubah menjadi "Pause"
        composeTestRule.onNodeWithTag("PauseButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pause").assertIsDisplayed()

        // Tunggu hingga waktu berubah dari 25:00 (maksimum 3 detik)
        val initialTimeMillis = 25 * 60 * 1000L
        var timeChanged = false
        try {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                // Cek apakah node dengan teks selain 25:00 ada. Ini lebih fleksibel daripada mencari 24:58.
                // Kita bisa lebih spesifik jika perlu, tapi ini cukup untuk verifikasi awal.
                // Idealnya, kita akan memeriksa bahwa nilai timeLeftInMillis di ViewModel telah berkurang.
                // Namun, untuk tes UI murni, kita periksa output teks.
                // Perlu cara untuk mendapatkan teks dari node TimerText dan membandingkannya.
                // Untuk saat ini, kita asumsikan perubahan akan terjadi dan tidak sama dengan 25:00.
                // Ini masih bisa rapuh. Cara yang lebih baik adalah mengekspos state ViewModel ke tes jika memungkinkan
                // atau menggunakan IdlingResource.

                // Coba cari node yang BUKAN 25:00. Ini akan gagal jika masih 25:00.
                // Jika berhasil, berarti waktu telah berubah.
                // Ini adalah hack, cara yang lebih baik adalah dengan IdlingResource atau akses ke ViewModel.
                // Untuk sekarang, kita akan coba menunggu perubahan spesifik ke "24:59" atau "24:58"
                // composeTestRule.onNodeWithText("25:00", substring = true).isNotDisplayed() // Ini akan error jika masih 25:00
                // Untuk sementara, kita akan mencari waktu yang lebih kecil dari 25:00
                // Ini masih belum ideal karena race condition.
                // Kita akan coba mencari "24:59" atau "24:58"
                val found2459 = try { composeTestRule.onNodeWithText("24:59").assertIsDisplayed(); true } catch (e: AssertionError) { false }
                val found2458 = try { composeTestRule.onNodeWithText("24:58").assertIsDisplayed(); true } catch (e: AssertionError) { false }
                timeChanged = found2459 || found2458
                timeChanged
            }
        } catch (e: androidx.compose.ui.test.ComposeTimeoutException) {
            // Waktu tidak berubah setelah 3 detik, tes gagal.
            // Kita bisa log atau assert false di sini jika mau.
        }

        // Verifikasi bahwa waktu telah berubah
        assert(timeChanged) { "Timer did not update after clicking Start." }
    }

    @Test
    fun timerScreen_pauseAndResumeTimer_worksCorrectly() {
        // Start timer
        composeTestRule.onNodeWithTag("StartButton").performClick()
        
        // Verify timer is running (Pause button is displayed)
        composeTestRule.onNodeWithTag("PauseButton").assertIsDisplayed()
        
        // Pause timer
        composeTestRule.onNodeWithTag("PauseButton").performClick()
        
        // Verify timer is paused (Start button is displayed again)
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
        
        // Resume timer
        composeTestRule.onNodeWithTag("StartButton").performClick()
        
        // Verify timer is running again (Pause button is displayed)
        composeTestRule.onNodeWithTag("PauseButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pause").assertIsDisplayed()
    }
    
    @Test
    fun timerScreen_resetTimer_resetsToDefaultTime() {
        // Start timer
        composeTestRule.onNodeWithTag("StartButton").performClick()
        
        // Wait a moment for timer to change
        Thread.sleep(1000)
        
        // Reset timer
        composeTestRule.onNodeWithTag("ResetButton").performClick()
        
        // Verify timer is reset to default time (25:00)
        composeTestRule.onNodeWithText("25:00").assertIsDisplayed()
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
    }
}