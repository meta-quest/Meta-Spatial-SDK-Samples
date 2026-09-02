package com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface MrukDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MrukEntity)

    @Query("SELECT * FROM mruk_objects WHERE id = :id")
    suspend fun getById(id: String): MrukEntity?

    @Query("SELECT * FROM mruk_objects")
    suspend fun getAll(): List<MrukEntity>

    @Delete
    suspend fun delete(entity: MrukEntity)

    @Query("DELETE FROM mruk_objects WHERE id = :id")
    suspend fun deleteById(id: String)
}

