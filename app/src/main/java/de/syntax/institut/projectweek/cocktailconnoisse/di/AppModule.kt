package de.syntax.institut.projectweek.cocktailconnoisse.di

import android.util.Log
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.FavoriteCocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.FavoriteCocktailDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail.CocktailRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite.FavoriteCocktailRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite.FavoriteCocktailRepositoryInterface
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

    single<FavoriteCocktailDao> {
        Log.d("KoinModule", "FavoriteCocktailDao")
        FavoriteCocktailDatabase.getDatabase(get()).favoriteCocktailDao()
    }

    single<FavoriteCocktailRepositoryInterface> {
        Log.d("KoinModule", "FavoriteCocktailRepositoryInterface")
        FavoriteCocktailRepository(get())
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
