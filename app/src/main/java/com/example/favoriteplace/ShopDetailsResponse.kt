package com.example.favoriteplace

data class ShopDetailsResponse(
    val saleDeadline: String,
    val category: String,
    val imageUrl: String,
    val userPoint: Int,
    val status: String,
    val name: String,
    val point: Int,
    val description: String,
    val alreadyBought: Boolean
)