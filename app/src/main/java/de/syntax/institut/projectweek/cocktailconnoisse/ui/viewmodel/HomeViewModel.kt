package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import de.syntax.institut.projectweek.cocktailconnoisse.enum.CocktailType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    // TODO sts 23.05.25 - implement viewmodel database for persistence of cocktails

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    private val _cocktailType = MutableStateFlow(CocktailType.ALCOHOLIC)
    val cocktailType: StateFlow<CocktailType> = _cocktailType

    private val _randomCocktail = MutableStateFlow<Cocktail?>(null)
    val randomCocktail: StateFlow<Cocktail?> = _randomCocktail

    private val _randomCocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val randomCocktails: StateFlow<List<Cocktail>> = _randomCocktails

    val withAlcoholic: StateFlow<Boolean> = cocktailType
        .map { it == CocktailType.ALCOHOLIC }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _cocktailType.value == CocktailType.ALCOHOLIC
        )

    private val _updatedCocktail = MutableStateFlow<Cocktail?>(null)
    val updatedCocktail: StateFlow<Cocktail?> = _updatedCocktail

    fun updateFavorite(cocktail: Cocktail) {
        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        try {
            viewModelScope.launch {
                val newCocktail = cocktailRepo.getCocktailById(cocktail.id.toString())
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
                newCocktail.value?.let {
                    it.favorited = !cocktail.favorited
                    cocktailRepo.updateCachedCocktail(it)
                    setState { ViewModelState.READY }
                    _updatedCocktail.value = it
                }
                setState { ViewModelState.READY }
            }
        } catch (e: ApiError) {

            _apiError.value = e
            setState { ViewModelState.ERROR }
        }
    }

    fun loadCocktails() {

        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        viewModelScope.launch {

            try {

                cocktailRepo.getRandomCocktail().collect { cocktail ->
                    _randomCocktail.value = cocktail
                }

                cocktailRepo.getCocktailsByType(
                    if (withAlcoholic.value) CocktailType.ALCOHOLIC.label
                    else CocktailType.NON_ALCOHOLIC.label
                ).collect { cocktails ->
                    _randomCocktails.value = cocktails
                }

                Log.d("HomeViewModel", "loadCocktails() ready")
                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun setCocktailType(newValue: Boolean) {

        if (newValue) _cocktailType.value = CocktailType.ALCOHOLIC
        else _cocktailType.value = CocktailType.NON_ALCOHOLIC

        loadCocktails()
    }

    fun resetApiError() {
        _apiError.value = null
    }


}