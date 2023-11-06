package com.demo.currencyconversion.service

import com.demo.currencyconversion.RateRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRateService {
    @GET("/api/latest.json?app_id="+Constants.API_ID)
    suspend fun getCurrencyRate(
        @Query("base") base: String,
        @Query("show_alternative") show_alternative: Boolean?=false,
        @Query("prettyprint") prettyprint: Boolean?=false
        ): Response<RateRemote>
}