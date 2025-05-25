package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.cocktail.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.favorite.FavoriteCocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.enum.CocktailType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CocktailsViewModel(

    application: Application,
    private val cocktailRepository: CocktailRepositoryInterface,
    private val favoriteCocktailRepository: FavoriteCocktailRepositoryInterface,
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    // TODO sts 23.05.25 - implement viewmodel database for persistence of cocktails

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    private val _withAlcoholic = MutableStateFlow(true)
    val withAlcoholic: StateFlow<Boolean> = _withAlcoholic

    private val _randomCocktail = MutableStateFlow<Cocktail?>(null)
    val randomCocktail: StateFlow<Cocktail?> = _randomCocktail

    private val _randomCocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val randomCocktails: StateFlow<List<Cocktail>> = _randomCocktails

    fun loadCocktails() {

        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        viewModelScope.launch {

            try {

                cocktailRepository.getRandomCocktail().collect { cocktail ->
                    _randomCocktail.value = cocktail
                }

                cocktailRepository.getCocktails(
                    if (withAlcoholic.value) CocktailType.ALCOHOLIC.label
                    else CocktailType.NON_ALCOHOLIC.label
                ).collect { cocktails ->
                    _randomCocktails.value = cocktails
                }

                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun setCocktailType(newValue: Boolean) {

        _withAlcoholic.value = newValue
        loadCocktails()
    }

    fun resetApiError() {
        _apiError.value = null
    }
}