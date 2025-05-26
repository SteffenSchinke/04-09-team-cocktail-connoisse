package de.syntax.institut.projectweek.cocktailconnoisse.data.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.Flow

interface CocktailRepositoryInterface {

    fun getRandomCocktail(): Flow<Cocktail?>

    fun getCocktailsByType(type : String): Flow<List<Cocktail>>

    fun getCocktailById(id: String): Flow<Cocktail?>

    fun getCocktailsByName(name: String): Flow<List<Cocktail>>

    fun getAllCategories(): Flow<List<Category>>

    fun insertFavoritedCocktail(favoritedCocktail: Cocktail)

    fun deleteFavoritedCocktail(favoritedCocktail: Cocktail)

    fun getAllFavoritedCocktails(): Flow<List<Cocktail>>
}