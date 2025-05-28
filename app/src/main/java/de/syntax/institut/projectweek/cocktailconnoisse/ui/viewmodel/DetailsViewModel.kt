package de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import de.schinke.steffen.base_classs.AppBaseViewModelAndroid
import de.schinke.steffen.enums.ViewModelState
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.ApiError
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.data.repository.CocktailRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(

    application: Application,
    private val cocktailRepo: CocktailRepositoryInterface
) : AppBaseViewModelAndroid<ViewModelState>(application, ViewModelState.READY) {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _apiError = MutableStateFlow<ApiError?>(null)
    val apiError: StateFlow<ApiError?> = _apiError

    val isFavorited: StateFlow<Boolean> = _cocktail
        .filterNotNull()
        .map { it.favorited }
        .stateIn( viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadCocktailById(id: String) {

        if (state.value != ViewModelState.READY) return

        setState { ViewModelState.WORKING }

        viewModelScope.launch {

            try {
                cocktailRepo.getCocktailById(id).collect { cocktail ->
                    _cocktail.value = cocktail
                }

                setState { ViewModelState.READY }
            } catch (e: ApiError) {

                _apiError.value = e
                setState { ViewModelState.ERROR }
            }
        }
    }

    fun updateIsFavorited(cocktail: Cocktail) {

        _cocktail.value?.let {
            viewModelScope.launch {

                val newCocktail = cocktail.copy(favorited = !cocktail.favorited)
                cocktailRepo.updateCachedCocktail(newCocktail)
                _cocktail.value = newCocktail
            }
        }
    }


    fun resetApiError() { _apiError.value = null }
}