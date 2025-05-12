package com.example.projet_android.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projet_android.model.Product
import com.example.projet_android.network.RetrofitInstance
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.*

class QrScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startScan()
    }

    private fun startScan() {
        val options = ScanOptions()
        options.setPrompt("Scannez le QRCode d'un article")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Scan annulé", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            val id = result.contents.toIntOrNull()
            if (id != null) {
                fetchProductById(id)
            } else {
                Toast.makeText(this, "QR code invalide", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun fetchProductById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val product = RetrofitInstance.api.getProductById(id)
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@QrScannerActivity, ProductDetailActivity::class.java)
                    intent.putExtra("product", product)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QrScannerActivity, "Produit introuvable", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
