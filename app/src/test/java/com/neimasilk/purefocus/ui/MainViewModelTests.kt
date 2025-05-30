package com.neimasilk.purefocus.ui

import com.neimasilk.purefocus.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
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
    private val testScope = TestScope(testDispatcher)
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: MainViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesManager = mock()
        whenever(preferencesManager.isDarkMode).thenReturn(false)
        whenever(preferencesManager.lastText).thenReturn("")
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `updateText updates uiState immediately`() = testScope.runTest {
        // Given
        viewModel = MainViewModel(preferencesManager)
        val newText = "Hello, PureFocus!"
        
        // When
        viewModel.updateText(newText)
        
        // Then
        assertEquals(newText, viewModel.uiState.value.text)
    }
    
    @Test
    fun `text is saved to preferences after debounce delay`() = testScope.runTest {
        // Given
        viewModel = MainViewModel(preferencesManager)
        val newText = "Hello, PureFocus!"
        
        // When
        viewModel.updateText(newText)
        
        // Advance time by less than debounce delay (500ms)
        advanceTimeBy(300)
        
        // Then - text should not be saved yet
        verify(preferencesManager, times(0)).lastText = newText
        
        // When - advance time past debounce delay
        advanceTimeBy(300) // Total 600ms
        
        // Then - text should be saved
        verify(preferencesManager).lastText = newText
    }
    
    @Test
    fun `text is loaded from preferences on init`() = testScope.runTest {
        // Given
        val savedText = "Saved text"
        whenever(preferencesManager.lastText).thenReturn(savedText)
        
        // When
        viewModel = MainViewModel(preferencesManager)
        
        // Then
        assertEquals(savedText, viewModel.uiState.value.text)
    }
    
    @Test
    fun `multiple rapid text updates only save once`() = testScope.runTest {
        // Given
        viewModel = MainViewModel(preferencesManager)
        
        // When - multiple rapid updates
        viewModel.updateText("Text 1")
        advanceTimeBy(100)
        viewModel.updateText("Text 2")
        advanceTimeBy(100)
        viewModel.updateText("Text 3")
        
        // Then - no saves yet
        verify(preferencesManager, times(0)).lastText = any()
        
        // When - advance time past debounce delay
        advanceTimeBy(500) // Total 700ms
        
        // Then - only the last text should be saved
        verify(preferencesManager).lastText = "Text 3"
    }
}