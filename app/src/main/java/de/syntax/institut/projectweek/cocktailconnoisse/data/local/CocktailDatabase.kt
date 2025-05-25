package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail

@Database(entities = [Cocktail::class], version = 1, exportSchema = false)
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao

    companion object {
        @Volatile
        private var instance: CocktailDatabase? = null

        fun getDatabase(context: Context): CocktailDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CocktailDatabase::class.java,
                    "app_cocktails_db"
                ).build().also { instance = it }
            }
        }
    }
}