package com.rdm.mygas.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GasDao {
    @Query("SELECT * FROM gas ORDER BY description")
    fun getGas(): LiveData<List<Gas>>

    @Query("SELECT * FROM gas WHERE favorite = :favorite ORDER BY description")
    fun getGasFavorite(favorite: Boolean): LiveData<List<Gas>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Gas>)

}