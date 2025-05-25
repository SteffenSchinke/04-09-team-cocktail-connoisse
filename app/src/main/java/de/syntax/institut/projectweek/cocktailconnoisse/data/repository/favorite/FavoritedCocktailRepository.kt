package de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite

import de.syntax.institut.projectweek.cocktailconnoisse.data.local.FavoritedCocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoritedCocktail
import kotlinx.coroutines.flow.Flow

class FavoritedCocktailRepository(

    private val favoritedCocktailDao: FavoritedCocktailDao
): FavoritedCocktailRepositoryInterface {

    override suspend fun insertFavoritedCocktail(favoritedCocktail: FavoritedCocktail) {

        favoritedCocktailDao.insertFavoritedCocktail(favoritedCocktail)
    }

    override suspend fun deleteFavoritedCocktail(favoriteMeal: FavoritedCocktail) {

        favoritedCocktailDao.deleteFavoritedCocktail(favoriteMeal)
    }

    override suspend fun getAllFavoritedCocktails(): Flow<List<FavoritedCocktail>> {

        return favoritedCocktailDao.getAllFavoritedCocktails()
    }


}