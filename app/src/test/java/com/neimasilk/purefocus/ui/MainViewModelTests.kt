package com.neimasilk.purefocus.ui

import android.content.Context
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.core.app.ApplicationProvider
import com.neimasilk.purefocus.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MainViewModelTests {
    
    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: MainViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Clear any existing preferences
        context.getSharedPreferences("purefocus_preferences", Context.MODE_PRIVATE).edit().clear().apply()
        preferencesManager = PreferencesManager(context)
        viewModel = MainViewModel(preferencesManager)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `updateText updates uiState immediately`() = testScope.runTest {
        // Given
        val newText = "Hello, PureFocus!"
        
        // When
        viewModel.updateText(newText)
        
        // Then - verify the state was updated
        assertEquals(newText, viewModel.uiState.value.textFieldValue.text)
    }
    
    @Test
    fun `updateTextFieldValue updates uiState with correct TextFieldValue`() = testScope.runTest {
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
    fun `text is saved to preferences after debounce delay`() = testScope.runTest {
        // Given
        val newText = "Hello, PureFocus!"
        
        // When
        viewModel.updateText(newText)
        
        // Advance time by less than debounce delay (1000ms)
        testScope.testScheduler.advanceTimeBy(500)
        
        // Then - text should not be saved yet
        assertEquals("", runBlocking { preferencesManager.getFocusWriteText().first() })
        
        // When - advance time past debounce delay
        testScope.testScheduler.advanceTimeBy(600) // Total 1100ms
        
        // Then - text should be saved
        assertEquals(newText, runBlocking { preferencesManager.getFocusWriteText().first() })
    }
    
    @Test
    fun `text is loaded from preferences on init`() = testScope.runTest {
        // Given
        val savedText = "Saved text"
        preferencesManager.saveFocusWriteText(savedText)
        
        // When - create a new viewModel to trigger init
        val newViewModel = MainViewModel(preferencesManager)
        
        // Then - verify the state was initialized with the saved text
        assertEquals(savedText, newViewModel.uiState.value.textFieldValue.text)
    }
    
    @Test
    fun `multiple rapid text updates only save once`() = testScope.runTest {
        // Given
        
        // When - multiple rapid updates
        viewModel.updateText("Text 1")
        testScope.testScheduler.advanceTimeBy(100)
        viewModel.updateText("Text 2")
        testScope.testScheduler.advanceTimeBy(100)
        viewModel.updateText("Text 3")
        
        // Then - no saves yet
        assertEquals("", runBlocking { preferencesManager.getFocusWriteText().first() })
        
        // When - advance time past debounce delay
        testScope.testScheduler.advanceTimeBy(1000) // Total 1200ms
        testScope.testScheduler.advanceUntilIdle() // Execute all pending coroutines
        
        // Then - only the last text should be saved
        assertEquals("Text 3", runBlocking { preferencesManager.getFocusWriteText().first() })
        assertEquals("Text 3", viewModel.uiState.value.textFieldValue.text)
    }
    
    @Test
    fun `multiple rapid TextFieldValue updates preserve selection`() = testScope.runTest {
        // Given
        val text1 = TextFieldValue("Text 1", selection = TextRange(3))
        val text2 = TextFieldValue("Text 2", selection = TextRange(4))
        val text3 = TextFieldValue("Text 3", selection = TextRange(5))
        
        // When - multiple rapid updates with different selections
        viewModel.updateTextFieldValue(text1)
        testScope.testScheduler.advanceTimeBy(100)
        viewModel.updateTextFieldValue(text2)
        testScope.testScheduler.advanceTimeBy(100)
        viewModel.updateTextFieldValue(text3)
        
        // Then - selection should be preserved for the latest update
        assertEquals(text3.selection, viewModel.uiState.value.textFieldValue.selection)
        
        // When - advance time past debounce delay
        testScope.testScheduler.advanceTimeBy(1000) // Total 1200ms
        testScope.testScheduler.advanceUntilIdle() // Execute all pending coroutines
        
        // Then - only the last text should be saved, and selection still preserved
        assertEquals("Text 3", runBlocking { preferencesManager.getFocusWriteText().first() })
        assertEquals("Text 3", viewModel.uiState.value.textFieldValue.text)
        assertEquals(TextRange(5), viewModel.uiState.value.textFieldValue.selection)
    }
    
    @Test
    fun `clearText clears UI state and preferences`() = testScope.runTest {
        // Given
        val initialText = "Some text to clear"
        viewModel.updateText(initialText)
        
        // Advance time to save the text
        testScope.testScheduler.advanceTimeBy(1100)
        
        // Verify text is saved
        assertEquals(initialText, runBlocking { preferencesManager.getFocusWriteText().first() })
        assertEquals(initialText, viewModel.uiState.value.textFieldValue.text)
        
        // When
        viewModel.clearText()
        
        // Advance time to ensure coroutine completes
        testScope.testScheduler.advanceUntilIdle()
        
        // Then - UI state should be cleared
        assertEquals("", viewModel.uiState.value.textFieldValue.text)
        assertEquals(TextRange.Zero, viewModel.uiState.value.textFieldValue.selection)
        
        // And preferences should be cleared
        assertEquals("", runBlocking { preferencesManager.getFocusWriteText().first() })
    }
    
    @Test
    fun `saveTextManually saves current text immediately`() = testScope.runTest {
        // Given
        val textToSave = "Text to save manually"
        viewModel.updateText(textToSave)
        
        // Verify text is not saved yet (before debounce)
        assertEquals("", runBlocking { preferencesManager.getFocusWriteText().first() })
        
        // When
        viewModel.saveTextManually()
        
        // Advance time to ensure coroutine completes
        testScope.testScheduler.advanceUntilIdle()
        
        // Then - text should be saved immediately
        assertEquals(textToSave, runBlocking { preferencesManager.getFocusWriteText().first() })
    }
}