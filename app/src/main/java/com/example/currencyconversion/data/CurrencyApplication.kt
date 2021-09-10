package com.example.currencyconversion.data

import android.app.Application

class CurrencyApplication: Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val database: CurrencyRoomDatabase by lazy { CurrencyRoomDatabase.getDatabase(this) }
}