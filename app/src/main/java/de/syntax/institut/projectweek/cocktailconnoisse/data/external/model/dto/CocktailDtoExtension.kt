package de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.dto

import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Ingredient

fun CocktailDto.toDomain(): Cocktail {

    val ingredients = listOf(
        ingredient1 to measure1,
        ingredient2 to measure2,
        ingredient3 to measure3,
        ingredient4 to measure4,
        ingredient5 to measure5,
        ingredient6 to measure6,
        ingredient7 to measure7,
        ingredient8 to measure8,
        ingredient9 to measure9,
        ingredient10 to measure10,
        ingredient11 to measure11,
        ingredient12 to measure12,
        ingredient13 to measure13,
        ingredient14 to measure14,
        ingredient15 to measure15
    ).filter { (name, _) -> !name.isNullOrBlank() }
        .map { (name, measure) ->
            Ingredient(name = name!!.trim(), measure = measure?.trim() ?: "")
        }

    return Cocktail(
        id = id,
        category = category,
        instructions = instructions,
        imageUrl = imageUrl,
        ingredients = ingredients
    )
}