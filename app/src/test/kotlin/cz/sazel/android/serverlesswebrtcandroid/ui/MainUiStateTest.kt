package cz.sazel.android.serverlesswebrtcandroid.ui

import org.junit.Test
import org.junit.Assert.*

class MainUiStateTest {
    
    @Test
    fun `MainUiState has correct default values`() {
        // Given & When
        val state = MainUiState()
        
        // Then
        assertEquals(emptyList<String>(), state.consoleMessages)
        assertEquals("", state.inputText)
        assertEquals("", state.inputHint)
        assertTrue(state.isInputEnabled)
        assertFalse(state.isProgressVisible)
        assertFalse(state.isCreateOfferVisible)
    }
    
    @Test
    fun `MainUiState copy works correctly`() {
        // Given
        val originalState = MainUiState()
        val newMessages = listOf("message1", "message2")
        val newInputText = "test input"
        
        // When
        val updatedState = originalState.copy(
            consoleMessages = newMessages,
            inputText = newInputText,
            isProgressVisible = true
        )
        
        // Then
        assertEquals(newMessages, updatedState.consoleMessages)
        assertEquals(newInputText, updatedState.inputText)
        assertTrue(updatedState.isProgressVisible)
        // Unchanged fields should remain the same
        assertEquals("", updatedState.inputHint)
        assertTrue(updatedState.isInputEnabled)
        assertFalse(updatedState.isCreateOfferVisible)
    }
}