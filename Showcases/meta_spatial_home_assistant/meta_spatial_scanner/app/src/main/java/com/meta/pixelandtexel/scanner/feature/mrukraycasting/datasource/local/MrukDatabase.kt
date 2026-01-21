package com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MrukEntity::class], version = 1)
abstract class MrukDatabase : RoomDatabase() {
    abstract fun mrukDao(): MrukDao
}

