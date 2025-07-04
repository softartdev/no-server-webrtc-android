package cz.sazel.android.serverlesswebrtcandroid.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.text.Html
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import cz.sazel.android.serverlesswebrtcandroid.R

@Composable
fun ConsoleItem(
    message: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showQRDialog by remember { mutableStateOf(false) }
    
    SelectionContainer {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable {
                    // Show QR code on click
                    showQRDialog = true
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY).toString(),
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        // Copy to clipboard on long click
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("text", message))
                        Toast.makeText(context, R.string.clipboard_copy, Toast.LENGTH_SHORT).show()
                    },
                fontFamily = FontFamily.Monospace
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