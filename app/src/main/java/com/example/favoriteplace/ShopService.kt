package com.example.favoriteplace

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ShopService {
    @GET("/shop/limited")
    fun getLimitedSales(
        @Header("Authorization") authorization: String
    ): Call<LimitedSalesResponse>
}