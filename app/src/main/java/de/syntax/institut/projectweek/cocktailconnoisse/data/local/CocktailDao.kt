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
    fun insertFavoritedCocktail(cocktail: Cocktail)

    @Update
    fun updateFavoritedCocktail(cocktail: Cocktail)

    @Query("DELETE FROM cocktail")
    fun deleteAllFavoritedCocktails()

    @Delete
    fun deleteFavoritedCocktail(cocktail: Cocktail)

    @Query("SELECT * FROM cocktail")
    fun getAllFavoritedCocktails(): Flow<List<Cocktail>>
}