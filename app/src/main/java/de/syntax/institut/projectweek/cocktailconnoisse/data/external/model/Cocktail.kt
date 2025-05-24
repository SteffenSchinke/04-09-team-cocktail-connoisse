package de.syntax.institut.projectweek.cocktailconnoisse.data.external.model

data class Cocktail(
    val id: String,
    val category: String?,
    val instructions: String?,
    val imageUrl: String?,
    val ingredients: List<Ingredient>
)