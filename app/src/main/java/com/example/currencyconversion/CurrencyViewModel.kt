package com.example.currencyconversion

import android.util.Log
import androidx.lifecycle.*
import com.example.currencyconversion.data.CurrencyDao
import com.example.currencyconversion.data.CurrencyView
import com.example.currencyconversion.network.CurrencyApi
import com.example.currencyconversion.utils.Pref
import kotlinx.coroutines.launch
import java.util.*

class CurrencyViewModel(
    private val currencyDao: CurrencyDao,
    private val pref: Pref
) :
    ViewModel() {


    fun setAmount(amount: String?) {
        _amountToConvert.value = amount?.toDoubleOrNull() ?: 0.0
    }

    private val _amountToConvert: MutableLiveData<Double> by lazy { MutableLiveData<Double>(0.0) }
    private val amountToConvert: LiveData<Double> = _amountToConvert


    private val currencyListDB: LiveData<List<com.example.currencyconversion.data.Currency>> =
        currencyDao.getAllCurrency().asLiveData()

    val spinnerData: LiveData<List<String>> = Transformations.map(currencyListDB) { currencyList ->
        currencyList.map { it.currencyCode }
    }

    private val _baseCurrencyRate: MutableLiveData<Double> by lazy { MutableLiveData<Double>(1.0) }
    private val baseCurrencyRate: LiveData<Double> = _baseCurrencyRate

    fun setBaseCurrencyRate(spinnerSelectedValue: String) {
        _baseCurrencyRate.value = currencyListDB.value?.find {
            it.currencyCode == spinnerSelectedValue
        }?.USDollarRate
        Log.i("baseCurrencyRate", _baseCurrencyRate.value.toString())
    }

    val transformedAPIData: MediatorLiveData<List<CurrencyView>> =
        MediatorLiveData<List<CurrencyView>>()


    init {
        getDataMethod()

        transformedAPIData.addSource(currencyListDB) {
            transformData(it, amountToConvert.value, baseCurrencyRate.value)
        }
        transformedAPIData.addSource(amountToConvert) {
            transformData(currencyListDB.value, it, baseCurrencyRate.value)
        }
        transformedAPIData.addSource(baseCurrencyRate) {
            transformData(currencyListDB.value, amountToConvert.value, it)
        }
    }

    /**
     *  Checks the following conditions
     *
     * App installed and opened for the first time
     *
     * Cache expired i.e more than a day since the database was written to
     *
     */
    private fun getDataMethod() {
        val lastTime = pref.getLastTime()
        when {

            // App installed and opened for the first time
            lastTime == 0L -> {
                // insert api data into db
                viewModelScope.launch {
                    // insert db with api data
                    currencyDao.insertAll(getCurrencyListAPIData())
                }

                // update shared preference key TIME_CREATED to currentTimeMillis
                pref.putLastTime(System.currentTimeMillis())
            }

            // Cache expired i.e more than a day since the database was written to
            System.currentTimeMillis() - lastTime > 86_400_000L -> {

                viewModelScope.launch {
                    // update db with api data
                    currencyDao.update(getCurrencyListAPIData())
                }

                // update shared preference key TIME_CREATED to currentTimeMillis
                pref.putLastTime(System.currentTimeMillis())
            }
        }
    }

    private suspend fun getCurrencyListAPIData(): List<com.example.currencyconversion.data.Currency> {
        // call api - get data
        return CurrencyApi.retrofitService.getCurrencies().quotes.toList().map {
            com.example.currencyconversion.data.Currency(
                currencyCode = it.first.takeLast(
                    3
                ), USDollarRate = it.second
            )
        }
    }

    private fun transformData(
        currencyListDB: List<com.example.currencyconversion.data.Currency>?,
        amountToConvert: Double?,
        baseCurrencyRate: Double?
    ) {
        if (currencyListDB == null || amountToConvert == null || baseCurrencyRate == null) return
        transformedAPIData.value = currencyListDB.map { currency ->
            CurrencyView(
                currency.currencyCode,
                (amountToConvert.times(currency.USDollarRate).div(baseCurrencyRate)).let {
                    String.format("%.2f", it)
                },
                Currency.getInstance(currency.currencyCode).displayName
            )
        }

    }
}

/**
 *  Factory class to instantiate the [ViewModel] instance
 */
class CurrencyViewModelFactory(
    private val currencyDao: CurrencyDao,
    private val pref: Pref
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyViewModel(currencyDao, pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

