package com.example.currencyconversion.utils

import android.content.Context

class Pref(
    private val ctx: Context
) {

    private val preferences = ctx.getSharedPreferences("appPreference", Context.MODE_PRIVATE)

    fun getLastTime(): Long {
        return preferences.getLong("TIME_CREATED", 0L)
    }

    fun putLastTime(time: Long) {
        preferences.edit().apply {
            putLong("TIME_CREATED", time)
        }.apply()
    }

}