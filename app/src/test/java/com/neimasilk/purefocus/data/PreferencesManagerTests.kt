package com.neimasilk.purefocus.data

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PreferencesManagerTests {
    
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var preferencesManager: PreferencesManager
    
    @Before
    fun setup() {
        // Mock SharedPreferences dan Editor
        context = mock()
        sharedPreferences = mock()
        editor = mock()
        
        // Setup behavior untuk mock
        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenReturn(editor)
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
        
        // Inisialisasi PreferencesManager dengan mock
        preferencesManager = PreferencesManager(context)
    }
    
    @Test
    fun `lastText getter returns value from SharedPreferences`() {
        // Given
        val expectedText = "Test text"
        whenever(sharedPreferences.getString(eq("last_text"), any())).thenReturn(expectedText)
        
        // When
        val result = preferencesManager.lastText
        
        // Then
        assertEquals(expectedText, result)
    }
    
    @Test
    fun `lastText getter returns empty string when SharedPreferences returns null`() {
        // Given
        whenever(sharedPreferences.getString(eq("last_text"), any())).thenReturn(null)
        
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
        verify(editor).putString(eq("last_text"), eq(textToSave))
        verify(editor).apply()
    }
    
    @Test
    fun `isDarkMode getter returns value from SharedPreferences`() {
        // Given
        val expectedValue = true
        whenever(sharedPreferences.getBoolean(eq("dark_mode"), any())).thenReturn(expectedValue)
        
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
        verify(editor).putBoolean(eq("dark_mode"), eq(valueToSave))
        verify(editor).apply()
    }
}