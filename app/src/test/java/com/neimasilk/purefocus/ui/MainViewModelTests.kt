package com.neimasilk.purefocus.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.neimasilk.purefocus.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.times
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class MainViewModelTests {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: MainViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesManager = mock()
        whenever(preferencesManager.isDarkMode).thenReturn(false)
        whenever(preferencesManager.lastText).thenReturn("")
        viewModel = MainViewModel(preferencesManager)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `updateText updates uiState immediately`() = runTest {
        // Given
        val newText = "Hello, PureFocus!"
        
        // When
        viewModel.updateText(newText)
        
        // Then - verify the state was updated
        assertEquals(newText, viewModel.uiState.value.textFieldValue.text)
    }
    
    @Test
    fun `updateTextFieldValue updates uiState with correct TextFieldValue`() = runTest {
        // Given
        val newText = "Hello, PureFocus!"
        val newTextFieldValue = TextFieldValue(text = newText, selection = TextRange(5, 10))
        
        // When
        viewModel.updateTextFieldValue(newTextFieldValue)
        
        // Then - verify the state was updated with correct text and selection
        assertEquals(newText, viewModel.uiState.value.textFieldValue.text)
        assertEquals(TextRange(5, 10), viewModel.uiState.value.textFieldValue.selection)
    }
    
    @Test
    fun `text is saved to preferences after debounce delay`() = runTest {
        // Given
        val newText = "Hello, PureFocus!"
        
        // When
        viewModel.updateText(newText)
        
        // Advance time by less than debounce delay (500ms)
        testDispatcher.scheduler.advanceTimeBy(300)
        
        // Then - text should not be saved yet
        verify(preferencesManager, times(0)).lastText = newText
        
        // When - advance time past debounce delay
        testDispatcher.scheduler.advanceTimeBy(300) // Total 600ms
        testDispatcher.scheduler.runCurrent() // Run pending tasks
        
        // Then - text should be saved
        verify(preferencesManager).lastText = newText
    }
    
    @Test
    fun `text is loaded from preferences on init`() = runTest {
        // Given
        val savedText = "Saved text"
        whenever(preferencesManager.lastText).thenReturn(savedText)
        
        // When - create a new viewModel to trigger init
        val newViewModel = MainViewModel(preferencesManager)
        
        // Then - verify the state was initialized with the saved text
        assertEquals(savedText, newViewModel.uiState.value.textFieldValue.text)
    }
    
    @Test
    fun `multiple rapid text updates only save once`() = runTest {
        // Given
        
        // When - multiple rapid updates
        viewModel.updateText("Text 1")
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.updateText("Text 2")
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.updateText("Text 3")
        
        // Then - no saves yet
        verify(preferencesManager, times(0)).lastText = any()
        
        // When - advance time past debounce delay
        testDispatcher.scheduler.advanceTimeBy(500) // Total 700ms
        testDispatcher.scheduler.runCurrent() // Run pending tasks
        
        // Then - only the last text should be saved
        verify(preferencesManager).lastText = "Text 3"
        assertEquals("Text 3", viewModel.uiState.value.textFieldValue.text)
    }
    
    @Test
    fun `multiple rapid TextFieldValue updates preserve selection`() = runTest {
        // Given
        val text1 = TextFieldValue("Text 1", selection = TextRange(3))
        val text2 = TextFieldValue("Text 2", selection = TextRange(4))
        val text3 = TextFieldValue("Text 3", selection = TextRange(5))
        
        // When - multiple rapid updates with different selections
        viewModel.updateTextFieldValue(text1)
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.updateTextFieldValue(text2)
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.updateTextFieldValue(text3)
        
        // Then - selection should be preserved for the latest update
        assertEquals(text3.selection, viewModel.uiState.value.textFieldValue.selection)
        
        // When - advance time past debounce delay
        testDispatcher.scheduler.advanceTimeBy(500) // Total 700ms
        testDispatcher.scheduler.runCurrent() // Run pending tasks
        
        // Then - only the last text should be saved, and selection still preserved
        verify(preferencesManager).lastText = "Text 3"
        assertEquals("Text 3", viewModel.uiState.value.textFieldValue.text)
        assertEquals(TextRange(5), viewModel.uiState.value.textFieldValue.selection)
    }
}