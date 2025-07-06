package cz.sazel.android.serverlesswebrtcandroid.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import android.text.Html
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun ConsoleItem(
    message: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showQRDialog by remember { mutableStateOf(false) }
    val annotatedText = remember(message) { parseHtmlToAnnotatedString(message) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        SelectionContainer {
            Text(
                text = annotatedText,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        // Show QR code on click
                        showQRDialog = true
                    },
                fontFamily = FontFamily.Monospace,
                onTextLayout = { _ ->
                    // Handle long click for copy to clipboard
                    // This is handled through SelectionContainer which allows text selection
                }
            )
        }
    }
    
    if (showQRDialog) {
        QRCodeDialog(
            text = message,
            onDismiss = { showQRDialog = false }
        )
    }
}

@Composable
fun QRCodeDialog(
    text: String,
    onDismiss: () -> Unit
) {
    val qrBitmap = remember(text) {
        generateQRCode(text)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Encrypted QR Code",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                qrBitmap?.let { bitmap ->
                    androidx.compose.foundation.Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(300.dp)
                            .padding(bottom = 16.dp)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

private fun generateQRCode(text: String): Bitmap? {
    return try {
        val (width: Int, height: Int) = 1024 to 1024
        val bitMatrix: BitMatrix = QRCodeWriter()
            .encode(text, BarcodeFormat.QR_CODE, width, height)
        val bmp: Bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bmp
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Parse HTML formatted text and convert to AnnotatedString to preserve colors.
 * Handles <font color="...">text</font> tags used by the console.
 */
private fun parseHtmlToAnnotatedString(htmlText: String): AnnotatedString {
    return buildAnnotatedString {
        val cleanText = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString()
        
        // Check for known color patterns used in console
        when {
            htmlText.contains("<font color=\"#673AB7\">") -> {
                // Debug/purple color
                withStyle(SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF673AB7))) {
                    append(cleanText)
                }
            }
            htmlText.contains("<font color=\"#009900\">") -> {
                // Success/green color  
                withStyle(SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF009900))) {
                    append(cleanText)
                }
            }
            htmlText.contains("<font color=\"#000099\">") -> {
                // Info/blue color
                withStyle(SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF000099))) {
                    append(cleanText)
                }
            }
            htmlText.contains("<font color=\"#990000\">") -> {
                // Error/red color
                withStyle(SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF990000))) {
                    append(cleanText)
                }
            }
            else -> {
                // Default color - no special formatting
                append(cleanText)
            }
        }
    }
}