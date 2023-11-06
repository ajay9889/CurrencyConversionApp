package com.demo.currencyconversion.koindi
import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.demo.currencyconversion.MainApplication
import com.demo.currencyconversion.database.DatabaseHelper
import com.demo.currencyconversion.repository.CurrencyConversionRepository
import com.demo.currencyconversion.repository.LocalDbRepository
import com.demo.currencyconversion.repositoryimpl.CurrencyConversionRepositoryImpl
import com.demo.currencyconversion.repositoryimpl.LocalDbRepositoryImpl
import com.demo.currencyconversion.service.Constants
import com.demo.currencyconversion.viewmodel.CurrencyViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule= module {
    // Room Db creating signle instance
    single<DatabaseHelper> {DatabaseHelper.getDatabase(androidContext(),  "currency_db")}
    // retrofit client
    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    single<LocalDbRepository> { LocalDbRepositoryImpl(androidContext(),get() as DatabaseHelper)}
    single<CurrencyConversionRepository> { CurrencyConversionRepositoryImpl(
        androidContext() ,
        get(),
        (get() as DatabaseHelper),
        get()
    )
    }
    single{ CurrencyViewModel(get(),get(), androidApplication() as MainApplication) }
}
