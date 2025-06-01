package com.neimasilk.purefocus.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertTextEquals
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.neimasilk.purefocus.ui.screens.FocusWriteScreen
import com.neimasilk.purefocus.ui.theme.PureFocusTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * Instrumented test untuk integrasi FocusWriteScreen dengan FocusWriteViewModel.
 * 
 * Test ini memverifikasi bahwa:
 * - FocusWriteScreen dapat ditampilkan dengan ViewModel
 * - Text input berfungsi dengan benar melalui ViewModel
 * - Two-way data binding antara UI dan ViewModel bekerja
 */
@RunWith(AndroidJUnit4::class)
class FocusWriteViewModelIntegrationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun focusWriteScreen_withViewModel_isDisplayed() {
        // Given
        val viewModel = FocusWriteViewModel()
        
        // When
        composeTestRule.setContent {
            PureFocusTheme {
                val textFieldValue by viewModel.textFieldValue.collectAsState()
                
                FocusWriteScreen(
                    textFieldValue = textFieldValue,
                    onTextFieldValueChanged = { viewModel.updateText(it) },
                    onClearText = { viewModel.clearText() },
                    wordCount = viewModel.getWordCount(),
                    characterCount = viewModel.getCharacterCount()
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithTag("focusWriteTextField")
            .assertIsDisplayed()
    }
    
    @Test
    fun focusWriteScreen_textInput_updatesViewModel() {
        // Given
        val viewModel = FocusWriteViewModel()
        val testText = "Hello World"
        
        // When
        composeTestRule.setContent {
            PureFocusTheme {
                val textFieldValue by viewModel.textFieldValue.collectAsState()
                
                FocusWriteScreen(
                    textFieldValue = textFieldValue,
                    onTextFieldValueChanged = { viewModel.updateText(it) },
                    onClearText = { viewModel.clearText() },
                    wordCount = viewModel.getWordCount(),
                    characterCount = viewModel.getCharacterCount()
                )
            }
        }
        
        // Type text into the TextField
        composeTestRule
            .onNodeWithTag("focusWriteTextField")
            .performTextInput(testText)
        
        // Then - verify text is displayed in UI
        composeTestRule
            .onNodeWithTag("focusWriteTextField")
            .assertTextEquals(testText)
        
        // And verify ViewModel state is updated
        assert(viewModel.getCurrentText() == testText)
        assert(viewModel.getWordCount() == 2) // "Hello World" = 2 words
        assert(viewModel.getCharacterCount() == 11) // "Hello World" = 11 characters
    }
    
    @Test
    fun focusWriteScreen_emptyText_showsCorrectCounts() {
        // Given
        val viewModel = FocusWriteViewModel()
        
        // When
        composeTestRule.setContent {
            PureFocusTheme {
                val textFieldValue by viewModel.textFieldValue.collectAsState()
                
                FocusWriteScreen(
                    textFieldValue = textFieldValue,
                    onTextFieldValueChanged = { viewModel.updateText(it) },
                    onClearText = { viewModel.clearText() },
                    wordCount = viewModel.getWordCount(),
                    characterCount = viewModel.getCharacterCount()
                )
            }
        }
        
        // Then - verify initial state
        assert(viewModel.getCurrentText() == "")
        assert(viewModel.getWordCount() == 0)
        assert(viewModel.getCharacterCount() == 0)
        
        // And verify TextField is empty
        composeTestRule
            .onNodeWithTag("focusWriteTextField")
            .assertTextEquals("")
    }
}