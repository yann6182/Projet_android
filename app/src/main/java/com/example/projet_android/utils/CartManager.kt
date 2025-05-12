package com.example.projet_android.utils

import com.example.projet_android.model.Product

object CartManager {
    private val cartItems = mutableListOf<Product>()

    fun addItem(product: Product) {
        cartItems.add(product)
    }

    fun getItems(): List<Product> = cartItems

    fun clear() {
        cartItems.clear()
    }
}
