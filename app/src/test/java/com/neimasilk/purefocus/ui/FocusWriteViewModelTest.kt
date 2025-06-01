package com.neimasilk.purefocus.ui

import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit test untuk FocusWriteViewModel.
 * Memverifikasi bahwa state updates berfungsi dengan benar.
 */
@ExperimentalCoroutinesApi
class FocusWriteViewModelTest {
    
    private lateinit var viewModel: FocusWriteViewModel
    
    @Before
    fun setup() {
        viewModel = FocusWriteViewModel()
    }
    
    @Test
    fun `initial state should be empty`() = runTest {
        // Verify initial state
        val initialValue = viewModel.textFieldValue.value
        assertEquals("", initialValue.text)
        assertEquals(0, viewModel.getWordCount())
        assertEquals(0, viewModel.getCharacterCount())
    }
    
    @Test
    fun `updateText should update textFieldValue state`() = runTest {
        // Given
        val testText = "Hello World"
        val testTextFieldValue = TextFieldValue(testText)
        
        // When
        viewModel.updateText(testTextFieldValue)
        
        // Then
        assertEquals(testText, viewModel.textFieldValue.value.text)
        assertEquals(testText, viewModel.getCurrentText())
    }
    
    @Test
    fun `clearText should reset textFieldValue to empty`() = runTest {
        // Given - set some text first
        val testText = "Some text to clear"
        viewModel.updateText(TextFieldValue(testText))
        
        // When
        viewModel.clearText()
        
        // Then
        assertEquals("", viewModel.textFieldValue.value.text)
        assertEquals("", viewModel.getCurrentText())
    }
    
    @Test
    fun `getWordCount should return correct word count`() = runTest {
        // Test empty text
        assertEquals(0, viewModel.getWordCount())
        
        // Test single word
        viewModel.updateText(TextFieldValue("Hello"))
        assertEquals(1, viewModel.getWordCount())
        
        // Test multiple words
        viewModel.updateText(TextFieldValue("Hello World Test"))
        assertEquals(3, viewModel.getWordCount())
        
        // Test text with extra spaces
        viewModel.updateText(TextFieldValue("  Hello   World  "))
        assertEquals(2, viewModel.getWordCount())
    }
    
    @Test
    fun `getCharacterCount should return correct character count`() = runTest {
        // Test empty text
        assertEquals(0, viewModel.getCharacterCount())
        
        // Test text with characters
        viewModel.updateText(TextFieldValue("Hello"))
        assertEquals(5, viewModel.getCharacterCount())
        
        // Test text with spaces
        viewModel.updateText(TextFieldValue("Hello World"))
        assertEquals(11, viewModel.getCharacterCount())
    }
    
    @Test
    fun `state updates should be reflected in StateFlow`() = runTest {
        // Given
        val testText1 = "First text"
        val testText2 = "Second text"
        
        // When - update text multiple times
        viewModel.updateText(TextFieldValue(testText1))
        assertEquals(testText1, viewModel.textFieldValue.value.text)
        
        viewModel.updateText(TextFieldValue(testText2))
        assertEquals(testText2, viewModel.textFieldValue.value.text)
        
        // When - clear text
        viewModel.clearText()
        assertEquals("", viewModel.textFieldValue.value.text)
    }
}