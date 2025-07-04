package cz.sazel.android.serverlesswebrtcandroid.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.sazel.android.serverlesswebrtcandroid.console.IConsole
import cz.sazel.android.serverlesswebrtcandroid.webrtc.ServerlessRTCClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

data class MainUiState(
    val consoleMessages: List<String> = emptyList(),
    val inputText: String = "",
    val inputHint: String = "",
    val isInputEnabled: Boolean = true,
    val isProgressVisible: Boolean = false,
    val isCreateOfferVisible: Boolean = false,
    val currentState: ServerlessRTCClient.State = ServerlessRTCClient.State.INITIALIZING
)

class MainViewModel(application: Application) : AndroidViewModel(application), 
    ServerlessRTCClient.IStateChangeListener, IConsole {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private lateinit var client: ServerlessRTCClient
    private val consoleMessages = mutableListOf<String>()
    
    fun initializeClient(context: Context) {
        if (!::client.isInitialized) {
            client = ServerlessRTCClient(this, context, this)
            try {
                client.init()
            } catch (e: Exception) {
                printf("Error initializing client: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun setRetainedClient(retainedClient: ServerlessRTCClient) {
        client = retainedClient
        onStateChanged(client.state)
    }
    
    fun getClient(): ServerlessRTCClient = client
    
    fun sendMessage(text: String) {
        val trimmedText = text.trim()
        when (client.state) {
            ServerlessRTCClient.State.WAITING_FOR_OFFER -> client.processOffer(trimmedText)
            ServerlessRTCClient.State.WAITING_FOR_ANSWER -> client.processAnswer(trimmedText)
            ServerlessRTCClient.State.CHAT_ESTABLISHED -> {
                if (trimmedText.isNotBlank()) {
                    client.sendMessage(trimmedText)
                    printf("&gt;$trimmedText")
                }
            }
            else -> if (trimmedText.isNotBlank()) printf(trimmedText)
        }
        updateInputText("")
    }
    
    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }
    
    fun makeOffer() {
        client.makeOffer()
    }
    
    fun waitForOffer() {
        client.waitForOffer()
    }
    
    override fun onStateChanged(state: ServerlessRTCClient.State) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val context = getApplication<Application>()
            val hint = when (state) {
                ServerlessRTCClient.State.WAITING_FOR_OFFER -> context.getString(cz.sazel.android.serverlesswebrtcandroid.R.string.hint_paste_offer)
                ServerlessRTCClient.State.WAITING_FOR_ANSWER -> context.getString(cz.sazel.android.serverlesswebrtcandroid.R.string.hint_paste_answer)
                ServerlessRTCClient.State.CHAT_ESTABLISHED -> context.getString(cz.sazel.android.serverlesswebrtcandroid.R.string.enter_message)
                ServerlessRTCClient.State.WAITING_TO_CONNECT,
                ServerlessRTCClient.State.CREATING_OFFER,
                ServerlessRTCClient.State.CREATING_ANSWER -> state.name
                else -> ""
            }
            
            val isInputEnabled = when (state) {
                ServerlessRTCClient.State.WAITING_TO_CONNECT,
                ServerlessRTCClient.State.CREATING_OFFER,
                ServerlessRTCClient.State.CREATING_ANSWER -> false
                else -> true
            }
            
            val isProgressVisible = when (state) {
                ServerlessRTCClient.State.WAITING_TO_CONNECT,
                ServerlessRTCClient.State.CREATING_OFFER,
                ServerlessRTCClient.State.CREATING_ANSWER -> true
                else -> false
            }
            
            val isCreateOfferVisible = state == ServerlessRTCClient.State.WAITING_FOR_OFFER
            
            if (state == ServerlessRTCClient.State.CHAT_ENDED || 
                state == ServerlessRTCClient.State.INITIALIZING) {
                client.waitForOffer()
            }
            
            _uiState.value = currentState.copy(
                inputHint = hint,
                isInputEnabled = isInputEnabled,
                isProgressVisible = isProgressVisible,
                isCreateOfferVisible = isCreateOfferVisible,
                currentState = state
            )
        }
    }
    
    override fun printf(text: String, vararg args: Any) {
        viewModelScope.launch {
            val formattedText = String.format(Locale.getDefault(), text, *args)
            consoleMessages.add(formattedText)
            _uiState.value = _uiState.value.copy(
                consoleMessages = consoleMessages.toList()
            )
        }
    }
    
    override fun printf(resId: Int, vararg args: Any) {
        printf(getApplication<Application>().getString(resId, *args))
    }
    
    fun restoreConsoleMessages(messages: List<String>) {
        consoleMessages.clear()
        consoleMessages.addAll(messages)
        _uiState.value = _uiState.value.copy(
            consoleMessages = consoleMessages.toList()
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        if (::client.isInitialized) {
            client.destroy()
        }
    }
}