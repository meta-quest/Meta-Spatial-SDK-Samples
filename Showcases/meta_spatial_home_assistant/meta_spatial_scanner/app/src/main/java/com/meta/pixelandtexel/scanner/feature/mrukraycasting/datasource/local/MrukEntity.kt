package com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mruk_objects")
data class MrukEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "q_w") val q_w: Float,
    @ColumnInfo(name = "q_x") val q_x: Float,
    @ColumnInfo(name = "q_y") val q_y: Float,
    @ColumnInfo(name = "q_z") val q_z: Float,

    @ColumnInfo(name = "v_x") val v_x: Float,
    @ColumnInfo(name = "v_y") val v_y: Float,
    @ColumnInfo(name = "v_z") val v_z: Float,
)

