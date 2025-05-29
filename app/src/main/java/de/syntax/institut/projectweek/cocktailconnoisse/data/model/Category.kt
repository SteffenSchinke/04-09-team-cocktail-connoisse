package de.syntax.institut.projectweek.cocktailconnoisse.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category (

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val imageId: Int,
    @ColumnInfo(name = "createdAt", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String
) {

    fun toUrlArgument(): String {

        return name.replace(" ", "_")
    }
}