package de.syntax.institut.projectweek.cocktailconnoisse.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "favorited_cocktail")
data class FavoritedCocktail(

    @PrimaryKey(autoGenerate = false)
    val id: String
)