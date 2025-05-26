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
                "Cocktail" -> R.drawable.ic_launcher_background
                "Ordinary Drink" -> R.drawable.ic_launcher_background
                "Punch / Party Drink" -> R.drawable.ic_launcher_background
                "Shake" -> R.drawable.ic_launcher_background
                "Other / Unknown" -> R.drawable.ic_launcher_background
                "Cocoa" -> R.drawable.ic_launcher_background
                "Shot" -> R.drawable.ic_launcher_background
                "Coffee / Tea" -> R.drawable.ic_launcher_background
                "Homemade Liqueur" -> R.drawable.ic_launcher_background
                "Beer" -> R.drawable.ic_launcher_background
                "Soft Drink" -> R.drawable.ic_launcher_background
                else -> throw ApiError(
                    type = ApiErrorType.CATEGORY_FAILED,
                    responseCode = null,
                    innerMessage = "Unknown category: $name"
                )
            }
    )
}