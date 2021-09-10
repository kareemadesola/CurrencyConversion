package com.example.currencyconversion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton INSTANCE object
 */
@Database(entities = [Currency::class], version = 1, exportSchema = false)
abstract class CurrencyRoomDatabase: RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    companion object{
        @Volatile
        private var INSTANCE: CurrencyRoomDatabase? = null

        fun getDatabase(context: Context): CurrencyRoomDatabase{
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyRoomDatabase::class.java,
                    "currency_database"
                )
                // Wipes and rebuilds instead of migrating if no Migration object
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}