package com.example.currencyconversion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency")
    fun getAllCurrency(): Flow<List<Currency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencyList: List<Currency>)

    @Update
    suspend fun update(currency: List<Currency>)
}
