package com.neimasilk.purefocus.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PreferencesManagerTests {
    
    private lateinit var context: Context
    private lateinit var preferencesManager: PreferencesManager
    
    @Before
    fun setup() {
        // Use real Android context from Robolectric
        context = ApplicationProvider.getApplicationContext()
        
        // Clear any existing preferences for clean test state
        context.getSharedPreferences("purefocus_preferences", Context.MODE_PRIVATE)
            .edit().clear().apply()
        
        // Inisialisasi PreferencesManager dengan real context
        preferencesManager = PreferencesManager(context)
    }
    
    @Test
    fun `lastText getter returns value from SharedPreferences`() {
        // Given
        val expectedText = "Test text"
        preferencesManager.lastText = expectedText
        
        // When
        val result = preferencesManager.lastText
        
        // Then
        assertEquals(expectedText, result)
    }
    
    @Test
    fun `lastText getter returns empty string when SharedPreferences returns null`() {
        // Given - fresh PreferencesManager with no stored value
        // (setup already clears preferences)
        
        // When
        val result = preferencesManager.lastText
        
        // Then
        assertEquals("", result)
    }
    
    @Test
    fun `lastText setter saves value to SharedPreferences`() {
        // Given
        val textToSave = "New text"
        
        // When
        preferencesManager.lastText = textToSave
        
        // Then
        val savedText = preferencesManager.lastText
        assertEquals(textToSave, savedText)
    }
    
    @Test
    fun `isDarkMode getter returns value from SharedPreferences`() {
        // Given
        val expectedValue = true
        preferencesManager.isDarkMode = expectedValue
        
        // When
        val result = preferencesManager.isDarkMode
        
        // Then
        assertEquals(expectedValue, result)
    }
    
    @Test
    fun `isDarkMode setter saves value to SharedPreferences`() {
        // Given
        val valueToSave = true
        
        // When
        preferencesManager.isDarkMode = valueToSave
        
        // Then
        val savedValue = preferencesManager.isDarkMode
        assertEquals(valueToSave, savedValue)
    }
    
    @Test
    fun `saveFocusWriteText saves text to SharedPreferences`() {
        // Given
        val textToSave = "Test focus write text"
        
        // When
        preferencesManager.saveFocusWriteText(textToSave)
        
        // Then
        val savedText = preferencesManager.lastText
        assertEquals(textToSave, savedText)
    }
    
    @Test
    fun `getFocusWriteText returns saved text`() = runBlocking {
        // Given
        val expectedText = "Saved focus text"
        preferencesManager.saveFocusWriteText(expectedText)
        
        // When
        val result = preferencesManager.getFocusWriteText().first()
        
        // Then
        assertEquals(expectedText, result)
    }
    
    @Test
    fun `getFocusWriteText returns empty string when no text saved`() = runBlocking {
        // Given - fresh PreferencesManager with no stored value
        
        // When
        val result = preferencesManager.getFocusWriteText().first()
        
        // Then
        assertEquals("", result)
    }
    
    @Test
    fun `clearFocusWriteText removes text from SharedPreferences`() {
        // Given
        val textToSave = "Text to be cleared"
        preferencesManager.saveFocusWriteText(textToSave)
        
        // When
        preferencesManager.clearFocusWriteText()
        
        // Then
        val result = preferencesManager.lastText
        assertEquals("", result)
    }
}