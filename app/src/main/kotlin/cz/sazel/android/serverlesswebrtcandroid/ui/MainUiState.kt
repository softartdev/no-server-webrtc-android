package cz.sazel.android.serverlesswebrtcandroid.ui

import cz.sazel.android.serverlesswebrtcandroid.webrtc.ServerlessRTCClient

data class MainUiState(
    val consoleMessages: List<String> = emptyList(),
    val inputText: String = "",
    val inputHint: String = "",
    val isInputEnabled: Boolean = true,
    val isProgressVisible: Boolean = false,
    val isCreateOfferVisible: Boolean = false,
    val currentState: ServerlessRTCClient.State = ServerlessRTCClient.State.INITIALIZING
)