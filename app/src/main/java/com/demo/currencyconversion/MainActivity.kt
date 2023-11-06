package com.demo.currencyconversion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demo.currencyconversion.Utils.ResponseState
import com.demo.currencyconversion.data.Domain.CurrencyDomain
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.ui.composable.CustomDropdownCompose
import com.demo.currencyconversion.ui.composable.MyNumberField
import com.demo.currencyconversion.ui.composable.SimpleAlertDialog
import com.demo.currencyconversion.ui.composable.setCurrencyconversionList
import com.demo.currencyconversion.ui.theme.CurrencyConversionTheme
import com.demo.currencyconversion.viewmodel.CurrencyViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.compose.koinViewModel

class MainActivity(): ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConversionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    var expanded  = remember {
                        mutableStateOf(false)
                    }

                    val viewModel: CurrencyViewModel = koinViewModel()
                    val currency = viewModel.currencyLiveData.observeAsState()
                    val currencyConversion = viewModel.conversionRateLiveData.observeAsState()

                    var amountentered = remember { mutableStateOf(TextFieldValue("0"))}
                    var selectedCurrency = remember { mutableStateOf<Currency?>(null) }

                    // API call
                    LaunchedEffect(Dispatchers.IO) {
                        // load currency list
                        viewModel.getCurrencyList()
                    }

                    when(currency.value){
                        is ResponseState.Success->{
                            (currency.value as ResponseState.Success<CurrencyDomain>).data?.currencyList?.let { currencyList->
                                if(currencyList.isNotEmpty()){
                                    // fetch based on USD default currency
                                    if(selectedCurrency.value==null){
                                        currencyList.filter {
                                            it.symbol.equals("USD")
                                        }.single().let {
                                            selectedCurrency.value = currencyList.filter {
                                                it.symbol.equals("USD")
                                            }.single()
                                            selectedCurrency.value?.let{
                                                LaunchedEffect(Dispatchers.IO) {
                                                    // load first currency rate
                                                    viewModel.initFromToConvert(it.symbol)
                                                }
                                            }
                                        }
                                    }
                                   Column(modifier = Modifier.fillMaxSize()) {
                                        // creating the Editfields
                                        MyNumberField(viewModel,amountentered=amountentered)
                                        // creating the dropdown
                                        CustomDropdownCompose(
                                            viewModel,
                                            currencyList,
                                            expanded=expanded,
                                            selectedCurrency=selectedCurrency)
                                        
                                        Spacer(modifier = Modifier.padding(8.dp))
                                        // creating the grid view
                                        currencyConversion.value?.data?.currencyRateDomain?.let {
                                            setCurrencyconversionList(viewModel, amountentered, it)
                                        }
                                    }
                                }
                            }
                        }
                        is ResponseState.Error->{
                            (currency.value as ResponseState.Error<CurrencyDomain>).data?.message?.let {
                                var showErrorState = remember { mutableStateOf(true)}
                                SimpleAlertDialog(expanded=showErrorState , this.resources.getString(R.string.app_name), it)
                            }
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }
}
