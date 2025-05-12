package com.example.projet_android.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android.R
import com.example.projet_android.model.Product
import com.example.projet_android.utils.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var clearButton: Button
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerViewCart)
        totalTextView = findViewById(R.id.totalPrice)
        clearButton = findViewById(R.id.buttonClearCart)

        val cartItems: List<Product> = CartManager.getItems()
        adapter = ProductAdapter(cartItems) {}

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateTotal()

        clearButton.setOnClickListener {
            CartManager.clear()
            adapter.updateList(listOf())
            updateTotal()
        }
    }

    private fun updateTotal() {
        val total = CartManager.getItems().sumOf { it.price }
        totalTextView.text = "Total : %.2f €".format(total)
    }
}
