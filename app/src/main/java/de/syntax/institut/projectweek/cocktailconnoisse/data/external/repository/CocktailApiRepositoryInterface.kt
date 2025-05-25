package de.syntax.institut.projectweek.cocktailconnoisse.data.external.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.Flow

interface CocktailApiRepositoryInterface {

    fun getRandomCocktail(): Flow<Cocktail?>

    fun getCocktailsByType(type : String): Flow<List<Cocktail>>

    fun getCocktailById(id: String): Flow<Cocktail?>

    fun getCocktailsByName(name: String): Flow<List<Cocktail>>
}