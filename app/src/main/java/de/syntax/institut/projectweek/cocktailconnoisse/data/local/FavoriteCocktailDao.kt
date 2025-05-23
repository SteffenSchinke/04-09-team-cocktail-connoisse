package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoriteCocktail
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCocktailDao {

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertFavoriteCocktail(favoriteCocktail: FavoriteCocktail)

    @Update
    suspend fun updateFavoriteCocktail(favoriteCocktail: FavoriteCocktail)

    @Query("DELETE FROM favorite_cocktail")
    suspend fun deleteAllFavoriteCocktails()

    @Delete
    suspend fun deleteFavoriteCocktail(favoriteMeal: FavoriteCocktail)

    @Query("SELECT * FROM favorite_cocktail")
    fun getAllFavoriteCocktails(): Flow<List<FavoriteCocktail>>
}