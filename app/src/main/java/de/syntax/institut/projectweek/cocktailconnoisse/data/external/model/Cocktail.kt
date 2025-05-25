package de.syntax.institut.projectweek.cocktailconnoisse.data.external.model

import java.time.LocalDateTime

data class Cocktail(

    val id: String,
    val name: String?,
    val category: String?,
    val instructions: String?,
    val imageUrl: String?,
    val modifiedAt: LocalDateTime?,
    val ingredients: List<Ingredient>
)