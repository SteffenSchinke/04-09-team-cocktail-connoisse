package de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ResponseCategoryDto (

    @Json(name = "drinks")
    val category: List<CategoryDto>?
)