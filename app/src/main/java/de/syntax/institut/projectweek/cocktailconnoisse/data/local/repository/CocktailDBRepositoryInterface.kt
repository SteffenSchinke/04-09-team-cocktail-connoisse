package de.syntax.institut.projectweek.cocktailconnoisse.data.local.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.Flow

interface CocktailDBRepositoryInterface {

    suspend fun insertFavoritedCocktail(favoritedCocktail: Cocktail)

    suspend fun deleteFavoritedCocktail(favoritedCocktail: Cocktail)

    suspend fun getAllFavoritedCocktails(): Flow<List<Cocktail>>
}