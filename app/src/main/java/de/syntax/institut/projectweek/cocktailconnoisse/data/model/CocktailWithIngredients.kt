package de.syntax.institut.projectweek.cocktailconnoisse.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CocktailWithIngredients(
    @Embedded val cocktail: Cocktail,
    @Relation(
        parentColumn = "id",
        entityColumn = "cocktailId"
    )
    val ingredients: List<Ingredient>
)
