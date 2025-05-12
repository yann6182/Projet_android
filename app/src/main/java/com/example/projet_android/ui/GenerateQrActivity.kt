package com.example.projet_android.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projet_android.R
import com.example.projet_android.model.Product
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class GenerateQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr)

        val imageView = findViewById<ImageView>(R.id.qrImageView)
        val product = intent.getSerializableExtra("product") as? Product

        if (product != null) {
            val qrBitmap = generateQRCode(product.id.toString())
            if (qrBitmap != null) {
                imageView.setImageBitmap(qrBitmap)
            } else {
                Toast.makeText(this, "Erreur de génération du QRCode", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Aucun produit fourni", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        return try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix = multiFormatWriter.encode(
                text,
                BarcodeFormat.QR_CODE,
                512,
                512
            )
            
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            null
        }
    }
}
