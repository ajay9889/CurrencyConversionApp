package com.demo.currencyconversion.repositoryimpl

import android.content.Context
import android.util.Log
import com.demo.currencyconversion.R
import com.demo.currencyconversion.Utils.AppUtils
import com.demo.currencyconversion.Utils.ResponseState
import com.demo.currencyconversion.data.Domain.CurrencyDomain
import com.demo.currencyconversion.data.Domain.LatestRateDomain
import com.demo.currencyconversion.data.Domain.toCurrencyDomain
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain
import com.demo.currencyconversion.database.DatabaseHelper
import com.demo.currencyconversion.repository.CurrencyConversionRepository
import com.demo.currencyconversion.repository.LocalDbRepository
import com.demo.currencyconversion.service.CurrencyRateService
import com.demo.currencyconversion.service.CurrencyService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit


class CurrencyConversionRepositoryImpl (val context: Context,
                                        val retrofit: Retrofit,
                                        val dbhelper:DatabaseHelper,
                                        val localDbRepository: LocalDbRepository
): CurrencyConversionRepository {
    val tag="CurrencyConversionRepositoryImpl"
    override suspend fun getCurrencyList(): ResponseState<CurrencyDomain> {
        try {
            // check network connections
            if(AppUtils().isDeviceOnline(context)){
                val s = retrofit.create<CurrencyService>()
                s.getCurrency().let {
                    if (it.isSuccessful) {
                        it.body()?.let { it ->
                            var mutableList = mutableListOf<Currency>()
                            it.forEach { symbol, name ->
                                // insert all entries in DB
                                Currency(symbol = symbol.uppercase(), name = name)?.let { curency ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        dbhelper.currencyQueryDao().insertCurrency(curency)
                                    }
                                    mutableList.add(curency)
                                }

                            }
                            return ResponseState.Success(CurrencyDomain(currencyList = mutableList))
                        }
                    } else {
                        it.errorBody()?.let {
                            localDbRepository.getCurrencyList()?.let {
                                if (it.isNotEmpty()) {
                                    return ResponseState.Success(CurrencyDomain(currencyList = it))
                                }
                            } ?: kotlin.run {
                                it.string().let {
                                    JSONObject(it).let {
                                        if (it.has("description"))
                                            return ResponseState.Error(
                                                toCurrencyDomain(
                                                    it.getString(
                                                        "description"
                                                    )
                                                )
                                            )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (e:Exception){e.printStackTrace()}

        localDbRepository.getCurrencyList()?.let {
            if(it.isNotEmpty()){
                return ResponseState.Success(CurrencyDomain(currencyList = it))
            }
        }
        return ResponseState.Error(toCurrencyDomain(context.resources.getString(R.string.network_error)))
    }

    override suspend fun getLatestCurrencyRate(
        base: String,
        show_alternative: Boolean,
        prettyprint: Boolean
    ): ResponseState<LatestRateDomain> {
        try {
            // lookup api to get the data every 30 minutes
            val currentTimeInMillis: Long = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
            val oldTimeInMillis: Long = localDbRepository.getTimeStamp()?:run{0L}
            val isrequestfromAPI =
                oldTimeInMillis == 0L || (currentTimeInMillis - oldTimeInMillis)/60 > 30
            Log.d(tag, "$currentTimeInMillis  , $oldTimeInMillis  , diff: ${(currentTimeInMillis - oldTimeInMillis)/60}")
            if(isrequestfromAPI) {
                // check network connections
                if(AppUtils().isDeviceOnline(context)) {
                    val s = retrofit.create<CurrencyRateService>()
                    s.getCurrencyRate(base, show_alternative, prettyprint).let {
                        if (it.isSuccessful) {
                            it.body()?.let {
                                var mutableList = mutableListOf<CurrencyRateDomain>()
                                // insert all entries in DB for offline
                                it.rates.forEach { symbol, rate ->
                                    CurrencyRateDomain(
                                        base = base,
                                        timestamp = currentTimeInMillis,
                                        symbol = symbol.uppercase(),
                                        rate = rate
                                    )?.let {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            dbhelper.currencyQueryDao().insertCurrencyRateDomain(it)
                                        }
                                        mutableList.add(it)
                                    }
                                }
                                return ResponseState.Success(LatestRateDomain(currencyRateDomain = mutableList.toList()))
                            }
                        } else {
                            it.errorBody()?.let {
                                localDbRepository.getLatestCurrencyRate()?.let {
                                    if (it.isNotEmpty()) {
                                        return ResponseState.Success(
                                            LatestRateDomain(
                                                currencyRateDomain = it
                                            )
                                        )
                                    }
                                } ?: kotlin.run {
                                    it.string().let {
                                        JSONObject(it).let {
                                            if (it.has("description"))
                                                return ResponseState.Error(
                                                    LatestRateDomain(
                                                        it.getString(
                                                            "description"
                                                        )
                                                    )
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Offline Handling
        localDbRepository.getLatestCurrencyRate()?.let {
            if(it.isNotEmpty()){
                return ResponseState.Success(LatestRateDomain(currencyRateDomain = it))
            }
        }
        return ResponseState.Error(LatestRateDomain(message = context.resources.getString(R.string.unknown_error)))
    }

    override suspend fun getCurrencyRateDomain(symbol: String): CurrencyRateDomain? {
        return localDbRepository.getselectedCurrencyRate(symbol)
    }
}