package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(category: List<Category>)

    @Update
    suspend fun updateCategory(cocktail: Cocktail)

    @Delete
    suspend fun deleteCategory(cocktail: Cocktail)

    @Query("SELECT * FROM category")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM category")
    fun getCategoryCount(): Flow<Int>

    @Query("DELETE FROM category")
    suspend fun clearCache()
}