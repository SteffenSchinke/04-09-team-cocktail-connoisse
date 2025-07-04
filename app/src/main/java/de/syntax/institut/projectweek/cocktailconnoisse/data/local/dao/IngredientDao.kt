package de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Upsert
    suspend fun upsertIngredients(ingredients: List<Ingredient>)

    @Query("SELECT COUNT(*) FROM ingredient")
    fun getIngredientCount(): Flow<Int>


    // cash clear & reset primary key
    @Query("DELETE FROM ingredient")
    suspend fun clearIngredient()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'ingredient'")
    suspend fun resetIngredientPrimaryKey()

    suspend fun truncateIngredients() {
        clearIngredient()
        resetIngredientPrimaryKey()
    }
}