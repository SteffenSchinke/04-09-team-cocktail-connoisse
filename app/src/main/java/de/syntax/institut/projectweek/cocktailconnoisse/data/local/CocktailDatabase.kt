package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao.CategoryDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.dao.IngredientDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [Cocktail::class, Ingredient::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao
    abstract fun categoryDao(): CategoryDao
    abstract fun ingredientDao(): IngredientDao

    companion object {
        @Volatile
        private var instance: CocktailDatabase? = null

        @OptIn(DelicateCoroutinesApi::class)
        fun getDatabase(context: Context): CocktailDatabase {
            return instance ?: synchronized(this) {
                Room
                .databaseBuilder(
                    context.applicationContext,
                    CocktailDatabase::class.java,
                    "app_cocktails_db"
                )
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        db.execSQL("PRAGMA foreign_keys=ON")
                    }
                })
                .build().also { db ->
                    instance = db

                    GlobalScope.launch {
                        val prefs =
                            context.getSharedPreferences("init_flags", Context.MODE_PRIVATE)
                        if (!prefs.getBoolean("db_initialized", false)) {
                            db.cocktailDao().truncateCocktail()
                            db.ingredientDao().truncateIngredients()
                            db.categoryDao().truncateCategory()
                            prefs.edit { putBoolean("db_initialized", true) }
                        }
                    }
                }
            }
        }
    }
}