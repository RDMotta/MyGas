package com.rdm.mygas.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gas")
data class Gas(
    @PrimaryKey @ColumnInfo(name = "id") val gasId: String,
    val description: String,
    val value: Double,
    val favorite: Boolean
) {
    override fun toString() = description
}
