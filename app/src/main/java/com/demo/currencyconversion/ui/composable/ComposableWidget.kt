package com.demo.currencyconversion.ui.composable

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.demo.currencyconversion.data.dbtable.Currency
import com.demo.currencyconversion.data.dbtable.CurrencyRateDomain
import com.demo.currencyconversion.viewmodel.CurrencyViewModel

@Composable
fun setCurrencyconversionList(viewModel: CurrencyViewModel, amountentered: MutableState<TextFieldValue>, currencyRateDomain: List<CurrencyRateDomain>){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item(key = "categories_section") {
            val offset = Offset(5.0f, 5.0f)
            Text(
                style = TextStyle(
                    fontSize = 24.sp,
                    shadow = Shadow(
                        color = Color.LightGray, offset = offset, blurRadius = 3f
                    )
                ),
                fontWeight= FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                text = "Currency list:",
                modifier = Modifier.padding(start =8.dp, top = 8.dp)
            )
        }
        gridItems(
            data = currencyRateDomain,
            columnCount = 3,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        ) { itemData, index ->
            val offset = Offset(5.0f, 5.0f)
            Card(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().padding(5.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        style = TextStyle(fontSize = 14.sp),
                        fontWeight= FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        text = currencyRateDomain.get(index).symbol.trim()
                    )
                    Text(
                        style = TextStyle(
                            fontSize = 17.sp,
                            shadow = Shadow(
                                color = Color.LightGray, offset = offset, blurRadius = 3f
                            )
                        ),
                        fontWeight= FontWeight.Bold,
                        color = Color.Red,
                        text = String.format("%.2f",((amountentered.value.text.let {
                            if(it.isBlank())
                                "0"
                            else
                                it.trim()
                        }).toFloat()*(1/currencyRateDomain.get(index).rate!!)*(viewModel.selectedCurrencyRate.value?.rate ?: kotlin.run { 0.0f })).toDouble())
                    )



                }
            }
        }
    }
}


@Composable
fun MyNumberField(viewModel: CurrencyViewModel, amountentered : MutableState<TextFieldValue>) {
    var isErrored = remember { mutableStateOf(false) }
    OutlinedTextField(
        value           = amountentered.value,
        modifier        = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        label = { Text(text = "Enter amount") },
        isError = isErrored.value,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        onValueChange = {
            amountentered.value =it
            if(it.text.isNotEmpty()){
                isErrored.value = false
                viewModel.initFromToConvert("USD")
            }
            else
                isErrored.value = true
        }
    )
}
@Composable
fun SimpleAlertDialog(expanded : MutableState<Boolean>, title: String, message: String) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text ={
            Text(text = message)
        },
        properties = DialogProperties(),
        onDismissRequest = {
            expanded.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                expanded.value = true
            }) {
                Text(text = "OK")
            }
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownCompose(
    viewModel: CurrencyViewModel,
    currencyList: List<Currency>, modifier: Modifier = Modifier,
    expanded : MutableState<Boolean>,
    selectedCurrency : MutableState<Currency?>) {
    Box (modifier = modifier
        .fillMaxWidth()
        .padding(end = 20.dp),
        contentAlignment = Alignment.TopEnd) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = {
                expanded.value =!expanded.value
            }
        ) {
            Log.d("TAG: selectedCurrency","${selectedCurrency.value}")
            TextField(
                value = "${selectedCurrency.value?.symbol} - ${selectedCurrency.value?.name}",
                onValueChange = {
                    viewModel.fetchSelectedCurrencyRate(selectedCurrency.value!!.symbol)
                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                modifier=modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value =false }) {
                currencyList.forEach {item->
                    DropdownMenuItem(
                        text = { Text(text = "${item.symbol} - ${item.name}") },
                        onClick = {
                            selectedCurrency.value = item
                            expanded.value = false
                            viewModel.fetchSelectedCurrencyRate(selectedCurrency.value!!.symbol)
                        }
                    )
                }
            }
        }
    }
}
fun <T> LazyListScope.gridItems(
    data: List<T>,
    columnCount: Int,
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(T, Int) -> Unit,
) {
    val size = data.count()
    val rows = if (size == 0) 0 else 1 + (size - 1) / columnCount
    items(rows, key = { it.hashCode() }) { rowIndex ->
        Row(
            horizontalArrangement = horizontalArrangement,
            modifier = modifier
        ) {
            for (columnIndex in 0 until columnCount) {
                val itemIndex = rowIndex * columnCount + columnIndex
                if (itemIndex < size) {
                    Box(
                        modifier = Modifier.weight(1F, fill = true),
                        propagateMinConstraints = true
                    ) {
                        itemContent(data[itemIndex],itemIndex)
                    }
                } else {
                    Spacer(Modifier.weight(1F, fill = true))
                }
            }
        }
    }
}