package de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto

import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient

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
            Ingredient(
                cocktailId = id.toLong(),
                name = name!!.trim(),
                measure = measure?.trim() ?: ""
            )
        }

    val cocktail = Cocktail(
        id = id.toLong(),
        name = name,
        category = category,
        instructions = instructions,
        imageUrl = imageUrl,
        modifiedAt = modifiedAt
    )
    cocktail.ingredients = ingredients

    return cocktail
}