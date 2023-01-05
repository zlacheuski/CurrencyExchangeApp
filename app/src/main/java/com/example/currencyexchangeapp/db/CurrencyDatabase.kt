package com.example.currencyexchangeapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyexchangeapp.db.dao.ICurrencyDAO
import com.example.currencyexchangeapp.db.entity.Rates

@Database(entities = [Rates::class], version = 3)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun vocDao(): ICurrencyDAO

    companion object {
        const val DB_NAME = "currency.db"
    }
}