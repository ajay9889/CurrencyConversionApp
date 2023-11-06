package com.demo.currencyconversion.repository

import com.demo.currencyconversion.Utils.ResponseState
import com.demo.currencyconversion.data.Domain.CurrencyDomain
import com.demo.currencyconversion.data.Domain.LatestRateDomain
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain

interface LocalDbRepository {
    suspend fun getCurrencyList(): List<Currency>?
    suspend fun getLatestCurrencyRate(): List<CurrencyRateDomain>?
    suspend fun getTimeStamp(): Long?
    suspend fun getselectedCurrencyRate(symbol: String): CurrencyRateDomain?
}