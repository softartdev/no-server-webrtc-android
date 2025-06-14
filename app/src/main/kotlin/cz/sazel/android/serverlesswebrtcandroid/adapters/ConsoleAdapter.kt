package cz.sazel.android.serverlesswebrtcandroid.adapters

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.recyclerview.widget.RecyclerView
import cz.sazel.android.serverlesswebrtcandroid.R
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

/**
 * This is just to do the printing into the RecyclerView.
 */
class ConsoleAdapter(val items: List<String>) : RecyclerView.Adapter<ConsoleAdapter.ConsoleVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.l_item, parent, false)
        return ConsoleVH(view)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ConsoleVH, position: Int) {
        holder.tvText.text = Html.fromHtml(items[position])
    }

    class ConsoleVH(view: View) : RecyclerView.ViewHolder(view) {
        var tvText: TextView = view.findViewById(R.id.tvText)
        init {
            tvText.setOnLongClickListener {
                val clipboard = tvText.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("text", tvText.text.toString()))
                Toast.makeText(tvText.context, R.string.clipboard_copy, LENGTH_SHORT).show()
                true
            }
            tvText.setOnClickListener {
                val (width: Int, height: Int) = 1024 to 1024
                val bitMatrix: BitMatrix = QRCodeWriter()
                    .encode(tvText.text.toString(), BarcodeFormat.QR_CODE, width, height)
                val bmp: Bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    }
                }
                val imageView = ImageView(tvText.context).apply { setImageBitmap(bmp) }
                val dialog = AlertDialog.Builder(tvText.context)
                    .setTitle("Encrypted QR Code")
                    .setView(imageView)
                    .setPositiveButton("Close", null)
                    .create()
                dialog.show()
            }
        }
    }
}
