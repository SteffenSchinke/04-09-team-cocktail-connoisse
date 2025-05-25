package de.syntax.institut.projectweek.cocktailconnoisse.di

import android.util.Log
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.repository.CocktailApiRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.repository.CocktailApiRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.repository.CocktailDBRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.repository.CocktailDBRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CategoryViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CocktailsViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.DetailsViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.FavoritesViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private const val API_BASE_URL_COCKTAIL = "https://www.thecocktaildb.com/api/json/v1/1/"

val appModule = module {

    Log.d("KoinModule", "start AppModule")

    single<CocktailDao> {
        Log.d("KoinModule", "CocktailDao")
        CocktailDatabase.getDatabase(get()).cocktailDao()
    }

    single<CocktailDBRepositoryInterface> {
        Log.d("KoinModule", "FavoritedCocktailRepositoryInterface")
        CocktailDBRepository(get())
    }

    single<ApiCocktail> {
        ApiCocktail(API_BASE_URL_COCKTAIL)
    }

    single<CocktailApiRepositoryInterface> {
        Log.d("KoinModule", "CocktailRepositoryInterface")
        CocktailApiRepository(get())
    }

    Log.d("KoinModule", "CocktailsViewModel")
    viewModelOf(::CocktailsViewModel)

    Log.d("KoinModule", "CategoryViewModel")
    viewModelOf(::CategoryViewModel)

    Log.d("KoinModule", "FavoritesViewModel")
    viewModelOf(::FavoritesViewModel)

    Log.d("KoinModule", "DetailsViewModel")
    viewModelOf(::DetailsViewModel)

    Log.d("KoinModule", "SettingsViewModel")
    viewModelOf(::SettingsViewModel)

    Log.d("KoinModule", "end AppModule")

}
