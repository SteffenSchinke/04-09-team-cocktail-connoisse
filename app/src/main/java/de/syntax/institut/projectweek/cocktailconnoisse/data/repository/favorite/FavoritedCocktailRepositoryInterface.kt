package de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite

import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoritedCocktail
import kotlinx.coroutines.flow.Flow

interface FavoritedCocktailRepositoryInterface {

    suspend fun insertFavoritedCocktail(favoritedCocktail: FavoritedCocktail)

    suspend fun deleteFavoritedCocktail(favoriteMeal: FavoritedCocktail)

    suspend fun getAllFavoritedCocktails(): Flow<List<FavoritedCocktail>>
}