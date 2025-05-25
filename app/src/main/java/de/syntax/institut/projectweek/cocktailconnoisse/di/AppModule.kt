package de.syntax.institut.projectweek.cocktailconnoisse.di

import android.util.Log
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.FavoritedCocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.FavoritedCocktailDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail.CocktailRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite.FavoritedCocktailRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite.FavoritedCocktailRepositoryInterface
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

    single<FavoritedCocktailDao> {
        Log.d("KoinModule", "FavoritedCocktailDao")
        FavoritedCocktailDatabase.getDatabase(get()).favoritedCocktailDao()
    }

    single<FavoritedCocktailRepositoryInterface> {
        Log.d("KoinModule", "FavoritedCocktailRepositoryInterface")
        FavoritedCocktailRepository(get())
    }

    single<ApiCocktail> {
        ApiCocktail(API_BASE_URL_COCKTAIL)
    }

    single<CocktailRepositoryInterface> {
        Log.d("KoinModule", "CocktailRepositoryInterface")
        CocktailRepository(get())
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
