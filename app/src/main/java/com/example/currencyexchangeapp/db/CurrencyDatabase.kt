package com.example.currencyexchangeapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyexchangeapp.db.dao.IRateDAO
import com.example.currencyexchangeapp.db.entity.Rates

@Database(entities = [Rates::class], version = 5)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun dao(): IRateDAO

    companion object {
        const val DB_NAME = "currency.db"
    }
}