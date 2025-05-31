package com.example.projet_android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
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
    private lateinit var loadingIndicator: RelativeLayout
    private lateinit var noResultsText: TextView
    private var allProducts = listOf<Product>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        scanButton = findViewById(R.id.buttonScan)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        loadingIndicator = findViewById(R.id.loadingView)
        noResultsText = findViewById(R.id.noResultsText)

        // Marquer l'élément de recherche comme sélectionné
        bottomNavigation.selectedItemId = R.id.navigation_search

        adapter = ProductAdapter(listOf()) { selectedProduct ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("product", selectedProduct)
            startActivity(intent)
            // Ajouter animation de transition
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        setupBottomNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> true
                R.id.navigation_scan -> {
                    startActivity(Intent(this, QrScannerActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
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
        recyclerView.visibility = View.GONE
        noResultsText.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun fetchProducts() {
        showLoading()
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getAllProducts()
                }
                allProducts = response
                adapter.updateList(response)
                hideLoading()
                checkNoResults(response)
            } catch (e: Exception) {
                e.printStackTrace()
                hideLoading()
                noResultsText.text = "Erreur lors du chargement des produits"
                noResultsText.visibility = View.VISIBLE
            }
        }
    }

    private fun filterProducts(query: String?) {
        val filtered = if (query.isNullOrBlank()) allProducts
        else {
            // Séparer la requête en mots individuels
            val searchTerms = query.split(" ").filter { it.isNotBlank() }
            
            allProducts.filter { product ->
                if (searchTerms.isEmpty()) true
                else searchTerms.all { term ->
                    product.title.contains(term, ignoreCase = true)
                }
            }
        }
        adapter.updateList(filtered)
        checkNoResults(filtered)
    }

    private fun checkNoResults(list: List<Product>) {
        if (list.isEmpty()) {
            noResultsText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            noResultsText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    // Ajouter animation de retour
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
