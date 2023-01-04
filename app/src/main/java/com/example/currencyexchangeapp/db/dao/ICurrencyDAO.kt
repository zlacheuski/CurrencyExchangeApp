package com.example.currencyexchangeapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.currencyexchangeapp.db.entity.Rates
import kotlinx.coroutines.flow.Flow

@Dao
interface ICurrencyDAO {

    @Query("SELECT * FROM FavouriteCurrencyEntity;")
    fun getFavouriteCurrency(): Flow<List<Rates>>

    @Insert(onConflict = REPLACE)
    fun insertFavouriteRate(entity: Rates)

    @Delete
    fun deleteFavouriteCurrency(entity: Rates)
}
