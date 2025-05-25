package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoritedCocktail

@Database(entities = [FavoritedCocktail::class], version = 1, exportSchema = false)
abstract class FavoritedCocktailDatabase : RoomDatabase() {

    abstract fun favoritedCocktailDao(): FavoritedCocktailDao

    companion object {
        @Volatile
        private var instance: FavoritedCocktailDatabase? = null

        fun getDatabase(context: Context): FavoritedCocktailDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FavoritedCocktailDatabase::class.java,
                    "app_cocktails_db"
                ).build().also { instance = it }
            }
        }
    }
}