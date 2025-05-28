package de.syntax.institut.projectweek.cocktailconnoisse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.content.edit
import kotlinx.coroutines.DelicateCoroutinesApi

@Database(entities = [Cocktail::class, Ingredient::class], version = 1, exportSchema = false)
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao

    companion object {
        @Volatile
        private var instance: CocktailDatabase? = null

        @OptIn(DelicateCoroutinesApi::class)
        fun getDatabase(context: Context): CocktailDatabase {
            return instance ?:
                synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        CocktailDatabase::class.java,
                        "app_cocktails_db"
                    )
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                db.execSQL("PRAGMA foreign_keys=ON")
                            }

                            override fun onOpen(db: SupportSQLiteDatabase) {
                                super.onOpen(db)
                                db.execSQL("PRAGMA foreign_keys=ON")
                            }
                        })
                    .build().also { db ->
                            instance = db

                            GlobalScope.launch {
                                val prefs = context.getSharedPreferences("init_flags", Context.MODE_PRIVATE)
                                if (!prefs.getBoolean("db_initialized", false)) {
                                    db.cocktailDao().clearCachedCocktails()
                                    db.cocktailDao().clearCachedIngredients()
                                    prefs.edit { putBoolean("db_initialized", true) }
                                }
                            }
                        }
            }
        }
    }
}