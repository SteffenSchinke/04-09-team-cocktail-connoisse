package de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.extension

import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.RepositoryErrorType
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto.CategoryDto
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun CategoryDto.toDomain(): Category {

    val created = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    return Category(
        id = 0,
        name = name,
        createdAt = created,
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
                else -> throw RepositoryError(
                    type = RepositoryErrorType.CATEGORY_FAILED,
                    responseCode = null,
                    innerMessage = "Unknown category: $name"
                )
            }
    )
}