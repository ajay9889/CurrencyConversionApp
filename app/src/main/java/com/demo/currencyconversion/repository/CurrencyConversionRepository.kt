package com.demo.currencyconversion.repository

import com.demo.currencyconversion.Utils.ResponseState
import com.demo.currencyconversion.data.Domain.CurrencyDomain
import com.demo.currencyconversion.data.Domain.LatestRateDomain
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain

interface CurrencyConversionRepository {
    suspend fun getCurrencyList(): ResponseState<CurrencyDomain>
    suspend fun getLatestCurrencyRate(base: String, show_alternative: Boolean,prettyprint: Boolean): ResponseState<LatestRateDomain>?

    suspend fun getCurrencyRateDomain(symbol: String): CurrencyRateDomain?
}