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

    val spinnerData: LiveData<List<String>> = Transformations.map(rawAPIData){ currencyResponse ->
        currencyResponse.quotes.toList().map {
            it.first.takeLast(3)
        }
    }

    private val _baseCurrencyRate: MutableLiveData<Double> by lazy { MutableLiveData<Double>(1.0) }
    val baseCurrencyRate: LiveData<Double> = _baseCurrencyRate

    fun setBaseCurrencyRate(spinnerSelectedValue: String) {
        _baseCurrencyRate.value = rawAPIData.value!!.quotes.toList().find { it.first.takeLast(3) == spinnerSelectedValue }!!.second
        Log.i("baseCurrencyRate", _baseCurrencyRate.value.toString())
    }

    val transformedAPIData: MediatorLiveData<List<CurrencyView>> = MediatorLiveData<List<CurrencyView>>()

    /**
     * Get currency information from the currency api retrofit service
     *
     */
    init {
        viewModelScope.launch {
            _rawAPIData.value = CurrencyApi.retrofitService.getCurrencies()
            Log.i("rawApiData", rawAPIData.value.toString())
        }
        transformedAPIData.addSource(rawAPIData){
            transformData(it, amountToConvert.value, baseCurrencyRate.value)
        }
        transformedAPIData.addSource(amountToConvert){
            transformData(rawAPIData.value, it, baseCurrencyRate.value)
        }
        transformedAPIData.addSource(baseCurrencyRate){
            transformData(rawAPIData.value, amountToConvert.value, it)
        }
    }

    private fun transformData(rawAPIData: CurrencyResponse?, amountToConvert: Double?, baseCurrencyRate: Double?) {
        if (rawAPIData == null || amountToConvert == null || baseCurrencyRate == null) return
        transformedAPIData.value = rawAPIData.quotes.toList().map { currencyPair ->
            CurrencyView(
                currencyPair.first.takeLast(3),
                (amountToConvert.times(currencyPair.second).div(baseCurrencyRate)).let { String.format("%.2f", it)
                },
                Currency.getInstance(currencyPair.first.takeLast(3)).displayName
            )
        }
    }

}
