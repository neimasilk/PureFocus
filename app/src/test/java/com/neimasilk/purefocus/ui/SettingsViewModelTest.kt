package com.neimasilk.purefocus.ui

import com.neimasilk.purefocus.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @Mock
    private lateinit var mockPreferencesManager: PreferencesManager

    private lateinit var settingsViewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Mock StateFlows
        whenever(mockPreferencesManager.focusDuration).thenReturn(MutableStateFlow(25))
        whenever(mockPreferencesManager.shortBreakDuration).thenReturn(MutableStateFlow(5))
        whenever(mockPreferencesManager.longBreakDuration).thenReturn(MutableStateFlow(15))
        whenever(mockPreferencesManager.enableSoundNotifications).thenReturn(MutableStateFlow(true))
        
        settingsViewModel = SettingsViewModel(mockPreferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateFocusDuration calls PreferencesManager with valid duration`() {
        // Given
        val validDuration = 30
        
        // When
        settingsViewModel.updateFocusDuration(validDuration)
        
        // Then
        verify(mockPreferencesManager).updateFocusDuration(validDuration)
    }

    @Test
    fun `updateFocusDuration does not call PreferencesManager with invalid duration`() {
        // Given
        val invalidDuration = 200 // exceeds max limit of 180
        
        // When
        settingsViewModel.updateFocusDuration(invalidDuration)
        
        // Then
        verify(mockPreferencesManager, org.mockito.kotlin.never()).updateFocusDuration(invalidDuration)
    }

    @Test
    fun `updateShortBreakDuration calls PreferencesManager with valid duration`() {
        // Given
        val validDuration = 10
        
        // When
        settingsViewModel.updateShortBreakDuration(validDuration)
        
        // Then
        verify(mockPreferencesManager).updateShortBreakDuration(validDuration)
    }

    @Test
    fun `updateShortBreakDuration does not call PreferencesManager with invalid duration`() {
        // Given
        val invalidDuration = 70 // exceeds max limit of 60
        
        // When
        settingsViewModel.updateShortBreakDuration(invalidDuration)
        
        // Then
        verify(mockPreferencesManager, org.mockito.kotlin.never()).updateShortBreakDuration(invalidDuration)
    }

    @Test
    fun `updateLongBreakDuration calls PreferencesManager with valid duration`() {
        // Given
        val validDuration = 20
        
        // When
        settingsViewModel.updateLongBreakDuration(validDuration)
        
        // Then
        verify(mockPreferencesManager).updateLongBreakDuration(validDuration)
    }

    @Test
    fun `updateLongBreakDuration does not call PreferencesManager with invalid duration`() {
        // Given
        val invalidDuration = 150 // exceeds max limit of 120
        
        // When
        settingsViewModel.updateLongBreakDuration(invalidDuration)
        
        // Then
        verify(mockPreferencesManager, org.mockito.kotlin.never()).updateLongBreakDuration(invalidDuration)
    }

    @Test
    fun `toggleSoundNotifications calls PreferencesManager`() {
        // Given
        val enableSound = false
        
        // When
        settingsViewModel.toggleSoundNotifications(enableSound)
        
        // Then
        verify(mockPreferencesManager).updateEnableSoundNotifications(enableSound)
    }

    @Test
    fun `StateFlows are properly exposed from PreferencesManager`() {
        // Given/When - StateFlows are initialized in setup
        
        // Then
        assert(settingsViewModel.focusDuration.value == 25)
        assert(settingsViewModel.shortBreakDuration.value == 5)
        assert(settingsViewModel.longBreakDuration.value == 15)
        assert(settingsViewModel.enableSoundNotifications.value == true)
    }
}