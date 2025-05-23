package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.model.FavoriteCocktail

@Database(entities = [FavoriteCocktail::class], version = 1, exportSchema = false)
abstract class FavoriteCocktailDatabase : RoomDatabase() {

    abstract fun favoriteCocktailDao(): FavoriteCocktailDao

    companion object {
        @Volatile
        private var instance: FavoriteCocktailDatabase? = null

        fun getDatabase(context: Context): FavoriteCocktailDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, FavoriteCocktailDatabase::class.java, "favorite_cocktail_db"
                ).build().also { instance = it }
            }
        }
    }
}