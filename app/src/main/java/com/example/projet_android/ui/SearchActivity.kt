package com.example.projet_android.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.R
import com.example.projet_android.model.Product
import com.example.projet_android.network.RetrofitInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var scanButton: Button
    private lateinit var adapter: ProductAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private var allProducts = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        scanButton = findViewById(R.id.buttonScan)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Marquer l'élément de recherche comme sélectionné
        bottomNavigation.selectedItemId = R.id.navigation_search

        adapter = ProductAdapter(listOf()) { selectedProduct ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("product", selectedProduct)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchProducts()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })

        scanButton.setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> true
                R.id.navigation_scan -> {
                    startActivity(Intent(this, QrScannerActivity::class.java))
                    false
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    false
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun fetchProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getAllProducts()
                withContext(Dispatchers.Main) {
                    allProducts = response
                    adapter.updateList(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterProducts(query: String?) {
        val filtered = if (query.isNullOrBlank()) allProducts
        else allProducts.filter {
            it.title.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }
}
