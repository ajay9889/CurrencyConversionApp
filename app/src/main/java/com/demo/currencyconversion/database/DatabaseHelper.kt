package com.demo.currencyconversion.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain
import com.demo.currencyconversion.database.dao.CurrencyQueryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


@Database(entities = [CurrencyRateDomain::class, Currency::class], version = 1, exportSchema = false)
abstract class DatabaseHelper: RoomDatabase() {
    abstract fun currencyQueryDao(): CurrencyQueryDao
    private val dbHelper:DatabaseHelper by inject(DatabaseHelper::class.java)
    override fun clearAllTables() {
        CoroutineScope(Dispatchers.IO) .launch{
            dbHelper.currencyQueryDao().deleteCurrencyRateTable();
            dbHelper.currencyQueryDao().deleteCurrencyTable();
        }
    }


    companion object{
        @Volatile
        private var INSTANCE: DatabaseHelper? = null
        //   get Database instance to perform the Query functions
        open fun getDatabase(context: Context, DataBaseName: String?): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, DatabaseHelper::class.java, DataBaseName)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { INSTANCE = it }
            }
        }



    }
}