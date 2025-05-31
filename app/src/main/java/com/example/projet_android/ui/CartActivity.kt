package com.example.projet_android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.R
import com.example.projet_android.model.Product
import com.example.projet_android.utils.CartManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartMessage: TextView
    private lateinit var totalTextView: TextView
    private lateinit var clearButton: Button
    private lateinit var checkoutButton: Button
    private lateinit var adapter: ProductAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var loadingIndicator: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerViewCart)
        emptyCartMessage = findViewById(R.id.emptyCartMessage)
        totalTextView = findViewById(R.id.totalPrice)
        clearButton = findViewById(R.id.buttonClearCart)
        checkoutButton = findViewById(R.id.buttonCheckout)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        loadingIndicator = findViewById(R.id.loadingView)

        // Marquer l'élément du panier comme sélectionné
        bottomNavigation.selectedItemId = R.id.navigation_cart

        // Configurer le RecyclerView
        setupRecyclerView()
        
        // Mettre à jour l'affichage du panier
        updateCartDisplay()

        // Configurer les boutons
        clearButton.setOnClickListener {
            showLoading()
            CartManager.clear()
            updateCartDisplay()
            hideLoading()
        }
        
        checkoutButton.setOnClickListener {
            // Simuler un processus de paiement
            showLoading()
            
            // Attendre un peu pour simuler le traitement
            recyclerView.postDelayed({
                CartManager.clear()
                updateCartDisplay()
                hideLoading()
                // Afficher un message de succès
                android.widget.Toast.makeText(
                    this, 
                    "Commande validée avec succès!", 
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }, 1500)
        }
        
        // Configurer la navigation
        setupBottomNavigation()
    }
    
    override fun onResume() {
        super.onResume()
        updateCartDisplay()
    }
    
    private fun setupRecyclerView() {
        adapter = ProductAdapter(listOf()) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                    true
                }
                R.id.navigation_scan -> {
                    startActivity(Intent(this, QrScannerActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                    true
                }
                R.id.navigation_cart -> true
                else -> false
            }
        }
    }
    
    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
    }
    
    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
    }

    private fun updateCartDisplay() {
        val cartItems = CartManager.getItems()
        
        // Mettre à jour la liste des produits dans l'adaptateur
        adapter.updateList(cartItems)
        
        // Mettre à jour le prix total
        val total = cartItems.sumOf { it.price }
        totalTextView.text = "Total : %.2f €".format(total)
        
        // Activer/désactiver les boutons selon l'état du panier
        val isCartEmpty = cartItems.isEmpty()
        clearButton.isEnabled = !isCartEmpty
        checkoutButton.isEnabled = !isCartEmpty
        
        // Afficher/masquer le message de panier vide
        if (isCartEmpty) {
            recyclerView.visibility = View.GONE
            emptyCartMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyCartMessage.visibility = View.GONE
        }
    }
    
    // Ajouter animation de retour
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
