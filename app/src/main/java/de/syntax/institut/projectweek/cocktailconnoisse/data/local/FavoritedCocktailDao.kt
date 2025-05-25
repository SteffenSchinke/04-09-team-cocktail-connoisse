package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoritedCocktail
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritedCocktailDao {

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertFavoritedCocktail(favoritedCocktail: FavoritedCocktail)

    @Update
    suspend fun updateFavoritedCocktail(favoritedCocktail: FavoritedCocktail)

    @Query("DELETE FROM favorited_cocktail")
    suspend fun deleteAllFavoritedCocktails()

    @Delete
    suspend fun deleteFavoritedCocktail(favoritedCocktail: FavoritedCocktail)

    @Query("SELECT * FROM favorited_cocktail")
    fun getAllFavoritedCocktails(): Flow<List<FavoritedCocktail>>
}