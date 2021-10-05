package com.example.currencyconversion.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in a database
 */
@Entity
data class Currency(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "code") val currencyCode: String,
    @ColumnInfo(name = "us_dollar_rate") val USDollarIndex: Double
)