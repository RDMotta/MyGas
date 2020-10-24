package com.rdm.mygas.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GasDao {
    @Query("SELECT * FROM gas ORDER BY description")
    fun getGas(): LiveData<List<Gas>>

    @Query("SELECT * from gas ORDER BY description")
    fun getGasFlow(): Flow<List<Gas>>

    @Query("SELECT * FROM gas WHERE favorite = :favorite ORDER BY description")
    fun getGasFavoriteFlow(favorite: Boolean): Flow<List<Gas>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Gas>)

}