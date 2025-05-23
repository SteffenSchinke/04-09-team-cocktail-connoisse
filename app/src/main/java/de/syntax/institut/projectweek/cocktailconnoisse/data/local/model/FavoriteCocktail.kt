package de.syntax.institut.projectweek.cocktailconnoisse.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "favorite_cocktail")
data class FavoriteCocktail(

    @PrimaryKey(autoGenerate = false)
    val id: String
)