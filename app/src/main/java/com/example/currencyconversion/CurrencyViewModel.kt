package com.example.currencyconversion

import android.util.Log
import androidx.lifecycle.*
import com.example.currencyconversion.data.CurrencyView
import com.example.currencyconversion.network.CurrencyApi
import com.example.currencyconversion.network.CurrencyResponse
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    fun setAmount(amount: String?){
        _amountToConvert.value = amount?.toDoubleOrNull() ?: 0.0
    }

    private val _amountToConvert: MutableLiveData<Double> by lazy { MutableLiveData<Double>(0.0) }
    val amountToConvert: LiveData<Double> = _amountToConvert


    private val _rawAPIData: MutableLiveData<CurrencyResponse> by lazy { MutableLiveData<CurrencyResponse>() }
    val rawAPIData: LiveData<CurrencyResponse> = _rawAPIData

    val transformedAPIData: LiveData<List<CurrencyView>> =
        Transformations.switchMap(rawAPIData) { getTransformedData(it) }

    private fun getTransformedData(response: CurrencyResponse): LiveData<List<CurrencyView>> {
        return amountToConvert.map { amount->
            response.quotes.toList().map { it ->
                CurrencyView(
                    it.first.takeLast(3),
                    (it.second * amount).let { String.format("%.2f", it) },
                    java.util.Currency.getInstance(it.first.takeLast(3)).displayName
                )
            }
        }

    }
    val spinnerData: LiveData<List<String>> = Transformations.map(rawAPIData){ currencyResponse ->
        currencyResponse.quotes.toList().map { it.first.takeLast(3) }
    }

    /**
     * Get currency information from the currency api retrofit service
     *
     */
    init {
        viewModelScope.launch {
            _rawAPIData.value = CurrencyApi.retrofitService.getCurrencies()
//            val logTest= _rawAPIData.value
            Log.i("rawApiData",rawAPIData.value.toString())
//            val test = transformedAPIData.value
//            Log.i("transformedAPIData", test.toString())
        }

    }
}
