package de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail

import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Cocktail
import kotlinx.coroutines.flow.Flow

interface CocktailRepositoryInterface {

    fun getRandomCocktail(): Flow<Cocktail?>

    fun getCocktails(type : String): Flow<List<Cocktail>>

    fun getCocktailById(id: String): Flow<Cocktail?>
}