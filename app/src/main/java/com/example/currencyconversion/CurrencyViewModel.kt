package com.example.currencyconversion

import android.util.Log
import androidx.lifecycle.*
import com.example.currencyconversion.data.CurrencyView
import com.example.currencyconversion.network.CurrencyApi
import com.example.currencyconversion.network.CurrencyResponse
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    fun setAmount(amount: String?) {
        if (amount != null) {
            _amountToConvert.value = amount.toDouble()
        } else {
            _amountToConvert.value = 1.0
        }
    }

    private val _amountToConvert: MutableLiveData<Double> = MutableLiveData()
    val amountToConvert: LiveData<Double> = _amountToConvert


    private val _rawAPIData: MutableLiveData<CurrencyResponse> = MutableLiveData()
    val rawAPIData: LiveData<CurrencyResponse> = _rawAPIData

    val transformedAPIData: LiveData<List<CurrencyView>> =
        Transformations.switchMap(rawAPIData) { getTransformedData(it) }

    private fun getTransformedData(response: CurrencyResponse): LiveData<List<CurrencyView>> {
        return amountToConvert.map { amount->
            response.quotes.toList().map {
                CurrencyView(
                    it.first.takeLast(3),
                    it.second * amount,
                    java.util.Currency.getInstance(it.first.takeLast(3)).toString()
                )
            }
        }

    }

    /**
     * Get currency information from the currency api retrofit service
     *
     */
    init {
        viewModelScope.launch {
            _rawAPIData.value = CurrencyApi.retrofitService.getCurrencies()
            val logTest= _rawAPIData.value
            Log.i("rawApiData",logTest.toString())
        }

    }
}
