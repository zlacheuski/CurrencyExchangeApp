package com.example.currencyexchangeapp.utils

interface IRateAdapter {
    fun updateRateState(rateName: String, isLiked: Boolean)
    fun addToSharedPreferences(rateName: String, rateValue: Double)
    fun getFromSharedPreferences(value: String): String?
    fun getFloatFromSharedPreferences(value: String): Float
}