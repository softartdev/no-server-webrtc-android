package cz.sazel.android.serverlesswebrtcandroid

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.sazel.android.serverlesswebrtcandroid.ui.MainViewModel
import cz.sazel.android.serverlesswebrtcandroid.ui.screens.MainScreen
import cz.sazel.android.serverlesswebrtcandroid.ui.theme.ServerlessWebRTCTheme
import cz.sazel.android.serverlesswebrtcandroid.webrtc.ServerlessRTCClient

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var retainInstance: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
            )
        )

        // Initialize or restore client
        val retainedClient = lastCustomNonConfigurationInstance as ServerlessRTCClient?
        if (retainedClient == null) {
            viewModel.initializeClient(applicationContext)
        } else {
            viewModel.setRetainedClient(retainedClient)
        }

        // Restore console messages if available
        savedInstanceState?.getStringArrayList("SAVE_LINES")?.let { messages ->
            viewModel.restoreConsoleMessages(messages)
        }

        setContent {
            ServerlessWebRTCTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                MainScreen(
                    modifier = Modifier.imePadding(),
                    uiState = uiState,
                    onInputTextChanged = viewModel::updateInputText,
                    onSendMessage = viewModel::sendMessage,
                    onCreateOffer = viewModel::makeOffer
                )
            }
        }
    }

    override fun onRetainCustomNonConfigurationInstance(): Any? {
        retainInstance = true
        return viewModel.getClient()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val messages = viewModel.uiState.value.consoleMessages
        outState.putStringArrayList("SAVE_LINES", ArrayList(messages))
    }
}
