package com.example.currencyconversion

import android.util.Log
import androidx.lifecycle.*
import com.example.currencyconversion.data.CurrencyView
import com.example.currencyconversion.network.CurrencyApi
import com.example.currencyconversion.network.CurrencyResponse
import kotlinx.coroutines.launch
import java.util.*

class CurrencyViewModel : ViewModel() {
    fun setAmount(amount: String?) {
        _amountToConvert.value = amount?.toDoubleOrNull() ?: 0.0
    }

    private val _amountToConvert: MutableLiveData<Double> by lazy { MutableLiveData<Double>(0.0) }
    val amountToConvert: LiveData<Double> = _amountToConvert


    private val _rawAPIData: MutableLiveData<CurrencyResponse> by lazy { MutableLiveData<CurrencyResponse>() }
    private val rawAPIData: LiveData<CurrencyResponse> = _rawAPIData

    val transformedAPIData: LiveData<List<CurrencyView>> = Transformations.switchMap(rawAPIData) { getTransformedData(it) }

    private fun getTransformedData(response: CurrencyResponse): LiveData<List<CurrencyView>> {
        return amountToConvert.map { amount ->
            response.quotes.toList().map { currencyPair ->
                CurrencyView(
                    currencyPair.first.takeLast(3),
                    (amount.times(currencyPair.second)).let { String.format("%.2f", it) },
                    Currency.getInstance(currencyPair.first.takeLast(3)).displayName
                )
            }
        }
    }


    val spinnerData: LiveData<List<String>> = Transformations.map(rawAPIData){ currencyResponse ->
        currencyResponse.quotes.toList().map {
            it.first.takeLast(3)
        }
    }

    private val _baseCurrencyRate: MutableLiveData<Double> by lazy { MutableLiveData<Double>(1.0) }
    val baseCurrencyRate: LiveData<Double> = _baseCurrencyRate

    fun setBaseCurrencyRate(spinnerSelectedValue: String) {
        _baseCurrencyRate.value = Transformations.map(rawAPIData){ currencyResponse ->
            currencyResponse.quotes.toList().find { it.first.takeLast(3) == spinnerSelectedValue }
        }.map { it?.second }.value
//        val currency =rawAPIData.map { it.quotes.toList().map { it. } }
//        val currencyAmount = currency?.convertedAmount
        Log.i("transformedAPIData", transformedAPIData.value.toString())
        Log.i("baseCurrencyRate", _baseCurrencyRate.value.toString())
    }

    /**
     * Get currency information from the currency api retrofit service
     *
     */
    init {
        viewModelScope.launch {
            _rawAPIData.value = CurrencyApi.retrofitService.getCurrencies()
            Log.i("rawApiData", rawAPIData.value.toString())
        }

    }
}
