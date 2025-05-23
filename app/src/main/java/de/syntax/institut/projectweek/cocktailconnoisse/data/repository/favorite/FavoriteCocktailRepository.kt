package de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite

import de.syntax.institut.projectweek.cocktailconnoisse.data.local.FavoriteCocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoriteCocktail
import kotlinx.coroutines.flow.Flow

class FavoriteCocktailRepository(

    private val favoriteCocktailDao: FavoriteCocktailDao
): FavoriteCocktailRepositoryInterface {

    override suspend fun insertFavoriteCocktail(favoriteCocktail: FavoriteCocktail) {

        favoriteCocktailDao.insertFavoriteCocktail(favoriteCocktail)
    }

    override suspend fun deleteFavoriteCocktail(favoriteMeal: FavoriteCocktail) {

        favoriteCocktailDao.deleteFavoriteCocktail(favoriteMeal)
    }

    override suspend fun getAllFavoriteCocktails(): Flow<List<FavoriteCocktail>> {

        return favoriteCocktailDao.getAllFavoriteCocktails()
    }


}