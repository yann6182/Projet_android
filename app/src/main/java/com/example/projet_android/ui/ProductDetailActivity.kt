package com.example.projet_android.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.projet_android.R
import com.example.projet_android.model.Product
import com.example.projet_android.utils.CartManager
import com.example.projet_android.utils.FavoritesManager

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var product: Product
    private lateinit var buttonAddToFavorites: Button
    private lateinit var buttonShare: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val productFromIntent = intent.getSerializableExtra("product") as? Product
        if (productFromIntent == null) {
            Toast.makeText(this, "Erreur: Produit non trouvé", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        product = productFromIntent

        val imageView = findViewById<ImageView>(R.id.productImage)
        val titleView = findViewById<TextView>(R.id.productTitle)
        val priceView = findViewById<TextView>(R.id.productPrice)
        val descriptionView = findViewById<TextView>(R.id.productDescription)
        val addButton = findViewById<Button>(R.id.buttonAddToCart)
        val qrButton = findViewById<Button>(R.id.buttonGenerateQr)
        buttonAddToFavorites = findViewById(R.id.buttonAddToFavorites)
        buttonShare = findViewById(R.id.buttonShare)

        titleView.text = product.title
        priceView.text = "${product.price} €"
        descriptionView.text = product.description
        
        Glide.with(this)
            .load(product.image)
            .placeholder(R.drawable.placeholder_image)
            .transition(DrawableTransitionOptions.withCrossFade(300))
            .into(imageView)

        addButton.setOnClickListener {
            CartManager.addItem(product)
            val toast = Toast.makeText(this, "Produit ajouté au panier", Toast.LENGTH_SHORT)
            toast.show()
        }

        qrButton.setOnClickListener {
            val intent = Intent(this, GenerateQrActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        
        updateFavoriteButton()
        
        buttonAddToFavorites.setOnClickListener {
            toggleFavorite()
        }
        
        buttonShare.setOnClickListener {
            shareProduct()
        }
    }
    
    private fun updateFavoriteButton() {
        val isFavorite = FavoritesManager.isProductInFavorites(product)
        if (isFavorite) {
            buttonAddToFavorites.text = "Retirer des favoris"
            buttonAddToFavorites.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.btn_star_big_on, 0, 0, 0
            )
        } else {
            buttonAddToFavorites.text = "Ajouter aux favoris"
            buttonAddToFavorites.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.btn_star_big_off, 0, 0, 0
            )
        }
    }
    
    private fun toggleFavorite() {
        val isFavorite = FavoritesManager.isProductInFavorites(product)
        if (isFavorite) {
            FavoritesManager.removeItem(product)
            Toast.makeText(this, "Produit retiré des favoris", Toast.LENGTH_SHORT).show()
        } else {
            FavoritesManager.addItem(product)
            Toast.makeText(this, "Produit ajouté aux favoris", Toast.LENGTH_SHORT).show()
        }
        updateFavoriteButton()
    }
    
    private fun shareProduct() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        
        val shareMessage = """
            Découvrez ce produit : ${product.title}
            Prix : ${product.price} €
            Description : ${product.description}
            
            Produit partagé depuis l'application E-Commerce
        """.trimIndent()
        
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Partager via"))
    }
    
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
