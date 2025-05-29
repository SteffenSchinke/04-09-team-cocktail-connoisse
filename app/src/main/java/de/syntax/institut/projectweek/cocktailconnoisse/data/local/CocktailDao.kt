package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.CocktailWithIngredients
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {

    @Update
    suspend fun updateCocktail(cocktail: Cocktail)

    @Delete
    suspend fun deleteCocktail(cocktail: Cocktail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedCocktail(cocktail: Cocktail)

    @Transaction
    @Query("SELECT * FROM cocktail")
    fun getCachedCocktails(): Flow<List<CocktailWithIngredients>>

    @Transaction
    @Query("SELECT * FROM cocktail where id = :id")
    fun getCachedCocktailById(id: String): Flow<CocktailWithIngredients?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedIngredients(ingredients: List<Ingredient>)

    @Query("DELETE FROM ingredient")
    suspend fun clearCachedIngredients()

    @Query("DELETE FROM cocktail")
    suspend fun clearCachedCocktails()

    @Query("SELECT COUNT(*) FROM cocktail")
    fun getCocktailCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ingredient")
    fun getIngredientCount(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM cocktail WHERE favorited = 1")
    fun getFavorites(): Flow<List<CocktailWithIngredients>>

    @Transaction
    suspend fun insertCachedCocktailWithIngredients(
        cocktail: Cocktail,
        ingredients: List<Ingredient>
    ) {
        insertCachedCocktail(cocktail)
        insertCachedIngredients(ingredients.map { it.copy(cocktailId = cocktail.id) })
    }

    @Transaction
    suspend fun truncateCache() {
        clearCachedIngredients()
        clearCachedCocktails()
    }

    // mit flow und ohne suspend weil ein Flow schon asynchron ist
    @Transaction
    @Query("SELECT * FROM cocktail WHERE id = :cocktailId")
    suspend fun getCocktailWithIngredients(cocktailId: Long): CocktailWithIngredients

    // mit flow und ohne suspend weil ein Flow schon asynchron ist
    @Transaction
    @Query("SELECT * FROM cocktail")
    suspend fun getAllCocktailsWithIngredients(): List<CocktailWithIngredients>



    // new



    @Transaction
    @Query("SELECT * FROM cocktail where id = :id")
    fun getCocktailById(id: String): Flow<CocktailWithIngredients?>
}