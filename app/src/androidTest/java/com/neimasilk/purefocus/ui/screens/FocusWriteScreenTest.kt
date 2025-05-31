package com.neimasilk.purefocus.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.neimasilk.purefocus.ui.theme.PureFocusTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test untuk FocusWriteScreen.
 * 
 * Test ini memverifikasi bahwa elemen-elemen UI dasar dari FocusWriteScreen
 * ditampilkan dengan benar.
 */
@RunWith(AndroidJUnit4::class)
class FocusWriteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Setup test dengan menampilkan FocusWriteScreen dalam tema aplikasi
        composeTestRule.setContent {
            PureFocusTheme {
                FocusWriteScreen(
                    text = "",
                    onTextChanged = { /* No-op untuk test */ }
                )
            }
        }
    }

    @Test
    fun focusWriteTextField_isDisplayed_byDefault() {
        // Verifikasi bahwa text field untuk menulis fokus ditampilkan
        // Menggunakan testTag yang akan ditambahkan ke FocusWriteScreen
        composeTestRule.onNodeWithTag("focusWriteTextField").assertIsDisplayed()
    }

    @Test
    fun focusWriteScreen_displaysCorrectly() {
        // Test sederhana untuk memastikan screen dapat di-render tanpa crash
        // Ini adalah test dasar yang memverifikasi bahwa Composable dapat dibuat
        composeTestRule.onNodeWithTag("focusWriteTextField").assertExists()
    }
}