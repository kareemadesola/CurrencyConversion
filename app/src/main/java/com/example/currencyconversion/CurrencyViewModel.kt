package com.example.currencyconversion

import androidx.lifecycle.*
import com.example.currencyconversion.data.Currency
import com.example.currencyconversion.data.CurrencyDao
import com.example.currencyconversion.data.CurrencyView
import com.example.currencyconversion.network.CurrencyApi
import kotlinx.coroutines.launch

class CurrencyViewModel(private val currencyDao: CurrencyDao) : ViewModel() {
    private val _amountToConvert: MutableLiveData<Double> = MutableLiveData()
    val amountToConvert: LiveData<Double> = _amountToConvert


    private val _allCurrency: MutableLiveData<List<Currency>> = MutableLiveData()
    val allCurrency: LiveData<List<Currency>> = _allCurrency


    val currencyView: LiveData<List<CurrencyView>> =
        Transformations.map(allCurrency) { currencyList ->
            currencyList.map {
                CurrencyView(
                    it.currencyCode,
                    it.USDollarIndex * amountToConvert.value!!,
                    java.util.Currency.getInstance(it.currencyCode).toString()
                )
            }
        }

    /**
     * Get currency information from the currency api retrofit service
     *
     */
    init {
        viewModelScope.launch {
            val currencyList = CurrencyApi.retrofitService.getCurrencies().quotes.toList().map {
                Currency(currencyCode = it.first.takeLast(3), USDollarIndex = it.second)
            }
            // save api data into database
            currencyDao.insertAll(currencyList)

            // get data from database and assign
            _allCurrency.value = currencyDao.getAllCurrency().asLiveData().value
        }
    }




    fun setAmount(inputAmount: String?) {
        if (inputAmount != null) {
            _amountToConvert.value = inputAmount.toDouble()
        }
    }

}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class CurrencyViewModelFactory(private val currencyDao: CurrencyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyViewModel(currencyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}