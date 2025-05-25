package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite.FavoritedCocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(

    private val cocktailRepository: CocktailRepositoryInterface,
    private val favoritedCocktailRepository: FavoritedCocktailRepositoryInterface,
    application: Application
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    fun loadCocktailById(id: String) {

        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        viewModelScope.launch {

            try {
                cocktailRepository.getCocktailById(id).collect { cocktail ->
                    _cocktail.value = cocktail
                }

                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun resetApiError() { _apiError.value = null }
}