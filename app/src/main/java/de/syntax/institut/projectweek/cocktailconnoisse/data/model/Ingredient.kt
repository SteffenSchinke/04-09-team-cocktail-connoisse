package de.syntax.institut.projectweek.cocktailconnoisse.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredient",
    foreignKeys = [
        ForeignKey(
            entity = Cocktail::class,
            parentColumns = ["id"],
            childColumns = ["cocktailId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cocktailId"])]
)
data class Ingredient(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val cocktailId: Long,
    val name: String?,
    val measure: String?
)