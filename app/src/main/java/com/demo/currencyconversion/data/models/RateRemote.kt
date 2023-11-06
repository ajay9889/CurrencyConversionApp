package com.demo.currencyconversion
import com.google.gson.annotations.SerializedName

data class RateRemote(
    @SerializedName("timestamp")
    val timestamp: Long?=0,
    @SerializedName("base")
    val base: String?="USD",
    @SerializedName("rates")
    val rates: Map<String, Float> = emptyMap()
)