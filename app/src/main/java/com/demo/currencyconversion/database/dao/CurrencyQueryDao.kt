package com.demo.currencyconversion.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain

@Dao
interface CurrencyQueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyAll(currency: List<Currency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: Currency): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRateDomainAll(currency: List<CurrencyRateDomain>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRateDomain(currency: CurrencyRateDomain): Long

    @Query("DELETE FROM Currency")
    suspend fun deleteCurrencyTable()

    @Query("DELETE FROM CurrencyRateDomain")
    suspend fun deleteCurrencyRateTable()

    @Query("SELECT * FROM Currency  ORDER By symbol")
    suspend fun getCurrencyList(): List<Currency>?

    @Query("SELECT * FROM CurrencyRateDomain ORDER By symbol")
    suspend fun getCurrencyRateTableList(): List<CurrencyRateDomain>?

    @Query("SELECT timestamp FROM CurrencyRateDomain")
    suspend fun getTimeStamp(): Long?

    @Query("SELECT * FROM CurrencyRateDomain WHERE symbol=:symbol")
    suspend fun getselectedCurrencyRate(symbol: String): CurrencyRateDomain?

}