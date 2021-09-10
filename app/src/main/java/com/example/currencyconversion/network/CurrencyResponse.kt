package com.example.currencyconversion.network

data class CurrencyResponse(
//    val success: Boolean,
//    val terms: String,
//    val privacy: String,
//    val timestamp: Int,
//    val source: String,
    val quotes: Map<String, Double>
)

