package com.demo.currencyconversion
import androidx.room.Entity
import androidx.room.Index
import java.io.Serializable

data class CurrencyRemote(
    val currency: Map<String, String>?= emptyMap(),
    val message: String?=""
): Serializable


fun toCurrencyRemote(errorMessage: String?=""): CurrencyRemote = CurrencyRemote(
    message=errorMessage
)
