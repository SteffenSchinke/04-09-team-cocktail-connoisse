package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertFavoritedCocktail(cocktail: Cocktail)

    @Update
    suspend fun updateFavoritedCocktail(cocktail: Cocktail)

    @Query("DELETE FROM cocktail")
    suspend fun deleteAllFavoritedCocktails()

    @Delete
    suspend fun deleteFavoritedCocktail(cocktail: Cocktail)

    @Query("SELECT * FROM cocktail")
    fun getAllFavoritedCocktails(): Flow<List<Cocktail>>
}