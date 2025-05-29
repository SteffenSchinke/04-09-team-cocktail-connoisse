package de.syntax.institut.projectweek.cocktailconnoisse.data.repository

import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import de.syntax.institut.projectweek.cocktailconnoisse.enum.CocktailType
import kotlinx.coroutines.flow.Flow

interface CocktailRepositoryInterface {

    fun getRandomCocktail(): Flow<Cocktail?>

    fun getCocktailsByType(type: String): Flow<List<Cocktail>>

    fun getCocktailById(id: String): Flow<Cocktail?>

//    fun getCocktailsByName(name: String): Flow<List<Cocktail>>

//    fun getAllCategories(): Flow<List<Category>>

    fun getCocktailsByCategory(category: String): Flow<List<Cocktail>>

//    fun getCocktailsByIngredient(ingredient: String): Flow<List<Cocktail>>


    suspend fun insertCachedCocktailWithIngredients(
        cocktail: Cocktail,
        ingredients: List<Ingredient>
    )

// fun getCachedCocktails(): Flow<List<Cocktail>>

//    fun getCachedCocktailById(id: String): Flow<Cocktail?>

//    suspend fun deleteCachedCocktail(favoritedCocktail: Cocktail)

//    suspend fun updateCachedCocktail(cocktail: Cocktail)

//    fun getAllFavorites(): Flow<List<Cocktail>>

//    suspend fun truncateCache()
//
//    fun isCacheEmpty(): Flow<Boolean>




    // new



    suspend fun getCocktails(listIds: List<Long>): Flow<List<Cocktail>>

    suspend fun getCocktails(count: Long): Flow<List<Cocktail>>

    suspend fun getCocktails(type: CocktailType): Flow<List<Cocktail>>

    suspend fun getCocktails(name: String): Flow<List<Cocktail>>

    suspend fun getCocktails(category: Category): Flow<List<Cocktail>>

    suspend fun getCocktail(id: Long): Flow<Cocktail?>


    fun getFavorites(): Flow<List<Cocktail>>

    fun getCategories(): Flow<List<Category>>


    suspend fun deleteCocktail(cocktail: Cocktail)

    suspend fun updateCocktail(cocktail: Cocktail)

}