package com.example.projet_android.model

import java.io.Serializable

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
) : Serializable
