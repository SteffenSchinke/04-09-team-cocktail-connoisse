package de.syntax.institut.projectweek.cocktailconnoisse.di

import android.util.Log
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CategoriesViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CocktailsViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.HomeViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.DetailsViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.FavoritesViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SettingsViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SuggestionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private const val API_BASE_URL_COCKTAIL = "https://www.thecocktaildb.com/api/json/v1/1/"

val appModule = module {

    Log.d("KoinModule", "start AppModule")

    single<ApiCocktail> {
        ApiCocktail(API_BASE_URL_COCKTAIL)
    }

    single<CocktailDao> {
        Log.d("KoinModule", "CocktailDao")
        CocktailDatabase.getDatabase(get()).cocktailDao()
    }

    single<CocktailRepositoryInterface> {
        Log.d("KoinModule", "FavoritedCocktailRepositoryInterface")
        CocktailRepository(get(), get())
    }

    Log.d("KoinModule", "HomeViewModel")
    viewModelOf(::HomeViewModel)

    Log.d("KoinModule", "CocktailsViewModel")
    viewModelOf(::CocktailsViewModel)

    Log.d("KoinModule", "CategoryViewModel")
    viewModelOf(::CategoriesViewModel)

    Log.d("KoinModule", "FavoritesViewModel")
    viewModelOf(::FavoritesViewModel)

    Log.d("KoinModule", "DetailsViewModel")
    viewModelOf(::DetailsViewModel)

    Log.d("KoinModule", "SuggestionsViewModel")
    viewModelOf(::SuggestionsViewModel)

    Log.d("KoinModule", "SettingsViewModel")
    viewModelOf(::SettingsViewModel)

    Log.d("KoinModule", "end AppModule")

}
