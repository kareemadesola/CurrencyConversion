package com.example.currencyconversion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface CurrencyDao {
    @Query("SELECT * from currency")
    fun getAllCurrency(): Flow<List<Currency>>

    @Query("SELECT * from currency WHERE code = :currencyCode")
    fun getCurrency(currencyCode:String): Flow<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencyList: List<Currency>)


    @Update
    suspend fun update(currency: Currency)

    @Delete
    suspend fun delete(currency: Currency)
}