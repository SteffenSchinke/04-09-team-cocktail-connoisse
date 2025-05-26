package de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto

import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category

fun CategoryDto.toDomain(): Category {


    return Category("", 0)
}