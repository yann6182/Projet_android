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
import com.example.projet_android.utils.FavoritesManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritesActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessage: TextView
    private lateinit var clearButton: Button
    private lateinit var adapter: ProductAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var loadingIndicator: RelativeLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        
        recyclerView = findViewById(R.id.recyclerViewFavorites)
        emptyMessage = findViewById(R.id.emptyFavoritesMessage)
        clearButton = findViewById(R.id.buttonClearFavorites)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        loadingIndicator = findViewById(R.id.loadingView)
        
        // Marquer l'élément des favoris comme sélectionné
        bottomNavigation.selectedItemId = R.id.navigation_favorites
        
        setupRecyclerView()
        updateFavorites()
        setupBottomNavigation()
        
        clearButton.setOnClickListener {
            showLoading()
            FavoritesManager.clear()
            updateFavorites()
            hideLoading()
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateFavorites()
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
                R.id.navigation_favorites -> true
                R.id.navigation_cart -> {
                    startActivity(Intent(this, CartActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
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
    
    private fun updateFavorites() {
        showLoading()
        val favorites = FavoritesManager.getItems()
        adapter.updateList(favorites)
        
        // Activer/désactiver le bouton selon l'état des favoris
        clearButton.isEnabled = favorites.isNotEmpty()
        
        if (favorites.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyMessage.visibility = View.GONE
        }
        hideLoading()
    }
    
    // Ajouter animation de retour
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
} 