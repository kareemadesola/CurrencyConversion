package com.example.currencyconversion.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val ACCESS_KEY = "4b9c001f450240487ff5a3a7b71fe9b4"
private const val ENDPOINT = "live"
private const val BASE_URL = "http://api.currencylayer.com/"


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface CurrencyApiService {
    @GET("${ENDPOINT}?access_key=${ACCESS_KEY}")
    suspend fun getCurrencies(): CurrencyResponse
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object CurrencyApi {
    val retrofitService: CurrencyApiService by lazy { retrofit.create(CurrencyApiService::class.java) }
}