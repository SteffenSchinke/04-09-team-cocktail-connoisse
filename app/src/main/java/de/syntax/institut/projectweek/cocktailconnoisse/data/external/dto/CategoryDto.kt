package de.syntax.institut.projectweek.cocktailconnoisse.data.external.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list

{"drinks":[
    {"strCategory":"Cocktail"},
    {"strCategory":"Ordinary Drink"},
    {"strCategory":"Punch \/ Party Drink"},
    {"strCategory":"Shake"},
    {"strCategory":"Other \/ Unknown"},
    {"strCategory":"Cocoa"},
    {"strCategory":"Shot"},
    {"strCategory":"Coffee \/ Tea"},
    {"strCategory":"Homemade Liqueur"},
    {"strCategory":"Beer"},
    {"strCategory":"Soft Drink"}
  ]}

 */

@JsonClass(generateAdapter = true)
data class CategoryDto(

    @Json(name = "strCategory") val name: String,
)