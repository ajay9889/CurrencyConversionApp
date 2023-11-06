package com.demo.currencyconversion.service
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyService {
    @GET("/api/currencies.json")
    suspend fun getCurrency(): Response<Map<String, String>>
}