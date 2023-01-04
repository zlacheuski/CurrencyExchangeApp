package com.example.currencyexchangeapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavouriteCurrencyEntity")
data class Rates (
    @PrimaryKey
    @ColumnInfo(name = "rateName")
    val rateName: String,
    @ColumnInfo(name = "rateValue")
    val rateValue: Double
)

