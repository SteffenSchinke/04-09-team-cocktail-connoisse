package de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.CocktailWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {

    @Upsert
    suspend fun upsertCocktail(cocktail: Cocktail)

    @Delete
    suspend fun deleteCocktail(cocktail: Cocktail)

    @Transaction
    @Query("SELECT * FROM cocktail where id = :id")
    fun getCocktail(id: Long): Flow<CocktailWithIngredients?>

    @Transaction
    @Query("SELECT * FROM cocktail where name = :name")
    fun getCocktailByName(name: String): Flow<CocktailWithIngredients?>

    @Transaction
    @Query("SELECT * FROM cocktail")
    fun getCocktails(): Flow<List<CocktailWithIngredients>>

    @Transaction
    @Query("SELECT * FROM cocktail LIMIT :limit")
    fun getCocktails(limit: Int): Flow<List<CocktailWithIngredients>>

    @Transaction
    @Query("SELECT * FROM cocktail where category = :category")
    fun getCocktailsByCategory(category: String): Flow<List<CocktailWithIngredients>>

    @Transaction
    @Query("SELECT * FROM cocktail WHERE favorited = 1")
    fun getFavorites(): Flow<List<CocktailWithIngredients>>

    @Query("SELECT COUNT(*) FROM cocktail")
    fun getCocktailCount(): Flow<Int>


    // cash clear & reset primary key
    @Query("DELETE FROM cocktail")
    suspend fun clearCocktail()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'cocktail'")
    suspend fun resetCocktailPrimaryKey()

    suspend fun truncateCocktail() {
        clearCocktail()
        resetCocktailPrimaryKey()
    }
}