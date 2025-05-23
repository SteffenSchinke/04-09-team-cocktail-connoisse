package de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail

import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Cocktail
import kotlinx.coroutines.flow.Flow

interface CocktailRepositoryInterface {

    suspend fun getCocktails(): Flow<List<Cocktail>>

}