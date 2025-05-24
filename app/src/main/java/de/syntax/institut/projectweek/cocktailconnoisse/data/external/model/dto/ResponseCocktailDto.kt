package de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseCocktailDto(

    @Json(name = "drinks")
    val cocktails: List<CocktailDto>?
)