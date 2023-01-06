package com.example.currencyexchangeapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.currencyexchangeapp.db.entity.Rates
import kotlinx.coroutines.flow.Flow

@Dao
interface ICurrencyDAO {

    @Query("SELECT * FROM Rates ORDER BY isLiked DESC;")
    fun getRates(): Flow<List<Rates>>

    @Query("SELECT * FROM Rates ORDER BY isLiked DESC;")
    fun getRatesNotFlow(): List<Rates>

    @Query("UPDATE Rates SET isLiked =:isLiked WHERE rateName=:rateName")
    fun updateRateState(rateName: String, isLiked: Boolean)

    @Query("SELECT * FROM Rates WHERE rateName LIKE '%'||:searchQuery||'%' ORDER BY isLiked DESC;")
    fun getRatesByName(searchQuery: String): List<Rates>

    @Insert(onConflict = REPLACE)
    fun insertRate(ratesList: List<Rates>)

    @Update()
    fun updateRates(ratesList: List<Rates>)
}
