package com.neimasilk.purefocus.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.neimasilk.purefocus.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test untuk FocusWriteScreen.
 * 
 * Test ini memverifikasi bahwa elemen-elemen UI dasar dari FocusWriteScreen
 * ditampilkan dengan benar saat aplikasi diluncurkan.
 */
@RunWith(AndroidJUnit4::class)
class FocusWriteScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun settingsButton_isDisplayed_byDefault() {
        // Verifikasi bahwa tombol Settings (FloatingActionButton) ditampilkan
        // di layar utama secara default
        composeTestRule.onNodeWithContentDescription("Settings")
            .assertIsDisplayed()
    }

    @Test
    fun focusWriteScreen_isDisplayed_byDefault() {
        // Verifikasi bahwa FocusWriteScreen ditampilkan secara default
        // (bukan SettingsScreen)
        // Kita bisa memverifikasi ini dengan memastikan tombol Settings
        // menampilkan "Settings" bukan "Back to Focus"
        composeTestRule.onNodeWithContentDescription("Settings")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_isDisplayed_afterClickingSettingsButton() {
        // Klik tombol Settings
        composeTestRule.onNodeWithContentDescription("Settings")
            .performClick()
        
        // Verifikasi bahwa sekarang tombol menampilkan "Back to Focus"
        // yang menandakan kita sekarang di SettingsScreen
        composeTestRule.onNodeWithContentDescription("Back to Focus")
            .assertIsDisplayed()
    }

    @Test
    fun canNavigateBackToFocusWriteScreen_fromSettings() {
        // Navigasi ke Settings
        composeTestRule.onNodeWithContentDescription("Settings")
            .performClick()
        
        // Verifikasi kita di SettingsScreen
        composeTestRule.onNodeWithContentDescription("Back to Focus")
            .assertIsDisplayed()
        
        // Klik tombol untuk kembali ke FocusWriteScreen
        composeTestRule.onNodeWithContentDescription("Back to Focus")
            .performClick()
        
        // Verifikasi kita kembali ke FocusWriteScreen
        composeTestRule.onNodeWithContentDescription("Settings")
            .assertIsDisplayed()
    }
}