package com.demo.currencyconversion.data.Domain

import com.demo.currencyconversion.data.dbtable.Currency

data class CurrencyDomain(
    val message: String?="",
    val currencyList: List<Currency>?= emptyList(),
)
fun toCurrencyDomain(errorMessage: String?="", currencyList: List<Currency>? =emptyList()): CurrencyDomain = CurrencyDomain(
    message=errorMessage,
    currencyList = currencyList
)