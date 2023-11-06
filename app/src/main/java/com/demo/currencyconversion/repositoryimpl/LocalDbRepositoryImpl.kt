package com.demo.currencyconversion.repositoryimpl

import android.content.Context
import com.demo.currencyconversion.Utils.ResponseState
import com.demo.currencyconversion.data.Domain.CurrencyDomain
import com.demo.currencyconversion.data.Domain.LatestRateDomain
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain
import com.demo.currencyconversion.database.DatabaseHelper
import com.demo.currencyconversion.repository.LocalDbRepository

class LocalDbRepositoryImpl(val context:Context,  val dbhelper: DatabaseHelper): LocalDbRepository {
    override suspend fun getCurrencyList(): List<Currency>? {
        return dbhelper.currencyQueryDao().getCurrencyList()
    }

    override suspend fun getLatestCurrencyRate(): List<CurrencyRateDomain>? {
        return dbhelper.currencyQueryDao().getCurrencyRateTableList()
    }

    override suspend fun getTimeStamp(): Long? {
        return dbhelper.currencyQueryDao().getTimeStamp()
    }

    override suspend fun getselectedCurrencyRate(symbol: String): CurrencyRateDomain? {
        return dbhelper.currencyQueryDao().getselectedCurrencyRate(symbol.uppercase())
    }
}