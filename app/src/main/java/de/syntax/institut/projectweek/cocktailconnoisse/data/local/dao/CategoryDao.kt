package de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCategories(category: List<Category>)

    @Query("SELECT * FROM category")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM category")
    fun getCategoryCount(): Flow<Int>


    // cash clear & reset primary key
    @Query("DELETE FROM category")
    suspend fun clearCategory()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'category'")
    suspend fun resetCategoryPrimaryKey()

    suspend fun truncateCategory() {
        clearCategory()
        resetCategoryPrimaryKey()
    }
}