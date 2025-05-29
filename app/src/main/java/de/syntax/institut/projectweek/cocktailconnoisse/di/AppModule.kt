package de.syntax.institut.projectweek.cocktailconnoisse.di

import android.util.Log
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiCocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CategoryDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDao
import de.syntax.institut.projectweek.cocktailconnoisse.data.local.CocktailDatabase
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepository
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CategoriesViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CocktailsViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.HomeViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.DetailsViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.FavoriteSwitchViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.FavoritesViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModel
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

    single<CategoryDao> {
        Log.d("KoinModule", "CategoryDao")
        CocktailDatabase.getDatabase(get()).categoryDao()
    }

    single<CocktailRepositoryInterface> {
        Log.d("KoinModule", "FavoritedCocktailRepositoryInterface")
        CocktailRepository(get(), get(), get())
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

    Log.d("KoinModule", "SettingsViewModel")
    viewModelOf(::SettingsViewModel)

    Log.d("KoinModule", "FavoriteSwitchViewModel")
    viewModel { (cocktailId: Long) ->

        Log.d("KoinModule", "Creating FSwitchVM for ID: $cocktailId. Koin instance: ${this.hashCode()}")

        FavoriteSwitchViewModel(
            application = get(),
            cocktailRepo = get(),
            cocktailId = cocktailId
        )
    }


    Log.d("KoinModule", "end AppModule")
}
