package de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto

import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category

fun CategoryDto.toDomain(): Category {

    return Category(
        name = name,
        imageId =
            when (name) {
                "Cocktail" -> R.drawable.category_cocktail
                "Ordinary Drink" -> R.drawable.category_ordinary_drink
                "Punch / Party Drink" -> R.drawable.category_punch_patry_drink
                "Shake" -> R.drawable.category_shake
                "Other / Unknown" -> R.drawable.category_other_unknown
                "Cocoa" -> R.drawable.category_cocoa
                "Shot" -> R.drawable.category_shot
                "Coffee / Tea" -> R.drawable.category_coffee_tea
                "Homemade Liqueur" -> R.drawable.category_homemade_liqueur
                "Beer" -> R.drawable.category_beer
                "Soft Drink" -> R.drawable.category_soft_drink
                else -> throw ApiError(
                    type = ApiErrorType.CATEGORY_FAILED,
                    responseCode = null,
                    innerMessage = "Unknown category: $name"
                )
            }
    )
}