package de.syntax.institut.projectweek.cocktailconnoisse.data.local.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.Flow

class CocktailDBRepository(

    private val cocktailDao: CocktailDao
): CocktailDBRepositoryInterface {

    override suspend fun insertFavoritedCocktail(cocktail: Cocktail) {

        cocktailDao.insertFavoritedCocktail(cocktail)
    }

    override suspend fun deleteFavoritedCocktail(cocktail: Cocktail) {

        cocktailDao.deleteFavoritedCocktail(cocktail)
    }

    override suspend fun getAllFavoritedCocktails(): Flow<List<Cocktail>> {

        return cocktailDao.getAllFavoritedCocktails()
    }


}