package com.example.currencyconversion.data

data class Currency(
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val timestamp: Int,
    val source: String,
    val quotes: Map<String,Double>
)