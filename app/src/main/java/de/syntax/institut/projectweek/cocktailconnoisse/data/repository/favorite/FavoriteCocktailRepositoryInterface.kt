package de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite

import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoriteCocktail
import kotlinx.coroutines.flow.Flow

interface FavoriteCocktailRepositoryInterface {

    suspend fun insertFavoriteCocktail(favoriteCocktail: FavoriteCocktail)

    suspend fun deleteFavoriteCocktail(favoriteMeal: FavoriteCocktail)

    suspend fun getAllFavoriteCocktails(): Flow<List<FavoriteCocktail>>
}