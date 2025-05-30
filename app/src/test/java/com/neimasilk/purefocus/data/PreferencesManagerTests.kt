package com.neimasilk.purefocus.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
}