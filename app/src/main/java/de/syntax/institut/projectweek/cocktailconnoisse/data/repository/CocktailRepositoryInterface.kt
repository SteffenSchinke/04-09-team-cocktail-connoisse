package de.syntax.institut.projectweek.cocktailconnoisse.data.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.enum.CocktailType
import kotlinx.coroutines.flow.Flow

interface CocktailRepositoryInterface {

    suspend fun insertCocktail(cocktail: Cocktail)

    suspend fun deleteCocktail(cocktail: Cocktail)

    suspend fun updateCocktail(cocktail: Cocktail)

    fun getFavorites(): Flow<List<Cocktail>>

    fun getCategories(): Flow<List<Category>>

    fun getCocktail(): Flow<Cocktail?>

    fun getCocktail(id: Long): Flow<Cocktail?>

    fun getCocktails(listIds: List<Long>): Flow<List<Cocktail>>

    fun getCocktails(count: Int): Flow<List<Cocktail>>

    fun getCocktails(type: CocktailType): Flow<List<Cocktail>>

    fun getCocktails(count: Int, type: CocktailType): Flow<List<Cocktail>>

    fun getCocktails(name: String): Flow<List<Cocktail>>

    fun getCocktails(category: Category): Flow<List<Cocktail>>
}