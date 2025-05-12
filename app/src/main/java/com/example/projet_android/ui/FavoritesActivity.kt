package com.example.projet_android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.R
import com.example.projet_android.model.Product
import com.example.projet_android.utils.FavoritesManager

class FavoritesActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessage: TextView
    private lateinit var clearButton: Button
    private lateinit var adapter: ProductAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        
        recyclerView = findViewById(R.id.recyclerViewFavorites)
        emptyMessage = findViewById(R.id.emptyFavoritesMessage)
        clearButton = findViewById(R.id.buttonClearFavorites)
        
        setupRecyclerView()
        updateFavorites()
        
        clearButton.setOnClickListener {
            FavoritesManager.clear()
            updateFavorites()
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
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun updateFavorites() {
        val favorites = FavoritesManager.getItems()
        adapter.updateList(favorites)
        
        if (favorites.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyMessage.visibility = View.GONE
        }
    }
} 