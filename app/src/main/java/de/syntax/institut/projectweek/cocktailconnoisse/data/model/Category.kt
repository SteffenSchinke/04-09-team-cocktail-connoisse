package de.syntax.institut.projectweek.cocktailconnoisse.data.model

data class Category (

    val name: String,
    val imageId: Int,
) {

    fun toUrlArgument(): String {

        return name.replace(" ", "_")
    }
}