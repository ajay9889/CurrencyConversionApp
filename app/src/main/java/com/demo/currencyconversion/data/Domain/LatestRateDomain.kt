package com.demo.currencyconversion.data.Domain

import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain

data class LatestRateDomain(
    val message: String?="",
    val currencyRateDomain: List<CurrencyRateDomain>?= emptyList(),
)
fun toCurrencyRateDomain(errorMessage: String?="", currencyList: List<CurrencyRateDomain>? =emptyList()): LatestRateDomain = LatestRateDomain(
    message=errorMessage,
    currencyRateDomain = currencyList
)