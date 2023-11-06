package com.demo.currencyconversion.viewmodel
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.demo.currencyconversion.MainApplication
import com.demo.currencyconversion.R
import com.demo.currencyconversion.Utils.AppUtils
import com.demo.currencyconversion.Utils.ResponseState
import com.demo.currencyconversion.data.Domain.CurrencyDomain
import com.demo.currencyconversion.data.Domain.LatestRateDomain
import com.demo.currencyconversion.data.Domain.toCurrencyDomain
import com.demo.currencyconversion.data.Domain.toCurrencyRateDomain
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain
import com.demo.currencyconversion.repository.CurrencyConversionRepository
import kotlinx.coroutines.launch
class CurrencyViewModel (private val save: SavedStateHandle,val currencyConversionRepository:CurrencyConversionRepository,
                         val application: MainApplication): AndroidViewModel(application) {
    val currencyLiveData = MutableLiveData<ResponseState<CurrencyDomain>>()
    val conversionRateLiveData = MutableLiveData<ResponseState<LatestRateDomain>>()
    val selectedCurrencyRate = MutableLiveData<CurrencyRateDomain>()
    init {
        fetchSelectedCurrencyRate("AED")
    }

    fun getCurrencyList(){
        // fetch currency list from API
        viewModelScope.launch {
            currencyConversionRepository.getCurrencyList().run {
                Log.d("viewModel", "currencyConversionRepository")
                when(this)
                {
                    is ResponseState.Success-> {
                        this.data?.let {
                            it.let {
                                currencyLiveData.value = ResponseState.Success(it)
                            }
                        }
                    }
                    is ResponseState.Error-> {
                        currencyLiveData.value = ResponseState.Error(toCurrencyDomain(this.message, emptyList()))
                    }
                    else -> {

                    }
                }
            }
        }
    }
    fun fetchSelectedCurrencyRate(selectedCurrency: String){
        viewModelScope.launch {
            currencyConversionRepository.getCurrencyRateDomain(selectedCurrency)?.let {
                Log.d("TAG: currencyConversionRepository" , "$it")
                selectedCurrencyRate.value =it
                initFromToConvert("USD")
            }
        }
    }
    fun initFromToConvert(base: String){
        // fetch currency rate list from API
        viewModelScope.launch {
            currencyConversionRepository.getLatestCurrencyRate(base , false, false).let {
               when(it)
                {
                    is ResponseState.Success-> {
                        it.data?.let {
                            conversionRateLiveData.value = ResponseState.Success(it)
                        }
                    }
                    is ResponseState.Error-> {
                        conversionRateLiveData.value = ResponseState.Error(toCurrencyRateDomain(it.message, emptyList()))
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}