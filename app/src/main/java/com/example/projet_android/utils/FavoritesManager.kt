package com.example.projet_android.utils

import com.example.projet_android.model.Product

object FavoritesManager {
    private val favoriteItems = mutableListOf<Product>()

    fun addItem(product: Product): Boolean {
        if (!isProductInFavorites(product)) {
            favoriteItems.add(product)
            return true
        }
        return false
    }

    fun removeItem(product: Product): Boolean {
        return favoriteItems.remove(product)
    }

    fun getItems(): List<Product> = favoriteItems

    fun clear() {
        favoriteItems.clear()
    }

    fun isProductInFavorites(product: Product): Boolean {
        return favoriteItems.any { it.id == product.id }
    }
} 