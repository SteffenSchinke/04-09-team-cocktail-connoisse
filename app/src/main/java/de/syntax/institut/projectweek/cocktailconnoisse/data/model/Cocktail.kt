package de.syntax.institut.projectweek.cocktailconnoisse.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "cocktail")
data class Cocktail(

    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val category: String,
    val instructions: String?,
    val imageUrl: String,
    val modifiedAt: String?,
    var favorited: Boolean = false
) {

    @Ignore
    lateinit var ingredients: List<Ingredient>
}